package com.sdadas.scinote.repos.shared.model;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class PaperId implements Serializable {

    private String repo;

    private String id;

    public PaperId() {
    }

    public PaperId(String repo, String id) {
        this.repo = repo;
        this.id = id;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
