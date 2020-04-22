package com.sdadas.scinote.shared.model.paper;

import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author Sławomir Dadas
 */
public class WebLocation implements Serializable {

    private String name;

    private String url;

    public WebLocation() {
    }

    public WebLocation(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @SuppressWarnings("UnstableApiUsage")
    public String domain() {
        try {
            URI uri = new URI(url);
            if(StringUtils.isBlank(uri.getHost())) {
                return null;
            }
            try {
                InternetDomainName domain = InternetDomainName.from(uri.getHost());
                if(domain.isUnderRegistrySuffix()) {
                    return domain.topDomainUnderRegistrySuffix().toString().toLowerCase();
                } else {
                    return uri.getHost();
                }
            } catch (Exception ex) {
                return uri.getHost();
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebLocation that = (WebLocation) o;
        return StringUtils.equalsIgnoreCase(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url.toLowerCase());
    }
}
