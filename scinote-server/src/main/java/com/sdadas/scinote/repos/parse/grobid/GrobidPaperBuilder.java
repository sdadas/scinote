package com.sdadas.scinote.repos.parse.grobid;

import com.sdadas.scinote.shared.model.paper.*;
import org.grobid.core.data.BiblioItem;
import org.grobid.core.data.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class GrobidPaperBuilder {

    private final BiblioItem item;

    private final String filename;

    private final String paperId;

    public GrobidPaperBuilder(BiblioItem item, String filename, String paperId) {
        this.item = item;
        this.filename = filename;
        this.paperId = paperId;
    }

    public Paper createPaper() {
        Paper paper = new Paper();
        paper.addId(new PaperId("grobid", paperId));
        paper.setTitle(item.getTitle());
        paper.setSummary(item.getAbstract());
        paper.getSource().setName(filename);
        paper.setType(PaperType.ARTICLE);
        paper.setAuthors(createAuthors());
        paper.addUrl(new WebLocation(filename, "/pdf/" + paperId + ".pdf"));
        return paper;
    }

    private List<Author> createAuthors() {
        List<Person> authors = item.getFullAuthors();
        if(authors == null) return new ArrayList<>();
        List<Author> results = new ArrayList<>();
        int idx = 0;
        for (Person person : authors) {
            Author author = new Author();
            author.setFirstName(person.getFirstName());
            author.setLastName(person.getLastName());
            author.setOrder(idx);
            results.add(author);
            idx++;
        }
        return results;
    }
}
