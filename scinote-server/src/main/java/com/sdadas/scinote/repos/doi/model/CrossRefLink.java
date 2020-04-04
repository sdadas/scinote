package com.sdadas.scinote.repos.doi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class CrossRefLink implements Serializable {

    @JsonProperty("URL")
    private String url;

    @JsonProperty("content-type")
    private String contentType;

    @JsonProperty("content-version")
    private String contentVersion;

    @JsonProperty("intended-application")
    private String intendedApplication;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentVersion() {
        return contentVersion;
    }

    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    public String getIntendedApplication() {
        return intendedApplication;
    }

    public void setIntendedApplication(String intendedApplication) {
        this.intendedApplication = intendedApplication;
    }
}
