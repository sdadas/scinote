package com.sdadas.scinote.project.model;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class EditProjectRequest implements ProjectBaseAction {

    private String projectId;

    private String title;

    @Override
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
