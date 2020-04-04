package com.sdadas.scinote.repos.doi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class CrossRefEvent implements Serializable {

    @JsonProperty("name")
    private String name;

    @JsonProperty("location")
    private String location;

    @JsonProperty("start")
    private CrossRefDate start;

    @JsonProperty("end")
    private CrossRefDate end;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CrossRefDate getStart() {
        return start;
    }

    public void setStart(CrossRefDate start) {
        this.start = start;
    }

    public CrossRefDate getEnd() {
        return end;
    }

    public void setEnd(CrossRefDate end) {
        this.end = end;
    }
}
