package com.sdadas.scinote.repos;

import com.sdadas.scinote.cache.CacheService;
import com.sdadas.scinote.cache.model.Cached;
import com.sdadas.scinote.repos.shared.model.PaperIdMapping;
import com.sdadas.scinote.repos.shared.model.Query;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@Component
class PaperCache {

    private final CacheService cache;

    PaperCache(CacheService cache) {
        this.cache = cache;
        this.initCaches();
    }

    private void initCaches() {
        cache.initCache(Paper.class);
        cache.initCache(Query.class);
        cache.initCache(PaperIdMapping.class);
    }

    public void savePaper(Paper paper) {
        List<PaperId> ids = paper.getIds();
        if(paper.getUid() == null) {
            paper.setUid(RandomStringUtils.randomAlphanumeric(50));
        }
        cache.put(paper.getUid(), paper, Paper.class);
        for (PaperId id : ids) {
            PaperIdMapping mapping = new PaperIdMapping(id, paper.getUid());
            cache.put(id.toString(), mapping, PaperIdMapping.class);
        }
    }

    public Paper getPaper(PaperId paperId) {
        String id = paperId.toString();
        Cached<PaperIdMapping> mapping = cache.get(id, PaperIdMapping.class);
        if(mapping != null) {
            String uid = mapping.getValue().getUid();
            Cached<Paper> paper = cache.get(uid, Paper.class);
            return paper != null ? paper.getValue() : null;
        }
        return null;
    }

    public List<Paper> getPapers(List<PaperId> paperIds) {
        List<String> ids = paperIds.stream().map(PaperId::toString).collect(Collectors.toList());
        List<Cached<PaperIdMapping>> cached = cache.get(ids, PaperIdMapping.class);
        List<String> uids = cached.stream().map(val -> val.getValue().getUid()).collect(Collectors.toList());
        List<Cached<Paper>> papers = cache.get(uids, Paper.class);
        return papers.stream().map(Cached::getValue).collect(Collectors.toList());
    }

    public void saveQuery(String q, List<Paper> papers) {
        Cached<Query> cached = cache.get(q, Query.class);
        if(cached != null) return;
        Query query = new Query();
        query.setQuery(q);
        query.setResults(papers.stream().map(val -> val.getIds().get(0)).collect(Collectors.toList()));
        cache.put(q, query, Query.class);
    }

    public Query getQuery(String q) {
        Cached<Query> query = cache.get(q, Query.class);
        return query != null ? query.getValue() : null;
    }
}
