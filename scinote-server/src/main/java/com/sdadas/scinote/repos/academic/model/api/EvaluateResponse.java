package com.sdadas.scinote.repos.academic.model.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sławomir Dadas
 */
public class EvaluateResponse implements Serializable {

    private String expr;

    private List<Map<String, Object>> entities = new ArrayList<>();

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public List<Map<String, Object>> getEntities() {
        return entities;
    }

    public void setEntities(List<Map<String, Object>> entities) {
        this.entities = entities;
    }
}
