package com.sdadas.scinote.cache.h2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdadas.scinote.cache.model.Cached;
import com.sdadas.scinote.shared.Named;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Sławomir Dadas
 */
class CachedValue implements Serializable {

    private String key;

    private String name;

    private LocalDateTime updated;

    private String value;

    public CachedValue() {
    }

    public CachedValue(ObjectMapper mapper, String key, Object value) {
        this.key = key;
        if(value instanceof Named) {
            this.name = ((Named) value).name();
        }
        this.updated = LocalDateTime.now();
        try {
            this.value = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public <T> Cached<T> convert(ObjectMapper mapper, Class<T> type) {
        Cached<T> res = new Cached<>();
        res.setKey(key);
        res.setName(name);
        res.setUpdated(updated);

        if(value != null) {
            try {
                res.setValue(mapper.readValue(value, type));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
        }
        return res;
    }
}
