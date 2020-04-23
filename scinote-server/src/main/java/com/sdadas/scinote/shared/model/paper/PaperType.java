package com.sdadas.scinote.shared.model.paper;

import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;

/**
 * @author Sławomir Dadas
 */
public enum PaperType {
    ARTICLE(BibTeXEntry.TYPE_ARTICLE),
    IN_PROCEEDINGS(BibTeXEntry.TYPE_INPROCEEDINGS),
    PROCEEDINGS(BibTeXEntry.TYPE_PROCEEDINGS),
    COLLECTION(BibTeXEntry.TYPE_PROCEEDINGS),
    BOOK(BibTeXEntry.TYPE_BOOK),
    IN_BOOK(BibTeXEntry.TYPE_INBOOK),
    IN_COLLECTION(BibTeXEntry.TYPE_INCOLLECTION),
    MISC(BibTeXEntry.TYPE_MISC),
    PHDTHESIS(BibTeXEntry.TYPE_PHDTHESIS),
    TECHREPORT(BibTeXEntry.TYPE_TECHREPORT);

    private final Key key;

    PaperType(Key key) {
        this.key = key;
    }

    public Key key() {
        return this.key;
    }
}
