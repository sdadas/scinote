package com.sdadas.scinote.repos.academic.model.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class EvaluateRequest implements Serializable, QueryParamSource {

    private String expr;

    private String model = "latest";

    private int count = 10;

    private int offset = 0;

    private String orderby;

    private String attributes;

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @Override
    public MultiValueMap<String, String> queryParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("expr", this.expr);
        params.add("model", this.model);
        params.add("count", String.valueOf(this.count));
        params.add("offset", String.valueOf(this.offset));
        if(this.orderby != null) params.add("orderby", this.orderby);
        params.add("attributes", StringUtils.stripToEmpty(StringUtils.join(this.attributes, ",")));
        return params;
    }
}
