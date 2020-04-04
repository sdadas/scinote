package com.sdadas.scinote.repos.shared.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class Paper implements Serializable {

    private List<PaperId> ids = new ArrayList<>();

    private String title;

    private String titleNorm;

    private PaperType type;

    private String summary;

    private List<Author> authors = new ArrayList<>();

    private List<String> languages = new ArrayList<>();

    private Integer year;

    private LocalDate date;

    private Source source = new Source();

    private List<WebLocation> urls = new ArrayList<>();

    private Integer citations;

    private String pages;

    private String doi;

    private List<String> keywords = new ArrayList<>();

    public List<PaperId> getIds() {
        return ids;
    }

    public PaperId getIdOfType(String repoId) {
        return this.ids.stream().filter(val -> val.getRepo().equals(repoId)).findFirst().orElse(null);
    }

    public void setIds(List<PaperId> ids) {
        this.ids = ids;
    }

    public void addId(PaperId id) {
        this.ids.add(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleNorm() {
        return titleNorm;
    }

    public void setTitleNorm(String titleNorm) {
        this.titleNorm = titleNorm;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public PaperType getType() {
        return type;
    }

    public void setType(PaperType type) {
        this.type = type;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public List<WebLocation> getUrls() {
        return urls;
    }

    public void setUrls(List<WebLocation> urls) {
        this.urls = urls;
    }

    public void addUrl(WebLocation url) {
        this.urls.add(url);
    }

    public Integer getCitations() {
        return citations;
    }

    public void setCitations(Integer citations) {
        this.citations = citations;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
