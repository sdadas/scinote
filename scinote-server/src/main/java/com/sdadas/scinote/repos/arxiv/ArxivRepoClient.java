package com.sdadas.scinote.repos.arxiv;

import com.sdadas.scinote.repos.arxiv.model.ArxivResponse;
import com.sdadas.scinote.repos.arxiv.rest.ArxivRestClient;
import com.sdadas.scinote.repos.shared.RepoClient;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import com.sdadas.scinote.repos.shared.utils.MultiRegexMatcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author Sławomir Dadas
 */
@Component
public class ArxivRepoClient implements RepoClient {

    private static final String URL_PATTERN = "(https?://)?arxiv.org/[a-zA-Z0-9\\-]+/([\\d\\.]+)[\\w\\./]*";

    private static final String UID_PATTERN = "arxiv:([\\d\\.]+)";

    private final MultiRegexMatcher matcher = new MultiRegexMatcher(URL_PATTERN, UID_PATTERN);

    private final ArxivRestClient client;

    public ArxivRepoClient() {
        this.client = new ArxivRestClient();
    }

    @Override
    public String repoId() {
        return "arxiv";
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public PaperId supports(String query) {
        String id = simplify(query);
        return id != null ? new PaperId(repoId(), id) : null;
    }

    @Override
    public List<Paper> search(String query) {
        String id = simplify(query);
        if(id == null) return Collections.emptyList();
        ArxivResponse res = client.search(id);
        Paper paper = new ArxivPaperBuilder(res, id).createPaper();
        return paper != null ? Collections.singletonList(paper) : Collections.emptyList();
    }

    @Override
    public List<Paper> load(List<PaperId> ids) {
        List<Paper> results = new ArrayList<>();
        for (PaperId id : ids) {
            List<Paper> paper = search(id.getId());
            results.addAll(paper);
        }
        return results;
    }

    private String simplify(String query) {
        Matcher matched = matcher.matched(query);
        if(matched == null) return null;
        String id = matched.group(matched.groupCount());
        return StringUtils.stripEnd(id, ".");
    }
}
