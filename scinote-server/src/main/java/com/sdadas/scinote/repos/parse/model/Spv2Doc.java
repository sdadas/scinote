package com.sdadas.scinote.repos.parse.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class Spv2Doc implements Serializable {

    private List<String> authors;

    private List<Spv2Ref> bibs;

    private String docName;

    private String docSha;

    private String title;

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<Spv2Ref> getBibs() {
        return bibs;
    }

    public void setBibs(List<Spv2Ref> bibs) {
        this.bibs = bibs;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocSha() {
        return docSha;
    }

    public void setDocSha(String docSha) {
        this.docSha = docSha;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
