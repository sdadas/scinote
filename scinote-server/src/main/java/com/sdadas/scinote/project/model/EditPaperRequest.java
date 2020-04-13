package com.sdadas.scinote.project.model;

import com.sdadas.scinote.shared.model.paper.PaperId;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class EditPaperRequest implements PaperBaseAction {

    private String projectId;

    private PaperId paperId;

    private String notes;

    private List<String> tags;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
