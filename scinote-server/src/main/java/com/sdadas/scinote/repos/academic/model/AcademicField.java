package com.sdadas.scinote.repos.academic.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
public enum AcademicField {

    ENTITY_ID("Id"),
    TITLE("Ti"),
    LANGUAGE("L"), /* Removed */
    YEAR("Y"),
    DATE("D"),
    CITATIONS("CC"),
    ESTIMATED_CITATIONS("ECC"),
    AUTHORS("AA"),
    AUTHOR_NAME("AA.AuN", "AA.AuN"),
    AUTHOR_ORIGINAL_NAME("AA.DAuN","AA.DAuN"),
    AUTHOR_ID("AA.AuId", "AA.AuId"),
    AUTHOR_AFFILIATION("AA.AfN", "AA.AfN"),
    AUTHOR_ORIGINAL_AFFILIATION("AA.DAfN", "AA.DAfN"),
    AUTHOR_AFFILIATION_ID("AA.AfId", "AA.AfId"),
    AUTHOR_ORDER("AA.S", "AA.S"),
    FOS_NAME("FN", "F.FN"),
    FOS_ID("FId", "F.FId"),
    JOURNAL_NAME("JN", "J.JN"),
    JOURNAL_ID("JId", "J.JId"),
    CONFERENCE_NAME("CN", "C.CN"),
    CONFERENCE_ID("CId", "C.CId"),
    REFERENCES("RId"),
    PAPER_WORDS("W"),
    EXT("E"),
    EXT_TITLE("DN"),
    EXT_SOURCES("S"),
    EXT_SOURCE_TYPE("Ty", "S.Ty"),
    EXT_SOURCE_URL("U", "S.U"),
    EXT_VENUE_FULL("VFN"),
    EXT_VENUE_SHORT("VSN"),
    EXT_VOLUME("V"),
    EXT_JOURNAL("BV"),
    EXT_PUBLISHER("PB"),
    EXT_ISSUE("I"),
    EXT_FIRST_PAGE("FP"),
    EXT_LAST_PAGE("LP"),
    EXT_DOI("DOI"),
    EXT_CITATION_CTX("CC"),
    EXT_INVERTED_ABSTRACT("IA"),
    EXT_BT("BT"),
    EXT_IA_LENGTH("IndexLength", "IA.IndexLength"),
    EXT_IA_INDEX("InvertedIndex", "IA.InvertedIndex");

    public static final String ENTITY_SCOPE = entityScope();

    private final String code;

    private final String fullCode;

    AcademicField(String code) {
        this.code = code;
        this.fullCode = code;
    }

    AcademicField(String code, String fullCode) {
        this.code = code;
        this.fullCode = fullCode;
    }

    public String code() {
        return this.code;
    }

    public String codeFragment() {
        if(StringUtils.contains(this.code, '.'))  {
            return StringUtils.substringAfterLast(this.code, ".");
        } else {
            return this.code;
        }
    }

    public String fullCode() {
        return this.fullCode;
    }

    private static String entityScope() {
        List<AcademicField> fields = new ArrayList<>();
        fields.add(AcademicField.ENTITY_ID);
        fields.add(AcademicField.TITLE);
        fields.add(AcademicField.YEAR);
        fields.add(AcademicField.DATE);
        fields.add(AcademicField.CITATIONS);
        fields.add(AcademicField.ESTIMATED_CITATIONS);
        fields.add(AcademicField.AUTHOR_NAME);
        fields.add(AcademicField.AUTHOR_ORIGINAL_NAME);
        fields.add(AcademicField.AUTHOR_ORIGINAL_AFFILIATION);
        fields.add(AcademicField.AUTHOR_ORDER);
        fields.add(AcademicField.REFERENCES);
        fields.add(AcademicField.EXT_SOURCES);
        fields.add(AcademicField.EXT_VOLUME);
        fields.add(AcademicField.EXT_ISSUE);
        fields.add(AcademicField.EXT_PUBLISHER);
        fields.add(AcademicField.EXT_VENUE_SHORT);
        fields.add(AcademicField.EXT_VENUE_FULL);
        fields.add(AcademicField.EXT_FIRST_PAGE);
        fields.add(AcademicField.EXT_LAST_PAGE);
        fields.add(AcademicField.EXT_JOURNAL);
        fields.add(AcademicField.EXT_BT);
        fields.add(AcademicField.EXT_TITLE);
        fields.add(AcademicField.EXT_TITLE);
        fields.add(AcademicField.EXT_DOI);
        return fields.stream().map(AcademicField::code).collect(Collectors.joining(","));
    }
}
