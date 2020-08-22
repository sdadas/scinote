package com.sdadas.scinote.repos.academic.model;

import com.sdadas.scinote.shared.model.paper.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
public class AcademicPaper extends Paper {

    private Integer estimatedCitations;

    private List<Long> references;

    private List<Long> reverseReferences;

    private String bt;

    public AcademicPaper() {
    }

    @SuppressWarnings("unchecked")
    public AcademicPaper(Map<String, Object> entity) {
        PaperId id = new PaperId("academic", entity.get(AcademicField.ENTITY_ID.code()).toString());
        this.addId(id);
        this.setYear((Integer) entity.get(AcademicField.YEAR.code()));
        Object dateValue = entity.get(AcademicField.DATE.code());
        if(dateValue != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.setDate(LocalDate.parse(dateValue.toString(), formatter));
        }
        this.setCitations((Integer) entity.get(AcademicField.CITATIONS.code()));
        this.setEstimatedCitations((Integer) entity.get(AcademicField.ESTIMATED_CITATIONS.code()));
        this.setTitleNorm((String) entity.get(AcademicField.TITLE.code()));
        this.setTitle((String) entity.get(AcademicField.EXT_TITLE.code()));
        String lang = (String) entity.get(AcademicField.LANGUAGE.code());
        if(StringUtils.isNotBlank(lang)) {
            this.setLanguages(Arrays.asList(StringUtils.split(lang, "@@@")));
        }
        this.setUrls(createWebLocations((List<Map<String, Object>>) entity.get(AcademicField.EXT_SOURCES.code())));
        this.setReferences(createReferences((List<Object>) entity.get(AcademicField.REFERENCES.code())));
        this.setAuthors(createAuthors((List<Map<String, Object>>) entity.get("AA")));
        this.setSource(createSource(entity));
        Integer firstPage = createNumericValue(entity.get(AcademicField.EXT_FIRST_PAGE.code()));
        Integer lastPage = createNumericValue(entity.get(AcademicField.EXT_LAST_PAGE.code()));
        if(firstPage != null && lastPage != null) {
            this.setPages(String.format("%d-%d", firstPage, lastPage));
        } else if(firstPage != null) {
            this.setPages(firstPage.toString());
        } else if(lastPage != null) {
            this.setPages(lastPage.toString());
        }
        this.setDoi(StringUtils.stripToNull((String) entity.get(AcademicField.EXT_DOI.code())));
        this.setBt((String) entity.get(AcademicField.EXT_BT.code()));
        this.setType(PaperType.ARTICLE);
    }

    private Source createSource(Map<String, Object> entity) {
        Source source = new Source();
        source.setVenue((String) entity.get(AcademicField.EXT_VENUE_FULL.code()));
        source.setVenueShort((String) entity.get(AcademicField.EXT_VENUE_SHORT.code()));
        source.setPublisher((String) entity.get(AcademicField.EXT_PUBLISHER.code()));
        source.setName((String) entity.get(AcademicField.EXT_JOURNAL.code()));
        if(StringUtils.isBlank(source.getName()) && StringUtils.isNotBlank(source.getVenue())) {
            source.setName(source.getVenue());
        }
        return source;
    }

    private Integer createNumericValue(Object fpValue) {
        if(fpValue != null) {
            if(fpValue instanceof Number) {
                return ((Number) fpValue).intValue();
            } else {
                String value = StringUtils.strip(fpValue.toString());
                if (StringUtils.isNumeric(value)) {
                    return Integer.parseInt(value, 10);
                }
            }
        }
        return null;
    }

    private List<WebLocation> createWebLocations(List<Map<String, Object>> values) {
        if(values == null) return new ArrayList<>();
        List<WebLocation> results = new ArrayList<>();
        for (Map<String, Object> entity : values) {
            Object type = entity.get(AcademicField.EXT_SOURCE_TYPE.code());
            WebLocation location = new WebLocation();
            location.setName(type != null ? type.toString() : "0");
            location.setUrl(entity.get(AcademicField.EXT_SOURCE_URL.code()).toString());
            results.add(location);
        }
        return results;
    }

    private List<Long> createReferences(List<Object> values) {
        if(values == null) return new ArrayList<>();
        return values.stream().map(val -> ((Number) val).longValue()).collect(Collectors.toList());
    }

    private List<Author> createAuthors(List<Map<String, Object>> values) {
        if(values == null) return new ArrayList<>();
        List<Author> results = new ArrayList<>();
        for (Map<String, Object> entity : values) {
            Author author = new Author();
            Number idValue = (Number) entity.get(AcademicField.AUTHOR_ID.codeFragment());
            if(idValue != null) {
                author.setId(idValue.toString());
            }
            String fullName = (String) entity.get(AcademicField.AUTHOR_ORIGINAL_NAME.codeFragment());
            String firstName = StringUtils.substringBeforeLast(fullName, " ");
            String lastName = StringUtils.substringAfterLast(fullName, " ");
            author.setFirstName(firstName);
            author.setLastName(lastName);
            String affiliation = (String) entity.get(AcademicField.AUTHOR_ORIGINAL_AFFILIATION.codeFragment());
            author.setAffiliation(StringUtils.stripToNull(affiliation));
            author.setOrder((Integer) entity.get(AcademicField.AUTHOR_ORDER.codeFragment()));
            results.add(author);
        }
        return results;
    }

    public Integer getEstimatedCitations() {
        return estimatedCitations;
    }

    public void setEstimatedCitations(Integer estimatedCitations) {
        this.estimatedCitations = estimatedCitations;
    }

    public List<Long> getReferences() {
        return references;
    }

    public void setReferences(List<Long> references) {
        this.references = references;
    }

    public List<Long> getReverseReferences() {
        return reverseReferences;
    }

    public void setReverseReferences(List<Long> reverseReferences) {
        this.reverseReferences = reverseReferences;
    }

    public String getBt() {
        return bt;
    }

    public void setBt(String bt) {
        this.bt = bt;
    }

    public List<PaperId> referencesAsPaperIds() {
        if(references == null) return new ArrayList<>();
        return references.stream().map(ref -> new PaperId("academic", String.valueOf(ref))).collect(Collectors.toList());
    }

    public List<PaperId> reverseReferencesAsPaperIds() {
        if(reverseReferences == null) return new ArrayList<>();
        return reverseReferences.stream().map(ref -> new PaperId("academic", String.valueOf(ref))).collect(Collectors.toList());
    }
}
