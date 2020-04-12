package com.sdadas.scinote.project.model;

import com.sdadas.scinote.shared.model.paper.PaperId;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class PaperActionRequest implements Serializable {

    private String projectId;

    private PaperId paperId;

    private PaperAction action;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

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
