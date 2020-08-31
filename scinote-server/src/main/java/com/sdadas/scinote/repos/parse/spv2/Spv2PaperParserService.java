package com.sdadas.scinote.repos.parse.spv2;

import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.sdadas.scinote.repos.parse.PaperParserConfig;
import com.sdadas.scinote.repos.parse.PaperParserService;
import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.repos.parse.spv2.model.Spv2Response;
import com.sdadas.scinote.repos.parse.spv2.rest.Spv2RestClient;
import com.sdadas.scinote.repos.shared.exception.ExternalServiceException;
import com.sdadas.scinote.shared.FilesConfig;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.WebLocation;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Sławomir Dadas
 */
public class Spv2PaperParserService implements PaperParserService {

    private final static Logger LOG = LoggerFactory.getLogger(Spv2PaperParserService.class);

    private final PaperParserConfig config;

    private final FilesConfig filesConfig;

    private final Spv2RestClient client;

    private volatile boolean serviceAvailable;

    public Spv2PaperParserService(PaperParserConfig config, FilesConfig filesConfig) {
        this.config = config;
        this.filesConfig = filesConfig;
        this.client = new Spv2RestClient(config.getSpv2Url());
        ForkJoinPool.commonPool().execute(() -> checkServiceAvailable(config.getSpv2Url()));
    }

    public void checkServiceAvailable(String url) {
        if(StringUtils.isBlank(url)) {
            this.serviceAvailable = false;
        } else {
            LOG.info("Checking availability of {}", url);
            try {
                URI uri = new URI(url);
                String host = uri.getHost();
                if(StringUtils.isNotBlank(host)) {
                    InetAddress address = InetAddress.getByName(host);
                    this.serviceAvailable = address.isReachable(30000);
                } else {
                    this.serviceAvailable = false;
                }
            } catch (URISyntaxException | IOException e) {
                this.serviceAvailable = false;
            }
        }
    }

    @Override
    public ParseResponse parse(ParseRequest request) {
        ParseResponse response = request.createResponse();
        if(StringUtils.isNotBlank(response.getError())) return response;
        Resource resource = request.getResource();
        try {
            Spv2Response result = this.client.parse(resource);
            Paper paper = new Spv2PaperBuilder(result, request.getFilename()).createPaper();
            response.setPaper(paper);
            store(resource, paper);
        } catch (ExternalServiceException ex) {
            response.setError("Failed to parse PDF");
        }
        return response;
    }

    @Override
    public boolean serviceAvailable() {
        if(StringUtils.isBlank(config.getSpv2Url())) {
            return false;
        } else {
            return serviceAvailable;
        }
    }

    private void store(Resource resource, Paper paper) {
        String filename = RandomStringUtils.randomAlphanumeric(32) + ".pdf";
        File output = new File(filesConfig.getStorageDir(), filename);
        ForkJoinPool.commonPool().submit(() -> {
            try(InputStream is = resource.getInputStream()) {
                FileUtils.forceMkdirParent(output);
                ByteSink sink = Files.asByteSink(output);
                sink.writeFrom(is);
            } catch (IOException e) {
                LOG.error("Writing file failed", e);
            }
        });
        paper.addUrl(new WebLocation("pdf", "/pdf/" + filename));
    }
}
