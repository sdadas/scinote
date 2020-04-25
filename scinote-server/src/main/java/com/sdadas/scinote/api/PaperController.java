package com.sdadas.scinote.api;

import com.sdadas.scinote.repos.ReposService;
import com.sdadas.scinote.repos.parse.PaperParserService;
import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@CrossOrigin
@RestController
public class PaperController {

    private final ReposService service;

    @Autowired
    public PaperController(ReposService service, PaperParserService parser) {
        this.service = service;
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Paper> search(@RequestParam String q) {
        return service.papersByQuery(q);
    }

    @PostMapping(path = "/papers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Paper> papers(@RequestBody List<String> id) {
        List<PaperId> paperIds = id.stream().map(val -> PaperId.fromString(val, ",")).collect(Collectors.toList());
        return service.papersByIds(paperIds);
    }

    @PostMapping(path = "/parse", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParseResponse> parse(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();
        Resource resource = file.getResource();
        String filename = file.getOriginalFilename();
        ParseRequest request = new ParseRequest(filename, resource);
        ParseResponse response = service.parse(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/parse/available", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean parseServiceAvailable() {
        return service.parseServiceAvailable();
    }
}
