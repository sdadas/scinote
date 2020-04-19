package com.sdadas.scinote.repos.doi;

import com.google.common.io.CharStreams;
import com.google.common.net.InternetDomainName;
import com.sdadas.scinote.repos.doi.model.CrossRefResponse;
import com.sdadas.scinote.repos.doi.rest.CrossRefRestClient;
import com.sdadas.scinote.repos.shared.RepoClient;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@Component
public class CrossRefRepoClient implements RepoClient {

    private final static String DOI_REGEX = "\\b(10[.][0-9]{4,}(?:[.][0-9]+)*/(?:(?![\"&'<>])\\S)+)\\b";

    private final static Pattern DOI_PATTERN = Pattern.compile(DOI_REGEX, Pattern.UNICODE_CASE);

    private final CrossRefRestClient client;

    private final CrossRefTypeMapper mapper;

    private final Set<String> urlBlacklist;

    @Autowired
    public CrossRefRepoClient() {
        this.client = new CrossRefRestClient();
        this.mapper = new CrossRefTypeMapper();
        this.urlBlacklist = loadUrlBlacklist();
    }

    @SuppressWarnings("UnstableApiUsage")
    private Set<String> loadUrlBlacklist() {
        Resource resource = new ClassPathResource("/lists/doi_url_blacklist.txt");
        try(InputStream is = resource.getInputStream()) {
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            List<String> lines = CharStreams.readLines(reader);
            return new HashSet<>(lines);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
            String doi = findDoiInUrl(id);
            if(doi != null) return doi;
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

    @SuppressWarnings("UnstableApiUsage")
    private String findDoiInUrl(String url) {
        Matcher matcher = DOI_PATTERN.matcher(url);
        if(matcher.find()) {
            try {
                URI uri = new URI(url);
                InternetDomainName domain = InternetDomainName.from(uri.getHost());
                if(domain.isUnderRegistrySuffix()) {
                    String domainName = domain.topDomainUnderRegistrySuffix().toString().toLowerCase();
                    if(urlBlacklist.contains(domainName)) {
                        return null;
                    }
                }
            } catch (URISyntaxException e) {
                return matcher.group();
            }
        }
        return null;
    }
}
