package com.sdadas.scinote.repos.arxiv.rest;

import com.sdadas.scinote.repos.arxiv.model.ArxivResponse;
import com.sdadas.scinote.repos.shared.utils.RestClientBase;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Sławomir Dadas
 */
public class ArxivRestClient extends RestClientBase {

    public ArxivResponse search(String query) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id_list", query);
        String url = "https://export.arxiv.org/api/query";
        RequestEntity<String> request = request(url, params, "");
        ResponseEntity<ArxivResponse> response = exchange(request, ArxivResponse.class);
        return response.getBody();
    }
}
