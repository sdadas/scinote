package com.sdadas.scinote.repos.shared.model;

import com.sdadas.scinote.shared.model.paper.PaperId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class Query implements Serializable {

    private String query;

    private List<PaperId> results = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<PaperId> getResults() {
        return results;
    }

    public void setResults(List<PaperId> results) {
        this.results = results;
    }
}
