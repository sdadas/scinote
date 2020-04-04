package com.sdadas.scinote.repos.academic.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.sdadas.scinote.repos.academic.model.api.EvaluateRequest;
import com.sdadas.scinote.repos.academic.model.api.EvaluateResponse;
import com.sdadas.scinote.repos.shared.utils.RestClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author Sławomir Dadas
 */
@Component
public class AcademicRestClient extends RestClientBase {

    private static final Logger LOG = LoggerFactory.getLogger(AcademicRestClient.class);

    private static final String API_KEY_HEADER = "Ocp-Apim-Subscription-Key";

    private final ObjectMapper mapper;

    private final AcademicRestClientConfig config;

    @SuppressWarnings("all")
    private final RateLimiter limiter;

    @Autowired
    @SuppressWarnings("all")
    public AcademicRestClient(AcademicRestClientConfig config, ObjectMapper mapper) {
        this.mapper = mapper;
        this.config = config;
        this.limiter = RateLimiter.create(1);
    }

    @SuppressWarnings("all")
    public EvaluateResponse evaluate(EvaluateRequest request) {
        RequestEntity<String> entity = request(path("evaluate"), request, "");
        limiter.acquire();
        String json;
        try {
            json = mapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            LOG.error("Error serializing to json", ex);
            json = null;
        }
        LOG.info("{} Request: {}", getClass().getSimpleName(), json);
        ResponseEntity<EvaluateResponse> response = exchange(entity, EvaluateResponse.class);
        return response.getBody();
    }

    public String path(String relative) {
        return config.getUrl() + relative;
    }

    @Override
    protected HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(API_KEY_HEADER, this.config.getSecret());
        return headers;
    }
}
