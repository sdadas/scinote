package com.sdadas.scinote.shared.model.validation;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class ActionResponse implements Serializable {

    private List<String> errors = new ArrayList<>();

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void error(String message) {
        errors.add(message);
    }

    public void notBlank(Object value, String field) {
        String message = String.format("'%s' is empty", field);
        if(value instanceof String) {
            if(StringUtils.isBlank((CharSequence) value)) {
                error(message);
            }
        } else if(value == null) {
            error(message);
        }
    }
}
