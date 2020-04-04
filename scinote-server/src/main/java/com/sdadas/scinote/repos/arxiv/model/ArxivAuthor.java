package com.sdadas.scinote.repos.arxiv.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
@JacksonXmlRootElement(localName = "author")
public class ArxivAuthor implements Serializable {

    @JacksonXmlProperty(localName = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
