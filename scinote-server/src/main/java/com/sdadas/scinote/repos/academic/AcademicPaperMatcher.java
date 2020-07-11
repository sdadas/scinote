package com.sdadas.scinote.repos.academic;

import com.sdadas.scinote.repos.academic.model.AcademicPaper;
import com.sdadas.scinote.shared.model.paper.Author;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import com.sdadas.scinote.shared.model.paper.WebLocation;
import cz.jirutka.unidecode.Unidecode;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
public class AcademicPaperMatcher implements Serializable {

    private final Paper original;

    private final List<Paper> candidates;

    public AcademicPaperMatcher(Paper original, List<Paper> candidates) {
        this.original = original;
        this.candidates = candidates;
    }

    public Paper match() {
        if(candidates == null || candidates.isEmpty()) {
            return original;
        }
        String repo = original.firstPaperId().getRepo();
        boolean isUntrustedSource = StringUtils.equalsAny(repo, "url", "spv2", "grobid");
        Optional<PaperMatch> bestMatch = candidates.stream()
                .map(val -> new PaperMatch(original, val, isUntrustedSource))
                .filter(val -> val.matches)
                .min((o1, o2) -> Integer.compare(o2.score, o1.score));
        if(bestMatch.isPresent()) {
            AcademicPaper matched = (AcademicPaper) bestMatch.get().second;
            return convert(matched, isUntrustedSource);
        } else {
            return original;
        }
    }

    private Paper convert(AcademicPaper matched, boolean isUntrustedSource) {
        AcademicPaper result = new AcademicPaper();
        BeanUtils.copyProperties(original, result);
        if(isUntrustedSource) {
            result.setTitle(matched.getTitle());
            result.setAuthors(matched.getAuthors());
            result.setSource(matched.getSource());
            result.setYear(matched.getYear());
        }
        result.getIds().addAll(matched.getIds());
        result.setCitations(matched.getCitations());
        result.setEstimatedCitations(matched.getEstimatedCitations());
        result.setBt(matched.getBt());
        result.setReferences(matched.getReferences());
        result.setReverseReferences(matched.getReverseReferences());
        List<WebLocation> matchedUrls = ObjectUtils.firstNonNull(matched.getUrls(), new ArrayList<>());
        List<WebLocation> resultUrls = result.getUrls();
        if(resultUrls == null || resultUrls.isEmpty()) {
            result.setUrls(matched.getUrls());
        } else {
            matchedUrls.forEach(url -> { if(!resultUrls.contains(url)) resultUrls.add(url); });
        }
        return result;
    }

    private static class PaperMatch {

        private final Paper first;

        private final Paper second;

        private final boolean isUntrustedSource;

        private final boolean matches;

        private final int score;

        private PaperMatch(Paper first, Paper second, boolean isUntrustedSource) {
            this.first = first;
            this.second = second;
            this.isUntrustedSource = isUntrustedSource;
            this.matches = computeMatches();
            this.score = computeScore();
        }

        private boolean computeMatches() {
            if(first.getYear() != null && second.getYear() != null) {
                if(Math.abs(first.getYear() - second.getYear()) > 1) return false;
            }
            return authorsMatch();
        }

        private boolean authorsMatch() {
            List<Author> firstAuthors = first.getAuthors();
            List<Author> secondAuthors = second.getAuthors();
            if(firstAuthors == null || firstAuthors.isEmpty()) return false;
            if(secondAuthors == null || secondAuthors.isEmpty()) return false;
            Set<String> words = secondAuthors.stream()
                    .map(val -> normalize(val.toString()))
                    .flatMap(val -> Arrays.stream(StringUtils.split(val)))
                    .collect(Collectors.toSet());

            int errors = 0;
            for (Author author : firstAuthors) {
                String name = normalize(author.toString());
                String[] split = StringUtils.split(name);
                boolean anyMatch = false;
                for (String word : split) {
                    if (words.contains(word)) {
                        anyMatch = true;
                        break;
                    }
                }
                if(!anyMatch) {
                    if(isUntrustedSource) {
                        errors++;
                        if(errors > 2) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }

        private int computeScore() {
            int score = 0;
            if(second.getCitations() != null && second.getCitations() > 0) {
                score++;
            }
            score = scoreUrls(score);
            return score;
        }

        private int scoreUrls(int score) {
            int result = score;
            if(first.getUrls() == null || second.getUrls() == null) return result;
            Set<WebLocation> firstUrls = new HashSet<>(first.getUrls());
            Set<WebLocation> secondUrls = new HashSet<>(second.getUrls());
            for (WebLocation url : firstUrls) {
                if(secondUrls.contains(url)) {
                    result += 2;
                }
            }
            Set<String> firstDomains = firstUrls.stream().map(WebLocation::domain).collect(Collectors.toSet());
            Set<String> secondDomains = secondUrls.stream().map(WebLocation::domain).collect(Collectors.toSet());
            for (String domain : firstDomains) {
                if(domain != null && secondDomains.contains(domain)) {
                    result += 1;
                }
            }
            return result;
        }

        private String normalize(String text) {
            String res = Unidecode.toAscii().decode(text);
            res = StringUtils.lowerCase(res);
            return res.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}]", " ").replaceAll("\\s+", " ");
        }

    }
}
