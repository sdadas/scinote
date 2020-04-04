package com.sdadas.scinote.repos.doi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class CrossRefResponse implements Serializable {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message-type")
    private String messageType;

    @JsonProperty("message-version")
    private String messageVersion;

    @JsonProperty("message")
    private CrossRefPaper message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }

    public CrossRefPaper getMessage() {
        return message;
    }

    public void setMessage(CrossRefPaper message) {
        this.message = message;
    }
}
