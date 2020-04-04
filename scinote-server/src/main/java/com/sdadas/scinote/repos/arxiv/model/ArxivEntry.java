package com.sdadas.scinote.repos.arxiv.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
@JacksonXmlRootElement(localName = "entry")
public class ArxivEntry implements Serializable {

    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "updated")
    private LocalDateTime updated;

    @JacksonXmlProperty(localName = "published")
    private LocalDateTime published;

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "summary")
    private String summary;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "author")
    private List<ArxivAuthor> authors;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    private List<ArxivLink> links;

    public ArxivEntry() {
    }

    public ArxivEntry(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ArxivAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<ArxivAuthor> authors) {
        this.authors = authors;
    }

    public List<ArxivLink> getLinks() {
        return links;
    }

    public void setLinks(List<ArxivLink> links) {
        this.links = links;
    }
}
