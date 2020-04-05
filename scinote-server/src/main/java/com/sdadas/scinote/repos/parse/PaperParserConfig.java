package com.sdadas.scinote.repos.parse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Sławomir Dadas
 */
@Component
@ConfigurationProperties(prefix = "paper.parser")
public class PaperParserConfig {

    private String url;

    private String storageDir;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }
}
