package com.sdadas.scinote.repos.parse;

import com.sdadas.scinote.repos.parse.model.Spv2Doc;
import com.sdadas.scinote.repos.parse.model.Spv2Response;
import com.sdadas.scinote.repos.shared.exception.ExternalServiceException;
import com.sdadas.scinote.shared.model.paper.Author;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import com.sdadas.scinote.shared.model.paper.PaperType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class Spv2PaperBuilder {

    private final Spv2Response response;

    private final String filename;

    public Spv2PaperBuilder(Spv2Response response, String filename) {
        this.response = response;
        this.filename = filename;
    }

    public Paper createPaper() {
        Spv2Doc doc = response.getDoc();
        if(doc == null) throw new ExternalServiceException("Could not parse PDF");
        Paper paper = new Paper();
        paper.addId(new PaperId("spv2", doc.getDocSha()));
        paper.setTitle(doc.getTitle());
        paper.setAuthors(createAuthors(doc));
        paper.getSource().setName(filename);
        paper.setType(PaperType.ARTICLE);
        if(StringUtils.isBlank(paper.getTitle()) || paper.getAuthors().isEmpty()) {
            throw new ExternalServiceException("Could not parse PDF");
        }
        return paper;
    }

    private List<Author> createAuthors(Spv2Doc doc) {
        List<String> authors = doc.getAuthors();
        if(authors == null) return new ArrayList<>();
        List<Author> results = new ArrayList<>();
        int idx = 0;
        for (String author : authors) {
            author = StringUtils.strip(author);
            String firstName = StringUtils.substringBeforeLast(author, " ");
            String lastName = StringUtils.substringAfterLast(author, " ");
            results.add(new Author(firstName, lastName, idx));
            idx++;
        }
        return results;
    }
}
