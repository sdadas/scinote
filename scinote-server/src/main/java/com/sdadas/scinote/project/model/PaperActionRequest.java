package com.sdadas.scinote.project.model;

import com.sdadas.scinote.shared.model.paper.PaperId;

/**
 * @author Sławomir Dadas
 */
public class PaperActionRequest implements PaperBaseAction {

    private String projectId;

    private PaperId paperId;

    private PaperAction action;

    @Override
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public PaperId getPaperId() {
        return paperId;
    }

    public void setPaperId(PaperId paperId) {
        this.paperId = paperId;
    }

    public PaperAction getAction() {
        return action;
    }

    public void setAction(PaperAction action) {
        this.action = action;
    }
}
