package com.sdadas.scinote.repos.doi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @author Sławomir Dadas
 */
public class CrossRefDate {

    @JsonProperty("date-parts")
    private Object[] dateParts;

    @JsonProperty("date-time")
    private LocalDateTime dateTime;

    @JsonProperty("timestamp")
    private Long timestamp;

    public Object[] getDateParts() {
        return dateParts;
    }

    public void setDateParts(Object[] dateParts) {
        this.dateParts = dateParts;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer year() {
        if(dateTime != null) {
            return dateTime.getYear();
        } else if(dateParts != null && dateParts.length > 0) {
            Object part = dateParts[0];
            if(part instanceof Collection) {
                Collection<?> list = (Collection<?>) part;
                if(!list.isEmpty()) {
                    Object val = list.iterator().next();
                    if(val instanceof Number) {
                        return ((Number) val).intValue();
                    }
                }
            }
        }
        return null;
    }
}
