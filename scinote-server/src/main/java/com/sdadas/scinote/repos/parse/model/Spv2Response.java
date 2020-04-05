package com.sdadas.scinote.repos.parse.model;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class Spv2Response implements Serializable {

    private Spv2Doc doc;

    public Spv2Doc getDoc() {
        return doc;
    }

    public void setDoc(Spv2Doc doc) {
        this.doc = doc;
    }
}
