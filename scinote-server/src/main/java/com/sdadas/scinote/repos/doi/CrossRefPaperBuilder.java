package com.sdadas.scinote.repos.doi;

import com.sdadas.scinote.repos.doi.model.*;
import com.sdadas.scinote.repos.shared.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
public class CrossRefPaperBuilder implements Serializable {

    private final CrossRefResponse response;

    private final String doi;

    private final CrossRefTypeMapper mapper;

    public CrossRefPaperBuilder(CrossRefResponse response, String doi, CrossRefTypeMapper mapper) {
        this.response = response;
        this.doi = doi;
        this.mapper = mapper;
    }

    public Paper createPaper() {
        String status = response.getStatus();
        if(!StringUtils.equalsIgnoreCase(status, "ok")) {
            String message = String.format("CrossRef service returned %s status for doi %s", status, doi);
            throw new IllegalStateException(message);
        }
        CrossRefPaper msg = response.getMessage();
        Paper paper = new Paper();
        paper.addId(new PaperId("doi", doi));
        paper.setTitle(msg.getTitle().get(0));
        CrossRefDate yearDate = ObjectUtils.firstNonNull(msg.getIssued(), msg.getCreated(), msg.getDeposited());
        paper.setYear(yearDate.year());
        CrossRefDate created = msg.getCreated();
        paper.setDate(created != null && created.getDateTime() != null ? created.getDateTime().toLocalDate() : null);
        paper.setCitations(msg.getReferencedByCount());
        paper.setType(mapper.map(msg.getType()));
        paper.setDoi(doi);
        paper.setPages(msg.getPages());
        paper.setSource(createSource(msg));
        paper.setUrls(createWebLocations(msg));
        paper.setAuthors(createAuthors(msg));
        if(msg.getSubjects() != null) paper.setKeywords(msg.getSubjects());
        return paper;
    }

    private List<Author> createAuthors(CrossRefPaper msg) {
        if(msg.getAuthors() == null) return new ArrayList<>();
        List<Author> results = new ArrayList<>();
        int idx = 0;
        for (CrossRefAuthor author : msg.getAuthors()) {
            Author result = new Author(author.getGiven(), author.getFamily(), idx);
            result.setAffiliation(author.firstAffiliation());
            results.add(result);
            idx++;
        }
        return results;
    }

    private List<WebLocation> createWebLocations(CrossRefPaper msg) {
        if(msg.getLinks() == null) return new ArrayList<>();
        return msg.getLinks().stream()
                .map(val -> new WebLocation(val.getContentType(), val.getUrl()))
                .collect(Collectors.toList());
    }

    private Source createSource(CrossRefPaper msg) {
        Source source = new Source();
        source.setPublisher(msg.getPublisher());
        source.setName(alternatives(msg.getContainerTitle(), msg.getShortContainerTitle()));
        if(msg.getEvent() != null) {
            source.setVenue(msg.getEvent().getName());
        }
        return source;
    }

    private String alternatives(Object... values) {
        for (Object value : values) {
            if(value == null) {
                /* DO NOTHING */
            } else if (value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                if(collection.isEmpty()) continue;
                Object firstValue = collection.iterator().next();
                if(firstValue == null) continue;
                return firstValue.toString();
            } else if(value instanceof String) {
                if(StringUtils.isBlank((CharSequence) value)) continue;
                return value.toString();
            } else {
                return value.toString();
            }
        }
        return null;
    }
}
