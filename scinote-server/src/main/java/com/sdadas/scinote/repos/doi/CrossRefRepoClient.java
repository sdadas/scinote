package com.sdadas.scinote.repos.doi;

import com.sdadas.scinote.repos.doi.model.CrossRefResponse;
import com.sdadas.scinote.repos.doi.rest.CrossRefRestClient;
import com.sdadas.scinote.repos.shared.RepoClient;
import com.sdadas.scinote.repos.shared.model.Paper;
import com.sdadas.scinote.repos.shared.model.PaperId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sławomir Dadas
 */
@Component
public class CrossRefRepoClient implements RepoClient {

    private final static String DOI_REGEX = "\\b(10[.][0-9]{4,}(?:[.][0-9]+)*/(?:(?![\"&'<>])\\S)+)\\b";

    private final static Pattern DOI_PATTERN = Pattern.compile(DOI_REGEX, Pattern.UNICODE_CASE);

    private final CrossRefRestClient client;

    private final CrossRefTypeMapper mapper;

    @Autowired
    public CrossRefRepoClient(CrossRefRestClient client) {
        this.client = client;
        this.mapper = new CrossRefTypeMapper();
    }

    @Override
    public String repoId() {
        return "doi";
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public PaperId supports(String query) {
        String id = simplify(query);
        return id != null ? new PaperId(repoId(), id) : null;
    }

    @Override
    public List<Paper> search(String query) {
        String doi = simplify(query);
        if(StringUtils.isBlank(doi)) return Collections.emptyList();
        CrossRefResponse response = client.search(doi);
        Paper paper = new CrossRefPaperBuilder(response, doi, mapper).createPaper();
        return paper != null ? Collections.singletonList(paper) : null;
    }

    @Override
    public List<Paper> load(List<PaperId> ids) {
        List<Paper> results = new ArrayList<>();
        for (PaperId id : ids) {
            results.addAll(search(id.getId()));
        }
        return results;
    }

    private String simplify(String query) {
        String id = query;
        if(StringUtils.startsWithAny(id.toLowerCase(), "http://", "https://")) {
            Matcher matcher = DOI_PATTERN.matcher(id);
            if(matcher.find()) {
                return matcher.group();
            }
            id = StringUtils.removeStartIgnoreCase(id, "https://");
            id = StringUtils.removeStartIgnoreCase(id, "http://");
        }
        if(StringUtils.startsWithAny(id.toLowerCase(), "doi:", "doi.org/", "dx.doi.org/")) {
            id = StringUtils.removeStartIgnoreCase(id, "doi:");
            id = StringUtils.removeStartIgnoreCase(id, "doi.org/");
            id = StringUtils.removeStartIgnoreCase(id, "dx.doi.org/");
        }
        Matcher matcher = DOI_PATTERN.matcher(id);
        if(matcher.matches()) {
            return matcher.group();
        } else {
            return null;
        }
    }
}
