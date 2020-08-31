package com.sdadas.scinote.repos.parse;

import com.sdadas.scinote.repos.parse.grobid.GrobidPaperParserService;
import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.repos.parse.spv2.Spv2PaperParserService;
import com.sdadas.scinote.shared.FilesConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Sławomir Dadas
 */
@Service
public class PaperParserServiceImpl implements PaperParserService {

    private final PaperParserService backend;

    @Autowired
    public PaperParserServiceImpl(PaperParserConfig config, FilesConfig filesConfig) {
        this.backend = createBackend(config, filesConfig);
    }

    private PaperParserService createBackend(PaperParserConfig config, FilesConfig filesConfig) {
        String grobidHome = config.getGrobidHome();
        if(StringUtils.isNotBlank(grobidHome) && Files.exists(Path.of(grobidHome))) {
            return new GrobidPaperParserService(config, filesConfig);
        } else {
            return new Spv2PaperParserService(config, filesConfig);
        }
    }

    @Override
    public ParseResponse parse(ParseRequest request) {
        return backend.parse(request);
    }

    @Override
    public boolean serviceAvailable() {
        return backend.serviceAvailable();
    }
}
