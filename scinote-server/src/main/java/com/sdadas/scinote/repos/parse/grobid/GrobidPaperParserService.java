package com.sdadas.scinote.repos.parse.grobid;

import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.sdadas.scinote.repos.parse.PaperParserConfig;
import com.sdadas.scinote.repos.parse.PaperParserService;
import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.grobid.core.data.BiblioItem;
import org.grobid.core.engines.Engine;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.utilities.GrobidProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Sławomir Dadas
 */
public class GrobidPaperParserService implements PaperParserService {

    private final static Logger LOG = LoggerFactory.getLogger(GrobidPaperParserService.class);

    private final PaperParserConfig config;

    private Engine engine;

    private volatile boolean serviceAvailable;

    public GrobidPaperParserService(PaperParserConfig config) {
        this.config = config;
        ForkJoinPool.commonPool().execute(this::initGrobidEngine);
    }

    private void initGrobidEngine() {
        try {
            LOG.info("Initializing Grobid engine");
            File grobidHome = new File(System.getProperty("user.home"), "grobid-home");
            List<String> homePaths = Collections.singletonList(grobidHome.getAbsolutePath());
            GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(homePaths);
            GrobidProperties.getInstance(grobidHomeFinder);
            this.engine = GrobidFactory.getInstance().createEngine();
            this.serviceAvailable = true;
            LOG.info("Finished initialization of Grobid engine");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ParseResponse parse(ParseRequest request) {
        ParseResponse response = request.createResponse();
        if(StringUtils.isNotBlank(response.getError())) return response;
        try {
            String paperId = RandomStringUtils.randomAlphanumeric(32);
            File file = getFileFromResource(request.getResource(), paperId);
            BiblioItem item = new BiblioItem();
            engine.processHeader(file.getAbsolutePath(), 1, item);
            GrobidPaperBuilder builder = new GrobidPaperBuilder(item, request.getFilename(), paperId);
            response.setPaper(builder.createPaper());
        } catch (IOException e) {
            response.setError("Failed to parse PDF");
        }
        return response;
    }

    private File getFileFromResource(Resource resource, String paperId) throws IOException {
        if (resource instanceof FileSystemResource) {
            return resource.getFile();
        } else {
            String filename = paperId + ".pdf";
            File output = new File(config.getStorageDir(), filename);
            try(InputStream is = resource.getInputStream()) {
                FileUtils.forceMkdirParent(output);
                ByteSink sink = Files.asByteSink(output);
                sink.writeFrom(is);
                return output;
            }
        }
    }

    @Override
    public boolean serviceAvailable() {
        return serviceAvailable;
    }
}
