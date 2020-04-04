package com.sdadas.scinote.repos.arxiv.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.sdadas.scinote.repos.shared.model.Author;
import com.sdadas.scinote.repos.shared.model.Paper;
import com.sdadas.scinote.repos.shared.model.PaperId;
import com.sdadas.scinote.repos.shared.model.WebLocation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
@JacksonXmlRootElement(localName = "feed")
public class ArxivResponse implements Serializable {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "entry")
    private List<ArxivEntry> entries;

    public List<ArxivEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ArxivEntry> entries) {
        this.entries = entries;
    }
}
