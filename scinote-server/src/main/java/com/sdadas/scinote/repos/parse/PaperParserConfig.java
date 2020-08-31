package com.sdadas.scinote.repos.parse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Sławomir Dadas
 */
@Component
@ConfigurationProperties(prefix = "paper.parser")
public class PaperParserConfig {

    private String spv2Url;

    private String grobidHome;

    public String getSpv2Url() {
        return spv2Url;
    }

    public void setSpv2Url(String spv2Url) {
        this.spv2Url = spv2Url;
    }

    public String getGrobidHome() {
        return grobidHome;
    }

    public void setGrobidHome(String grobidHome) {
        this.grobidHome = grobidHome;
    }
}
