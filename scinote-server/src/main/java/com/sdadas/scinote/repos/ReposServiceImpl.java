package com.sdadas.scinote.repos;

import com.sdadas.scinote.repos.academic.AcademicRepoClient;
import com.sdadas.scinote.repos.parse.PaperParserService;
import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.repos.shared.RepoClient;
import com.sdadas.scinote.repos.shared.exception.RepeatSearch;
import com.sdadas.scinote.repos.shared.exception.RepeatSearchException;
import com.sdadas.scinote.repos.shared.model.Query;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@Service
public class ReposServiceImpl implements ReposService {

    private final static Logger LOG = LoggerFactory.getLogger(ReposServiceImpl.class);

    private final List<RepoClient> repos;

    private final AcademicRepoClient academicClient;

    private final PaperParserService parser;

    private final PaperCache cache;

    public ReposServiceImpl(List<RepoClient> repos, PaperParserService parser, PaperCache cache) {
        repos.sort(Comparator.comparingInt(RepoClient::priority));
        this.repos = repos;
        this.parser = parser;
        this.cache = cache;
        this.academicClient = repos.stream()
                .filter(val -> val instanceof AcademicRepoClient)
                .map(val -> (AcademicRepoClient) val)
                .findAny().orElseThrow();
    }

    @Override
    public Paper fetchReferences(Paper paper) {
        this.academicClient.fetchReferences(paper);
        cache.savePaper(paper);
        return paper;
    }

    @Override
    public List<Paper> papersByQuery(String query) {
        query = StringUtils.strip(query);
        if(StringUtils.isBlank(query)) return Collections.emptyList();
        List<Paper> cached = getFromCache(query);
        if(cached != null) return cached;
        for (RepoClient repo : repos) {
            List<Paper> results = query(query, repo);
            if(results != null) {
                cache.saveQuery(query.toLowerCase(), results);
                return results;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Paper> papersByIds(List<PaperId> paperIds) {
        List<Paper> cached = cache.getPapers(paperIds);
        Set<PaperId> missing = new LinkedHashSet<>(paperIds);
        Map<PaperId, Paper> results = new LinkedHashMap<>();
        paperIds.forEach(id -> results.put(id, null));
        for (Paper paper : cached) {
            for (PaperId id : paper.getIds()) {
                if(!results.containsKey(id)) continue;
                results.put(id, paper);
                missing.remove(id);
            }
        }
        if(missing.isEmpty()) return new ArrayList<>(results.values());
        for (RepoClient repo : repos) {
            updateFromRepo(repo, results, missing);
        }
        return results.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public ParseResponse parse(ParseRequest request) {
        ParseResponse response = parser.parse(request);
        Paper paper = response.getPaper();
        if(paper != null) {
            PaperId paperId = paper.getIds().get(0);
            Paper cached = cache.getPaper(paperId);
            if(cached != null) {
                response.setPaper(cached);
            } else {
                cache.savePaper(paper);
            }
        }
        return response;
    }

    private List<Paper> query(String query, RepoClient repo) {
        PaperId paperId = repo.supports(query);
        if(paperId != null) {
            Paper paper = cache.getPaper(paperId);
            if(paper != null) {
                LOG.debug("Paper found in cache for id '{}'", paperId.toString());
                return Collections.singletonList(paper);
            }
            LOG.debug("Query '{}' matched {} repository", query, repo.repoId());
            try {
                List<Paper> search = repo.search(query);
                if(search != null) search.forEach(cache::savePaper);
                return search;
            } catch (RepeatSearchException ex) {
                RepeatSearch context = ex.getContext();
                LOG.debug("Repeat search from '{}' to '{}'", query, context.getQuery());
                List<Paper> results = papersByQuery(context.getQuery());
                if(results != null) {
                    results.forEach(context::updatePaper);
                    results.forEach(cache::savePaper);
                }
                return results;
            } catch (Exception e) {
                LOG.error("Repository failed " + repo.getClass().getSimpleName(), e);
                return Collections.emptyList();
            }
        } else {
            return null;
        }
    }

    private List<Paper> getFromCache(String q) {
        Query query = cache.getQuery(q);
        if(query == null) return null;
        LOG.debug("Query found in cache '{}'", q);
        List<PaperId> paperIds = query.getResults();
        return papersByIds(paperIds);
    }

    private void updateFromRepo(RepoClient repo, Map<PaperId, Paper> results, Set<PaperId> missing) {
        String repoId = repo.repoId();
        List<PaperId> ids = missing.stream().filter(val -> val.getRepo().equals(repoId)).collect(Collectors.toList());
        try {
            List<Paper> papers = repo.load(ids);
            for (Paper paper : papers) {
                for (PaperId id : paper.getIds()) {
                    if(!results.containsKey(id)) continue;
                    results.put(id, paper);
                    missing.remove(id);
                }
                cache.savePaper(paper);
            }
        } catch (Exception e) {
            LOG.error("Repository failed " + repo.getClass().getSimpleName(), e);
        }
    }
}
