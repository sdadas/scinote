package com.sdadas.scinote.repos.parse;

import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.repos.parse.model.Spv2Response;
import com.sdadas.scinote.repos.parse.rest.Spv2RestClient;
import com.sdadas.scinote.repos.shared.exception.ExternalServiceException;
import com.sdadas.scinote.repos.shared.model.Paper;
import com.sdadas.scinote.repos.shared.model.WebLocation;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Sławomir Dadas
 */
@Service
public class PaperParserServiceImpl implements PaperParserService {

    private final static Logger LOG = LoggerFactory.getLogger(PaperParserServiceImpl.class);

    private final PaperParserConfig config;

    private final Spv2RestClient client;

    @Autowired
    public PaperParserServiceImpl(PaperParserConfig config) {
        this.config = config;
        this.client = new Spv2RestClient(config.getUrl());
    }

    @Override
    public ParseResponse parse(ParseRequest request) {
        ParseResponse response = validate(request);
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

    private ParseResponse validate(ParseRequest request) {
        ParseResponse response = new ParseResponse();
        String filename = request.getFilename();
        if(!StringUtils.endsWithIgnoreCase(filename, ".pdf")) {
            response.setError("Only PDF files are supported");
        }
        return response;
    }

    private void store(Resource resource, Paper paper) {
        String filename = RandomStringUtils.randomAlphanumeric(32) + ".pdf";
        File output = new File(config.getStorageDir(), filename);
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
