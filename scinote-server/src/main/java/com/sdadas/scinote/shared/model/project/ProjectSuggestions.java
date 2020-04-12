package com.sdadas.scinote.shared.model.project;

import com.sdadas.scinote.repos.academic.model.AcademicPaper;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sławomir Dadas
 */
public class ProjectSuggestions implements Serializable {

    private Candidates references = new Candidates();

    private Candidates reverseReferences = new Candidates();

    public Candidates getReferences() {
        return references;
    }

    public void setReferences(Candidates references) {
        this.references = references;
    }

    public Candidates getReverseReferences() {
        return reverseReferences;
    }

    public void setReverseReferences(Candidates reverseReferences) {
        this.reverseReferences = reverseReferences;
    }

    void remove(List<PaperId> ids) {
        remove(references, ids);
        remove(reverseReferences, ids);
    }

    void remove(PaperId id) {
        remove(Collections.singletonList(id));
    }

    void addRefsFromPaper(Paper paper, Project project) {
        if(paper instanceof AcademicPaper) {
            AcademicPaper ap = (AcademicPaper) paper;
            add(references, ap.referencesAsPaperIds(), project);
            add(reverseReferences, ap.reverseReferencesAsPaperIds(), project);
        }
    }

    void removeRefsFromPaper(Paper paper) {
        if(paper instanceof AcademicPaper) {
            AcademicPaper ap = (AcademicPaper) paper;
            remove(references, ap.referencesAsPaperIds());
            remove(reverseReferences, ap.reverseReferencesAsPaperIds());
        }
    }

    private void remove(Candidates candidates, List<PaperId> ids) {
        if(ids != null) {
            Set<PaperId> refs = new HashSet<>(ids);
            refs.forEach(candidates::decrement);
        }
    }

    private void add(Candidates candidates, List<PaperId> ids, Project p) {
        for (PaperId id : ids) {
            ProjectPaper pp = new ProjectPaper(id);
            boolean exists = p.getAccepted().contains(pp) || p.getRejected().contains(pp) || p.getReadLater().contains(pp);
            if(!exists) candidates.increment(pp.getId());
        }
    }
}
