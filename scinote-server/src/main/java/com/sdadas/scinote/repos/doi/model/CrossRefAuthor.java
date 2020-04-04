package com.sdadas.scinote.repos.doi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sławomir Dadas
 */
public class CrossRefAuthor implements Serializable {

    @JsonProperty("given")
    private String given;

    @JsonProperty("family")
    private String family;

    @JsonProperty("sequence")
    private String sequence;

    @JsonProperty("affiliation")
    private List<Map<String, Object>> affiliation;

    @JsonProperty("ORCID")
    private String orcid;

    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public List<Map<String, Object>> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<Map<String, Object>> affiliation) {
        this.affiliation = affiliation;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String firstAffiliation() {
        if(affiliation == null || affiliation.isEmpty()) return null;
        return Objects.toString(affiliation.get(0).get("name"));
    }
}
