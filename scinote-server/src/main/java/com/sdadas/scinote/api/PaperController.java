package com.sdadas.scinote.api;

import com.sdadas.scinote.repos.ReposService;
import com.sdadas.scinote.repos.shared.model.Paper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
@RestController
public class PaperController {

    private final ReposService service;

    @Autowired
    public PaperController(ReposService service) {
        this.service = service;
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Paper> search(@RequestParam String q) {
        return service.query(q);
    }
}
