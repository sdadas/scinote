package com.sdadas.scinote.bibtex;

import com.sdadas.scinote.bibtex.utils.LatexEscaper;
import com.sdadas.scinote.shared.model.paper.Author;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.Source;
import cz.jirutka.unidecode.Unidecode;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbibtex.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@Service
public class BibtexServiceImpl implements BibtexService {

    private final static Pattern ALNUM = Pattern.compile("[\\p{IsAlphabetic}\\p{IsDigit}]+", Pattern.UNICODE_CASE);

    private final LatexEscaper latex = new LatexEscaper();

    @Override
    public String getBibTeX(Collection<Paper> papers) {
        BibtexContext context = new BibtexContext();
        papers.forEach(paper -> createBibtex(paper, context));
        BibTeXFormatter formatter = new BibTeXFormatter();
        formatter.setIndent("  ");
        StringWriter writer = new StringWriter();
        try {
            formatter.format(context.database, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void createBibtex(Paper paper, BibtexContext context) {
        String key = paperKey(paper, context);
        Key type = paper.getType() != null ? paper.getType().key() : BibTeXEntry.TYPE_ARTICLE;
        BibTeXEntry entry = new BibTeXEntry(type, new Key(key));
        entry.addField(BibTeXEntry.KEY_TITLE, value(paper.getTitle(), true));
        entry.addField(BibTeXEntry.KEY_AUTHOR, value(paperAuthors(paper)));
        entry.addField(BibTeXEntry.KEY_YEAR, value(Objects.toString(paper.getYear())));
        Source source = paper.getSource();
        String sourceName = ObjectUtils.firstNonNull(source.getName(), source.getVenue(), source.getVenueShort(), "");
        Value sourceValue = value(sourceName, true);
        if(type.equals(BibTeXEntry.TYPE_ARTICLE)) {
            entry.addField(BibTeXEntry.KEY_JOURNAL, sourceValue);
        } else if(type.equals(BibTeXEntry.TYPE_INPROCEEDINGS) || type.equals(BibTeXEntry.TYPE_INCOLLECTION)) {
            entry.addField(BibTeXEntry.KEY_BOOKTITLE, sourceValue);
        }
        if(StringUtils.isNotBlank(source.getVenue())) {
            entry.addField(BibTeXEntry.KEY_ORGANIZATION, value(source.getVenue()));
        }
        if(StringUtils.isNotBlank(paper.getPages())) {
            entry.addField(BibTeXEntry.KEY_PAGES, value(paper.getPages()));
        }
        if(StringUtils.isNotBlank(source.getPublisher())) {
            entry.addField(BibTeXEntry.KEY_PUBLISHER, value(source.getPublisher()));
        }
        context.keys.add(key);
        context.database.addObject(entry);
    }

    private Value value(String val) {
        return value(val, false);
    }

    private Value value(String val, boolean preserveCase) {
        String escaped = latex.apply(val);
        if(preserveCase) escaped = preserveCase(escaped);
        return new StringValue(escaped, StringValue.Style.BRACED);
    }

    private String preserveCase(String val) {
        if(StringUtils.isBlank(val)) {
            return val;
        }
        if(StringUtils.upperCase(val).equals(val)) {
            return val; // Ignore values that are all caps
        }
        Matcher matcher = ALNUM.matcher(val);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String found = matcher.group();
            if (StringUtils.isAllUpperCase(found) && StringUtils.length(found) > 1) {
                matcher.appendReplacement(buffer, "{" + found  + "}");
            } else {
                matcher.appendReplacement(buffer, found);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String paperAuthors(Paper paper) {
        List<Author> authors = paper.getAuthors();
        if(authors == null || authors.isEmpty()) return "";
        Function<Author, String> authorFunc = (val) -> String.format("%s, %s", val.getLastName(), val.getFirstName());
        return authors.stream().map(authorFunc).collect(Collectors.joining(" and "));
    }

    private String paperKey(Paper paper, BibtexContext context) {
        List<Author> authors = paper.getAuthors();
        String name = authors != null && authors.size() > 0 ? authors.get(0).getLastName() : null;
        Integer year = paper.getYear() != null ? paper.getYear() : 0;
        String titleWord = StringUtils.left(StringUtils.substringBefore(paper.getTitle(), " "), 15);
        String baseKey = String.format("%s%d%s", StringUtils.defaultIfBlank(name, "unknown"), year, titleWord);
        baseKey = Unidecode.toAscii().decode(baseKey);
        baseKey = baseKey.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}]", "").toLowerCase();
        String key = baseKey;
        int idx = 1;
        while(context.keys.contains(key)) {
            key = baseKey + idx;
            idx++;
        }
        return key;
    }

    private static class BibtexContext {
        private final BibTeXDatabase database = new BibTeXDatabase();
        private final Set<String> keys = new HashSet<>();
    }
}
