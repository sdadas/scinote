package com.sdadas.scinote.api;

import com.sdadas.scinote.bibtex.BibtexService;
import com.sdadas.scinote.project.ProjectService;
import com.sdadas.scinote.repos.ReposService;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import com.sdadas.scinote.shared.model.project.Project;
import com.sdadas.scinote.shared.model.project.ProjectPaper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@RestController
public class BibtextController {

    private final BibtexService bibtex;

    private final ReposService repos;

    private final ProjectService project;

    @Autowired
    public BibtextController(BibtexService bibtex, ReposService repos, ProjectService project) {
        this.bibtex = bibtex;
        this.repos = repos;
        this.project = project;
    }

    @GetMapping(path = "/project/{projectId}/bibtex.bib", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> projectBibTeX(@PathVariable String projectId) {
        Project project = this.project.getProjectById(projectId);
        if(project == null) return ResponseEntity.notFound().build();
        Set<ProjectPaper> accepted = project.getAccepted();
        List<PaperId> paperIds = accepted.stream()
                .sorted(Comparator.comparingLong(o -> -o.getAdded()))
                .map(ProjectPaper::getId)
                .collect(Collectors.toList());
        List<Paper> papers = repos.papersByIds(paperIds);
        String bibTeX = this.bibtex.getBibTeX(papers);
        return ResponseEntity.ok(bibTeX);
    }

    @GetMapping(path = "/paper/{repo}/{id}/bibtex.bib", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> paperBibTeX(@PathVariable String repo, @PathVariable String id) {
        PaperId paperId = new PaperId(repo, id);
        List<Paper> papers = repos.papersByIds(Collections.singletonList(paperId));
        String bibTeX = this.bibtex.getBibTeX(papers);
        return ResponseEntity.ok(bibTeX);
    }
}
