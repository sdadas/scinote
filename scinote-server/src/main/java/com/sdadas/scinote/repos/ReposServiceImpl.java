package com.sdadas.scinote.repos;

import com.sdadas.scinote.repos.shared.RepoClient;
import com.sdadas.scinote.repos.shared.exception.RepeatSearch;
import com.sdadas.scinote.repos.shared.exception.RepeatSearchException;
import com.sdadas.scinote.repos.shared.model.Paper;
import com.sdadas.scinote.repos.shared.model.PaperId;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
@Service
public class ReposServiceImpl implements ReposService {

    private final static Logger LOG = LoggerFactory.getLogger(ReposServiceImpl.class);

    private final List<RepoClient> repos;

    public ReposServiceImpl(List<RepoClient> repos) {
        repos.sort(Comparator.comparingInt(RepoClient::priority));
        this.repos = repos;
    }

    @Override
    public List<Paper> query(String query) {
        query = StringUtils.strip(query);
        if(StringUtils.isBlank(query)) return Collections.emptyList();
        for (RepoClient repo : repos) {
            List<Paper> results = query(query, repo);
            if(results != null) {
                return results;
            }
        }
        return new ArrayList<>();
    }

    private List<Paper> query(String query, RepoClient repo) {
        PaperId paperId = repo.supports(query);
        if(paperId != null) {
            // TODO: check if is in cache
            LOG.debug("Query '{}' matched {} repository", query, repo.repoId());
            try {
                List<Paper> search = repo.search(query);
                // TODO: save in cache
                return search;
            } catch (RepeatSearchException ex) {
                RepeatSearch context = ex.getContext();
                LOG.debug("Repeat search from '{}' to '{}'", query, context.getQuery());
                List<Paper> results = query(context.getQuery());
                if(results != null) results.forEach(context::updatePaper);
                // TODO: save in cache
                return results;
            } catch (Exception e) {
                LOG.error("Repository failed " + repo.getClass().getSimpleName(), e);
                return Collections.emptyList();
            }
        } else {
            return null;
        }
    }
}
