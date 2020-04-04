package com.sdadas.scinote.repos.academic.model;

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
    AUTHOR_NAME("AuN", "AA.AuN"),
    AUTHOR_ID("AuId", "AA.AuId"),
    AUTHOR_AFFILIATION("AfN", "AA.AfN"),
    AUTHOR_AFFILIATION_ID("AfId", "AA.AfId"),
    AUTHOR_ORDER("S", "AA.S"),
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
    EXT_SOUCES("S"),
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
        fields.add(AcademicField.EXT);
        fields.add(AcademicField.AUTHOR_NAME);
        fields.add(AcademicField.REFERENCES);
        return fields.stream().map(AcademicField::code).collect(Collectors.joining(","));
    }
}
