package com.sdadas.scinote.repos.academic.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Sławomir Dadas
 */
@Component
@ConfigurationProperties(prefix = "academic.search")
public class AcademicRestClientConfig {

    private String url;

    private String secret;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
