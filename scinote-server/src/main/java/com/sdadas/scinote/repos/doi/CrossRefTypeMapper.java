package com.sdadas.scinote.repos.doi;

import com.sdadas.scinote.repos.shared.model.PaperType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sławomir Dadas
 */
public class CrossRefTypeMapper {

    private Map<String, PaperType> mapping = createMapping();

    private Map<String, PaperType> createMapping() {
        Map<String, PaperType> res = new HashMap<>();
        res.put("book-section", PaperType.IN_BOOK);
        res.put("monograph", PaperType.BOOK);
        res.put("report", PaperType.TECHREPORT);
        res.put("peer-review", PaperType.MISC);
        res.put("book-track", PaperType.COLLECTION);
        res.put("journal-article", PaperType.ARTICLE);
        res.put("book-part", PaperType.IN_BOOK);
        res.put("other", PaperType.MISC);
        res.put("book", PaperType.BOOK);
        res.put("journal-volume", PaperType.COLLECTION);
        res.put("book-set", PaperType.COLLECTION);
        res.put("reference-entry", PaperType.MISC);
        res.put("proceedings-article", PaperType.IN_PROCEEDINGS);
        res.put("journal", PaperType.COLLECTION);
        res.put("component", PaperType.MISC);
        res.put("book-chapter", PaperType.IN_BOOK);
        res.put("proceedings-series", PaperType.PROCEEDINGS);
        res.put("report-series", PaperType.COLLECTION);
        res.put("proceedings", PaperType.PROCEEDINGS);
        res.put("standard", PaperType.MISC);
        res.put("reference-book", PaperType.MISC);
        res.put("posted-content", PaperType.ARTICLE);
        res.put("journal-issue", PaperType.COLLECTION);
        res.put("dissertation", PaperType.PHDTHESIS);
        res.put("dataset", PaperType.MISC);
        res.put("book-series", PaperType.COLLECTION);
        res.put("edited-book", PaperType.BOOK);
        res.put("standard-series", PaperType.COLLECTION);
        return res;
    }

    public PaperType map(String type) {
        if(StringUtils.isBlank(type)) return PaperType.ARTICLE;
        PaperType res = mapping.get(type);
        return ObjectUtils.firstNonNull(res, PaperType.ARTICLE);
    }
}
