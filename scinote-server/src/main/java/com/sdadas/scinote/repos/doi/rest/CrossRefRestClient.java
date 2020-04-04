package com.sdadas.scinote.repos.doi.rest;

import com.sdadas.scinote.repos.doi.model.CrossRefResponse;
import com.sdadas.scinote.repos.shared.utils.RestClientBase;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author Sławomir Dadas
 */
@Component
public class CrossRefRestClient extends RestClientBase {

    public CrossRefResponse search(String doi) {
        String url = "http://api.crossref.org/works/" + doi;
        RequestEntity<String> request = request(url, null, "");
        ResponseEntity<CrossRefResponse> response = exchange(request, CrossRefResponse.class);
        return response.getBody();
    }
}
