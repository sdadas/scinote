package com.sdadas.scinote.project.model;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class ProjectActionRequest implements ProjectBaseAction {

    private String projectId;

    private ProjectAction action;

    @Override
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public ProjectAction getAction() {
        return action;
    }

    public void setAction(ProjectAction action) {
        this.action = action;
    }
}
