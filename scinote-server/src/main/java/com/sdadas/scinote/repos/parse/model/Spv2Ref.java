package com.sdadas.scinote.repos.parse.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class Spv2Ref implements Serializable {

    private String venue;

    private String year;

    private List<String> authors;

    private String title;

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
