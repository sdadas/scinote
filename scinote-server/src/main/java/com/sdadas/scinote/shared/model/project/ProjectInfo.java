package com.sdadas.scinote.shared.model.project;

import com.sdadas.scinote.cache.model.Cached;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Sławomir Dadas
 */
public class ProjectInfo implements Serializable {

    private String id;

    private String title;

    private LocalDateTime updated;

    public ProjectInfo(Cached<Project> cached) {
        this.id = cached.getKey();
        this.title = cached.getName();
        this.updated = cached.getUpdated();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
}
