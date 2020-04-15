package com.sdadas.scinote.api;

import com.sdadas.scinote.project.ProjectService;
import com.sdadas.scinote.project.model.EditPaperRequest;
import com.sdadas.scinote.project.model.EditProjectRequest;
import com.sdadas.scinote.project.model.PaperActionRequest;
import com.sdadas.scinote.project.model.ProjectActionRequest;
import com.sdadas.scinote.shared.model.project.Project;
import com.sdadas.scinote.shared.model.project.ProjectInfo;
import com.sdadas.scinote.shared.model.validation.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private void prepareResponse(ActionResponse response) {
        Object result = response.getResult();
        if(result instanceof Project) {
            ((Project) result).setSuggestions(null);
        }
    }
}
