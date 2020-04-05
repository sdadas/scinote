package com.sdadas.scinote.cache.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Sławomir Dadas
 */
public class Cached<T> implements Serializable {

    private String key;

    private String name;

    private LocalDateTime updated;

    private T value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
