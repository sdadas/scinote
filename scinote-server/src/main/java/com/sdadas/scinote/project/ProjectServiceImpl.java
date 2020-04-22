package com.sdadas.scinote.project;

import com.sdadas.scinote.cache.CacheService;
import com.sdadas.scinote.cache.model.Cached;
import com.sdadas.scinote.project.model.*;
import com.sdadas.scinote.repos.ReposService;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import com.sdadas.scinote.shared.model.project.Project;
import com.sdadas.scinote.shared.model.project.ProjectInfo;
import com.sdadas.scinote.shared.model.project.ProjectPaper;
import com.sdadas.scinote.shared.model.validation.ActionResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final CacheService cache;

    private final ReposService repos;

    @Autowired
    public ProjectServiceImpl(CacheService cache, ReposService repos) {
        this.cache = cache;
        this.repos = repos;
        this.initCaches();
    }

    private void initCaches() {
        cache.initCache(Project.class);
    }

    @Override
    public Project getProjectById(String id) {
        Cached<Project> cached = cache.get(id, Project.class);
        return cached != null ? cached.getValue() : null;
    }

    @Override
    public List<ProjectInfo> getAllProjects() {
        List<Cached<Project>> results = cache.all(false, Project.class);
        return results.stream().map(ProjectInfo::new).collect(Collectors.toList());
    }

    @Override
    public ActionResponse editProject(EditProjectRequest request) {
        Cached<Project> cached = cache.get(request.getProjectId(), Project.class);
        ActionResponse res = validate(request, cached);
        if(res.hasErrors()) return res;
        Project project = cached.getValue();
        project.setTitle(request.getTitle());
        cache.put(project.getId(), project, Project.class);
        return res;
    }

    @Override
    public ActionResponse editPaper(EditPaperRequest request) {
        Cached<Project> cached = cache.get(request.getProjectId(), Project.class);
        ActionResponse res = validate(request, cached);
        if(res.hasErrors()) return res;
        Project project = cached.getValue();
        ProjectPaper paper = project.paper(request.getPaperId());
        paper.setNotes(request.getNotes());
        paper.setTags(request.getTags());
        cache.put(project.getId(), project, Project.class);
        return res;
    }

    @Override
    public ActionResponse paperAction(PaperActionRequest request) {
        Cached<Project> cached = cache.get(request.getProjectId(), Project.class);
        ActionResponse res = validate(request, cached);
        res.notBlank(request.getAction(), "action");
        if(res.hasErrors()) return res;
        PaperId paperId = request.getPaperId();
        Project project = cached.getValue();
        List<Paper> papers = repos.papersByIds(Collections.singletonList(request.getPaperId()));
        Paper paper = papers.isEmpty() ? null : papers.get(0);
        if(paper == null) {
            res.error(String.format("Paper with id '%s' not found", request.getPaperId().toString()));
        }
        if(res.hasErrors()) return res;
        switch (request.getAction()) {
            case ACCEPT: acceptPaper(project, paper, paperId, res); break;
            case REJECT: project.reject(paper, paperId); break;
            case READ_LATER: project.readLater(paper, paperId); break;
        }
        cache.put(project.getId(), project, Project.class);
        return res;
    }

    private void acceptPaper(Project project, Paper paper, PaperId paperId, ActionResponse res) {
        Paper result = repos.fetchReferences(paper);
        project.accept(result, paperId);
        res.setResult(result);
    }

    @Override
    public ActionResponse projectAction(ProjectActionRequest request) {
        ActionResponse res = new ActionResponse();
        res.notBlank(request.getAction(), "action");
        if(res.hasErrors()) return res;
        switch (request.getAction()) {
            case CREATE: res = createProject(request); break;
            case DELETE: res = deleteProject(request); break;
        }
        return res;
    }

    @Override
    public List<Paper> getSuggestions(String projectId, int num) {
        Cached<Project> cached = cache.get(projectId, Project.class);
        if(cached == null) return new ArrayList<>();
        Project value = cached.getValue();
        Set<PaperId> ids = value.getTopCandidatesRefsFirst(num);
        return repos.papersByIds(new ArrayList<>(ids));
    }

    private ActionResponse createProject(ProjectActionRequest request) {
        ActionResponse res = new ActionResponse();
        res.notBlank(request.getProjectId(), "projectId");
        if(res.hasErrors()) return res;
        Project project = new Project();
        project.setId(request.getProjectId());
        project.setTitle("Project name");
        cache.put(request.getProjectId(), project, Project.class);
        res.setResult(project);
        return res;
    }

    private ActionResponse deleteProject(ProjectActionRequest request) {
        Cached<Project> cached = cache.get(request.getProjectId(), Project.class);
        ActionResponse res = validate(request, cached);
        if(res.hasErrors()) return res;
        Cached<Project> deleted = cache.delete(request.getProjectId(), Project.class);
        if(deleted != null) {
            res.setResult(deleted.getValue());
        }
        return res;
    }

    private ActionResponse validate(ProjectBaseAction action, Cached<Project> project) {
        ActionResponse res = new ActionResponse();
        String projectId = action.getProjectId();
        res.notBlank(projectId, "projectId");
        if(StringUtils.isBlank(projectId) && project == null) {
            res.error(String.format("Project with id '%s' not found", projectId));
        }
        if(action instanceof PaperBaseAction) {
            PaperBaseAction paperAction = (PaperBaseAction) action;
            res.notBlank(paperAction.getPaperId(), "paperId");
            if(paperAction.getPaperId() == null) return res;
            res.notBlank(paperAction.getPaperId().getId(), "paperId.id");
            res.notBlank(paperAction.getPaperId().getRepo(), "paperId.repo");
        }
        return res;
    }
}
