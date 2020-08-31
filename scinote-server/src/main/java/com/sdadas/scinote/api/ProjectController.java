package com.sdadas.scinote.api;

import com.sdadas.scinote.project.ProjectService;
import com.sdadas.scinote.project.model.*;
import com.sdadas.scinote.project.model.graph.ProjectGraph;
import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import com.sdadas.scinote.shared.model.project.Project;
import com.sdadas.scinote.shared.model.project.ProjectInfo;
import com.sdadas.scinote.shared.model.validation.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * @author Sławomir Dadas
 */
@CrossOrigin
@RestController
public class ProjectController {

    private final ProjectService service;

    @Autowired
    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping(path = "/project/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectInfo> listProjects() {
        return service.getAllProjects();
    }

    @GetMapping(path = "/project/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Project> getProject(@PathVariable String projectId) {
        Project project = service.getProjectById(projectId);
        if(project != null) project.setSuggestions(null);
        return ResponseEntity.of(Optional.ofNullable(project));
    }

    @GetMapping(path = "/project/{projectId}/graph", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectGraph> getProjectGraph(@PathVariable String projectId) {
        ProjectGraph graph = service.getProjectGraph(projectId);
        return ResponseEntity.of(Optional.ofNullable(graph));
    }

    @PostMapping(path = "/project/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse editProject(@RequestBody EditProjectRequest request) {
        ActionResponse response = service.editProject(request);
        prepareResponse(response);
        return response;
    }

    @PostMapping(path = "/project/paper/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse editPaper(@RequestBody EditPaperRequest request) {
        ActionResponse response = service.editPaper(request);
        prepareResponse(response);
        return response;
    }

    @PostMapping(path = "/project/action", produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse projectAction(@RequestBody ProjectActionRequest request) {
        ActionResponse response = service.projectAction(request);
        prepareResponse(response);
        return response;
    }

    @PostMapping(path = "/project/paper/action", produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse paperAction(@RequestBody PaperActionRequest request) {
        ActionResponse response = service.paperAction(request);
        prepareResponse(response);
        return response;
    }

    @GetMapping(path = "/project/{projectId}/suggestions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Paper> projectSuggestions(@PathVariable String projectId, @RequestParam(defaultValue = "10") Integer num) {
        return service.getSuggestions(projectId, num);
    }

    @PostMapping(path = "/project/{projectId}/{paperId}/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ActionResponse> upload(@PathVariable String projectId, @PathVariable String paperId,
                                                 @RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();
        PaperAttachFileRequest request = new PaperAttachFileRequest();
        request.setProjectId(projectId);
        request.setPaperId(PaperId.fromString(paperId, ","));
        request.setFilename(file.getOriginalFilename());
        request.setResource(file.getResource());
        ActionResponse response = service.paperAction(request);
        return ResponseEntity.ok(response);
    }

    private void prepareResponse(ActionResponse response) {
        Object result = response.getResult();
        if(result instanceof Project) {
            ((Project) result).setSuggestions(null);
        }
    }
}
