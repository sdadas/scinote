package com.sdadas.scinote.repos.shared.utils;

import com.sdadas.scinote.repos.academic.model.api.QueryParamSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author Sławomir Dadas
 */
public abstract class RestClientBase {

    private final RestTemplate client;

    public RestClientBase() {
        this.client = new RestTemplate();
    }

    protected HttpHeaders headers() {
        return new HttpHeaders();
    }

    protected RequestEntity<String> request(String url, QueryParamSource query, String body) {
        if(StringUtils.contains(url, '#')) url = StringUtils.substringBefore(url, "#");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if(query != null) builder.queryParams(query.queryParams());
        UriComponents components = builder.build();
        String encoded = components.encode(StandardCharsets.UTF_8).toUriString();
        encoded = StringUtils.removeEnd(encoded, ",");
        HttpMethod method = StringUtils.isBlank(body) ? HttpMethod.GET : HttpMethod.POST;
        return new RequestEntity<>(body, headers(), method, URI.create(encoded));
    }

    protected  <T> ResponseEntity<T> exchange(RequestEntity<?> entity, Class<T> clazz) {
        try {
            return client.exchange(entity, clazz);
        } catch (HttpStatusCodeException ex) {
            throw new IllegalStateException(ex.getResponseBodyAsString(), ex);
        }
    }
}
