package com.sdadas.scinote.repos.parse.model;

import com.sdadas.scinote.repos.shared.model.Paper;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class ParseResponse implements Serializable {

    private Paper paper;

    private String error;

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
