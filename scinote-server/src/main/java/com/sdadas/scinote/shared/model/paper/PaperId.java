package com.sdadas.scinote.shared.model.paper;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Sławomir Dadas
 */
public class PaperId implements Serializable {

    private String repo;

    private String id;

    public static PaperId fromString(String string, String sep) {
        String repo = StringUtils.substringBefore(string, sep);
        String id = StringUtils.substringAfter(string, sep);
        return new PaperId(repo, id);
    }

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

    @Override
    public String toString() {
        return repo + "," + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaperId paperId = (PaperId) o;
        return repo.equals(paperId.repo) && id.equals(paperId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repo, id);
    }
}
