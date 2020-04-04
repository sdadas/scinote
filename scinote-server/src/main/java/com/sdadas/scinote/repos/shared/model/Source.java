package com.sdadas.scinote.repos.shared.model;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class Source implements Serializable {

    private String name;

    private String publisher;

    private String venue;

    private String venueShort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getVenueShort() {
        return venueShort;
    }

    public void setVenueShort(String venueShort) {
        this.venueShort = venueShort;
    }
}
