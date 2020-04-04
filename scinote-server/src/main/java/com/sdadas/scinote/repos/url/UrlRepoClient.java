package com.sdadas.scinote.repos.url;

import com.google.common.net.InternetDomainName;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import com.sdadas.scinote.repos.shared.RepoClient;
import com.sdadas.scinote.repos.shared.exception.RepeatSearch;
import com.sdadas.scinote.repos.shared.exception.RepeatSearchException;
import com.sdadas.scinote.repos.shared.model.Paper;
import com.sdadas.scinote.repos.shared.model.PaperId;
import com.sdadas.scinote.repos.shared.model.WebLocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
@Component
public class UrlRepoClient implements RepoClient {

    @Override
    public String repoId() {
        return "url";
    }

    @Override
    public int priority() {
        return 90;
    }

    @Override
    public PaperId supports(String query) {
        Url url = getUrl(query);
        return url != null ? new PaperId(repoId(), url.getOriginalUrl()) : null;
    }

    @Override
    public List<Paper> search(String query) throws Exception {
        Url url = getUrl(query);
        String originalUrl = url != null ? url.getOriginalUrl() : null;
        if(StringUtils.isBlank(originalUrl)) return Collections.emptyList();
        PaperId id = new PaperId("url", originalUrl);
        UrlParser parser = new UrlParser(url);
        parser.download();
        List<String> dois = parser.findDOI();
        if(!dois.isEmpty()) {
            RepeatSearch context = new RepeatSearch(dois.get(0));
            context.addUpdateId(id);
            context.addUpdateUrl(new WebLocation("html", originalUrl));
            throw new RepeatSearchException(context);
        }
        return Collections.singletonList(parser.parse());
    }

    @Override
    public List<Paper> load(List<PaperId> ids) throws Exception {
        List<Paper> results = new ArrayList<>();
        for (PaperId id : ids) {
            results.addAll(search(id.getId()));
        }
        return results;
    }

    @SuppressWarnings("UnstableApiUsage")
    private Url getUrl(String query) {
        if(!StringUtils.startsWithAny(query.toLowerCase(), "http://", "https://")) return null;
        UrlDetector detector = new UrlDetector(query, UrlDetectorOptions.Default);
        List<Url> detected = detector.detect();
        if(detected.isEmpty()) return null;
        Url url = detected.get(0);
        String host = url.getHost();
        boolean publicDomain = InternetDomainName.from(host).isUnderPublicSuffix();
        return publicDomain ? url : null;
    }
}
