package com.sdadas.scinote.repos.parse.spv2.rest;

import com.sdadas.scinote.repos.parse.spv2.model.Spv2Response;
import com.sdadas.scinote.repos.shared.utils.RestClientBase;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Sławomir Dadas
 */
public class Spv2RestClient extends RestClientBase {

    private final String endpointUrl;

    public Spv2RestClient(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Override
    protected HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    public Spv2Response parse(Resource resource) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        RequestEntity<MultiValueMap<String, Object>> request = request(endpointUrl, null, body);
        ResponseEntity<Spv2Response> response = exchange(request, Spv2Response.class);
        return response.getBody();
    }
}
