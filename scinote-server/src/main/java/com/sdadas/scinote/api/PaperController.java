package com.sdadas.scinote.api;

import com.sdadas.scinote.repos.ReposService;
import com.sdadas.scinote.repos.parse.PaperParserService;
import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.shared.model.paper.Paper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        return service.query(q);
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
}
