package com.sdadas.scinote.repos.arxiv;

import com.sdadas.scinote.repos.arxiv.model.ArxivAuthor;
import com.sdadas.scinote.repos.arxiv.model.ArxivEntry;
import com.sdadas.scinote.repos.arxiv.model.ArxivLink;
import com.sdadas.scinote.repos.arxiv.model.ArxivResponse;
import com.sdadas.scinote.shared.model.paper.Author;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import com.sdadas.scinote.shared.model.paper.WebLocation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sławomir Dadas
 */
public class ArxivPaperBuilder {

    private final ArxivResponse response;

    private final String arxivId;

    public ArxivPaperBuilder(ArxivResponse response, String arxivId) {
        this.response = response;
        this.arxivId = arxivId;
    }

    public Paper createPaper() {
        if(response.getEntries() == null || response.getEntries().isEmpty()) return null;
        ArxivEntry entry = response.getEntries().get(0);
        Paper paper = new Paper();
        paper.addId(new PaperId("arxiv", arxivId));
        paper.setTitle(entry.getTitle().replaceAll("\\s+", " "));
        paper.setSummary(entry.getSummary());
        paper.setDate(entry.getPublished().toLocalDate());
        paper.setYear(entry.getPublished().getYear());
        for (ArxivLink link : entry.getLinks()) {
            paper.addUrl(new WebLocation(ObjectUtils.firstNonNull(link.getTitle(), link.getType()), link.getHref()));
        }
        int idx = 0;
        for (ArxivAuthor author : entry.getAuthors()) {
            String name = author.getName();
            String firstName = StringUtils.substringBeforeLast(name, " ");
            String lastName = StringUtils.substringAfterLast(name, " ");
            paper.addAuthor(new Author(firstName, lastName, idx));
            idx++;
        }
        paper.getSource().setPublisher("arXiv");
        paper.getSource().setName("arXiv preprint arXiv:" + arxivId);
        return paper;
    }
}
