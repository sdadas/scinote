package com.sdadas.scinote.shared.model.project;

import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sławomir Dadas
 */
public class Project implements Serializable {

    private String id;

    private String title;

    private List<String> tags;

    private Set<ProjectPaper> accepted = new LinkedHashSet<>();

    private Set<ProjectPaper> rejected = new LinkedHashSet<>();

    private Set<ProjectPaper> readLater = new LinkedHashSet<>();

    private ProjectSuggestions suggestions = new ProjectSuggestions();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Set<ProjectPaper> getAccepted() {
        return accepted;
    }

    public void setAccepted(Set<ProjectPaper> accepted) {
        this.accepted = accepted;
    }

    public Set<ProjectPaper> getRejected() {
        return rejected;
    }

    public void setRejected(Set<ProjectPaper> rejected) {
        this.rejected = rejected;
    }

    public Set<ProjectPaper> getReadLater() {
        return readLater;
    }

    public void setReadLater(Set<ProjectPaper> readLater) {
        this.readLater = readLater;
    }

    public ProjectSuggestions getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(ProjectSuggestions suggestions) {
        this.suggestions = suggestions;
    }

    public void accept(Paper paper) {
        PaperId paperId = paper.firstPaperId();
        ProjectPaper pp = new ProjectPaper(paperId);
        pp = ObjectUtils.firstNonNull(remove(pp, rejected, readLater), pp);
        boolean exists = accepted.contains(pp);
        if(!exists) {
            accepted.add(pp);
            suggestions.addRefsFromPaper(paper, this);
        }
    }

    public void reject(Paper paper) {
        PaperId paperId = paper.firstPaperId();
        ProjectPaper pp = new ProjectPaper(paperId);
        ProjectPaper removed = remove(pp, accepted);
        pp = ObjectUtils.firstNonNull(removed, remove(pp, readLater), pp);
        suggestions.remove(paperId);
        boolean exists = rejected.contains(pp);
        if(!exists) {
            rejected.add(pp);
        }
        if(removed != null) {
            suggestions.removeRefsFromPaper(paper);
        }
    }

    public void readLater(Paper paper) {
        PaperId paperId = paper.firstPaperId();
        ProjectPaper pp = new ProjectPaper(paperId);
        ProjectPaper removed = remove(pp, accepted);
        pp = ObjectUtils.firstNonNull(removed, remove(pp, rejected), pp);
        suggestions.remove(paperId);
        boolean exists = readLater.contains(pp);
        if(!exists) {
            readLater.add(pp);
        }
        if(removed != null) {
            suggestions.removeRefsFromPaper(paper);
        }
    }

    @SafeVarargs
    public final ProjectPaper remove(ProjectPaper pp, Set<ProjectPaper>... sets) {
        for (Set<ProjectPaper> set : sets) {
            boolean contains = set.contains(pp);
            if(contains) {
                ProjectPaper result = set.stream().filter(val -> val.equals(pp)).findAny().orElse(null);
                set.remove(pp);
                return result;
            }
        }
        return null;
    }

    public Set<PaperId> getTopCandidatesRefsFirst(int topN) {
        List<PaperId> refIds = this.suggestions.getReferences().topN(topN);
        Set<PaperId> results = new HashSet<>(refIds);
        if(results.size() < topN) {
            List<PaperId> revRefIds = this.suggestions.getReverseReferences().topN(topN);
            int idx = 0;
            while(results.size() < topN && idx < revRefIds.size()) {
                results.add(revRefIds.get(idx));
                idx++;
            }
        }
        return results;
    }
}