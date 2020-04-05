package com.sdadas.scinote.repos.shared.model;

import com.sdadas.scinote.shared.model.paper.PaperId;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class PaperIdMapping implements Serializable {

    private PaperId paperId;

    private String uid;

    public PaperIdMapping() {
    }

    public PaperIdMapping(PaperId paperId, String uid) {
        this.paperId = paperId;
        this.uid = uid;
    }

    public PaperId getPaperId() {
        return paperId;
    }

    public void setPaperId(PaperId paperId) {
        this.paperId = paperId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
