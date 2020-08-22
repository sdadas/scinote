package com.sdadas.scinote.repos.academic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdadas.scinote.repos.academic.model.AcademicPaper;
import com.sdadas.scinote.repos.academic.model.AcademicField;
import com.sdadas.scinote.repos.academic.model.api.EvaluateRequest;
import com.sdadas.scinote.repos.academic.model.api.EvaluateResponse;
import com.sdadas.scinote.repos.academic.rest.AcademicRestClient;
import com.sdadas.scinote.repos.shared.RepoClient;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import cz.jirutka.unidecode.Unidecode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@Component
public class AcademicRepoClient implements RepoClient {

    private final AcademicRestClient restClient;

    private final ObjectMapper mapper;

    @Autowired
    public AcademicRepoClient(AcademicRestClient restClient, ObjectMapper mapper) {
        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Override
    public List<Paper> search(String title) throws IOException {
        String norm = normalize(title);
        String query = String.format("Ti='%s'", norm);
        return query(query, 5);
    }

    @Override
    public List<Paper> load(List<PaperId> ids) throws Exception {
        if(ids == null || ids.isEmpty()) return new ArrayList<>();
        String query = ids.stream().map(val -> "Id=" + val.getId()).collect(Collectors.joining(", "));
        if(ids.size() > 1) query = "Or(" + query + ")";
        List<Paper> papers = query(query, ids.size());
        return papers.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Paper fetchReferences(Paper paper) throws IOException {
        AcademicPaper converted = convertPaperToAcademicPaper(paper);
        if(converted == null) return paper;
        paper = converted;
        AcademicPaper ap = (AcademicPaper) paper;
        if(ap.getCitations() == null || ap.getCitations() == 0) return paper;
        if(ap.getReverseReferences() != null) return paper;
        PaperId paperId = ap.getIdOfType(repoId());
        String query = String.format("RId=%s", paperId.getId());
        EvaluateRequest request = new EvaluateRequest();
        request.setExpr(query);
        request.setOrderby("Y:desc");
        request.setAttributes(AcademicField.ENTITY_SCOPE);
        request.setCount(20);
        EvaluateResponse response = this.restClient.evaluate(request);
        List<Map<String, Object>> entities = response.getEntities();
        List<Long> results;
        if(entities != null) {
            Function<Map<String, Object>, Long> id = val -> ((Number) val.get(AcademicField.ENTITY_ID.code())).longValue();
            results = entities.stream().map(id).collect(Collectors.toList());
        } else {
            results = new ArrayList<>();
        }
        ap.setReverseReferences(results);
        return paper;
    }

    private AcademicPaper convertPaperToAcademicPaper(Paper paper) throws IOException {
        if(paper instanceof AcademicPaper) {
            return (AcademicPaper) paper;
        } else {
            String title = paper.getTitle();
            if(StringUtils.isBlank(title)) return null;
            List<Paper> candidates = search(title);
            AcademicPaperMatcher matcher = new AcademicPaperMatcher(paper, candidates);
            paper = matcher.match();
            if(paper instanceof AcademicPaper) {
                return (AcademicPaper) paper;
            } else {
                return null;
            }
        }
    }

    private List<Paper> query(String query, int size) throws IOException {
        EvaluateRequest request = new EvaluateRequest();
        request.setExpr(query);
        request.setAttributes(AcademicField.ENTITY_SCOPE);
        request.setCount(size);
        EvaluateResponse response = this.restClient.evaluate(request);
        return createPapersFromResponse(response);
    }

    @SuppressWarnings("unchecked")
    private List<Paper> createPapersFromResponse(EvaluateResponse response) {
        List<Map<String, Object>> entities = response.getEntities();
        if(entities == null) return new ArrayList<>();
        List<Paper> results = new ArrayList<>();
        for (Map<String, Object> entity : entities) {
            results.add(new AcademicPaper(entity));
        }
        return results;
    }

    private String normalize(String text) {
        String res = Unidecode.toAscii().decode(text);
        res = StringUtils.lowerCase(res);
        return res.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}]", " ").replaceAll("\\s+", " ").strip();
    }

    @Override
    public String repoId() {
        return "academic";
    }

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public PaperId supports(String query) {
        return new PaperId(repoId(), normalize(query));
    }
}
