package com.sdadas.scinote.repos.shared.utils;

import com.sdadas.scinote.repos.shared.exception.ExternalServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
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

    protected <T> RequestEntity<T> request(String url, MultiValueMap<String, String> params, T body) {
        if(StringUtils.contains(url, '#')) url = StringUtils.substringBefore(url, "#");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if(params != null) builder.queryParams(params);
        UriComponents components = builder.build();
        String encoded = components.encode(StandardCharsets.UTF_8).toUriString();
        encoded = StringUtils.removeEnd(encoded, ",");
        HttpMethod method = HttpMethod.POST;
        if(body == null || (body instanceof String && StringUtils.isBlank((String) body))) {
            method = HttpMethod.GET;
        }
        return new RequestEntity<T>(body, headers(), method, URI.create(encoded));
    }

    protected  <T> ResponseEntity<T> exchange(RequestEntity<?> entity, Class<T> clazz) {
        try {
            return client.exchange(entity, clazz);
        } catch (HttpStatusCodeException ex) {
            throw new ExternalServiceException("Invalid http status", ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("Invalid http response", ex);
        }
    }
}
