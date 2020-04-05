package com.sdadas.scinote.repos.shared.exception;

import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import com.sdadas.scinote.shared.model.paper.WebLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class RepeatSearch implements Serializable {

    private final String query;

    private List<PaperId> updateIds = new ArrayList<>();

    private List<WebLocation> updateUrls = new ArrayList<>();

    public RepeatSearch(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void addUpdateId(PaperId id) {
        this.updateIds.add(id);
    }

    public void addUpdateUrl(WebLocation location) {
        this.updateUrls.add(location);
    }

    public void updatePaper(Paper paper) {
        updateIds.forEach(paper::addId);
        updateUrls.forEach(paper::addUrl);
    }
}
