package com.sdadas.scinote.project;

import com.sdadas.scinote.project.model.EditPaperRequest;
import com.sdadas.scinote.project.model.EditProjectRequest;
import com.sdadas.scinote.project.model.PaperActionRequest;
import com.sdadas.scinote.project.model.ProjectActionRequest;
import com.sdadas.scinote.shared.model.project.Project;
import com.sdadas.scinote.shared.model.validation.ActionResponse;

/**
 * @author Sławomir Dadas
 */
public interface ProjectService {

    Project getProjectById(String id);

    ActionResponse editProject(EditProjectRequest request);

    ActionResponse editPaper(EditPaperRequest request);

    ActionResponse paperAction(PaperActionRequest request);

    ActionResponse projectAction(ProjectActionRequest request);
}
