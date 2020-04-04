package com.sdadas.scinote.repos.doi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class CrossRefPaper implements Serializable {

    @JsonProperty("indexed")
    private CrossRefDate indexed;

    @JsonProperty("created")
    private CrossRefDate created;

    @JsonProperty("deposited")
    private CrossRefDate deposited;

    @JsonProperty("published-print")
    private CrossRefDate publishedPrint;

    @JsonProperty("issued")
    private CrossRefDate issued;

    @JsonProperty("publisher")
    private String publisher;

    @JsonProperty("publisher-location")
    private String publisherLocation;

    @JsonProperty("reference-count")
    private Integer referenceCount;

    @JsonProperty("is-referenced-by-count")
    private Integer referencedByCount;

    @JsonProperty("DOI")
    private String doi;

    @JsonProperty("type")
    private String type;

    @JsonProperty("source")
    private String source;

    @JsonProperty("title")
    private List<String> title;

    @JsonProperty("prefix")
    private String prefix;

    @JsonProperty("member")
    private String member;

    @JsonProperty("author")
    private List<CrossRefAuthor> authors;

    @JsonProperty("container-title")
    private List<String> containerTitle;

    @JsonProperty("short-container-title")
    private List<String> shortContainerTitle;

    @JsonProperty("original-title")
    private List<String> originalTitle;

    @JsonProperty("subtitle")
    private List<String> subtitle;

    @JsonProperty("short-title")
    private List<String> shortTitle;

    @JsonProperty("score")
    private Integer score;

    @JsonProperty("URL")
    private String url;

    @JsonProperty("page")
    private String pages;

    @JsonProperty("subject")
    private List<String> subjects;

    @JsonProperty("link")
    private List<CrossRefLink> links;

    @JsonProperty("event")
    private CrossRefEvent event;

    public CrossRefDate getIndexed() {
        return indexed;
    }

    public void setIndexed(CrossRefDate indexed) {
        this.indexed = indexed;
    }

    public CrossRefDate getCreated() {
        return created;
    }

    public void setCreated(CrossRefDate created) {
        this.created = created;
    }

    public CrossRefDate getDeposited() {
        return deposited;
    }

    public void setDeposited(CrossRefDate deposited) {
        this.deposited = deposited;
    }

    public CrossRefDate getPublishedPrint() {
        return publishedPrint;
    }

    public void setPublishedPrint(CrossRefDate publishedPrint) {
        this.publishedPrint = publishedPrint;
    }

    public CrossRefDate getIssued() {
        return issued;
    }

    public void setIssued(CrossRefDate issued) {
        this.issued = issued;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherLocation() {
        return publisherLocation;
    }

    public void setPublisherLocation(String publisherLocation) {
        this.publisherLocation = publisherLocation;
    }

    public Integer getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(Integer referenceCount) {
        this.referenceCount = referenceCount;
    }

    public Integer getReferencedByCount() {
        return referencedByCount;
    }

    public void setReferencedByCount(Integer referencedByCount) {
        this.referencedByCount = referencedByCount;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public List<CrossRefAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<CrossRefAuthor> authors) {
        this.authors = authors;
    }

    public List<String> getContainerTitle() {
        return containerTitle;
    }

    public void setContainerTitle(List<String> containerTitle) {
        this.containerTitle = containerTitle;
    }

    public List<String> getShortContainerTitle() {
        return shortContainerTitle;
    }

    public void setShortContainerTitle(List<String> shortContainerTitle) {
        this.shortContainerTitle = shortContainerTitle;
    }

    public List<String> getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(List<String> originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<String> getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(List<String> subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(List<String> shortTitle) {
        this.shortTitle = shortTitle;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<CrossRefLink> getLinks() {
        return links;
    }

    public void setLinks(List<CrossRefLink> links) {
        this.links = links;
    }

    public CrossRefEvent getEvent() {
        return event;
    }

    public void setEvent(CrossRefEvent event) {
        this.event = event;
    }
}
