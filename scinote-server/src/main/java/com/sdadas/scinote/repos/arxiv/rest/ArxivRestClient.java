package com.sdadas.scinote.repos.arxiv.rest;

import com.sdadas.scinote.repos.academic.model.api.QueryParamSource;
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
@Component
public class ArxivRestClient extends RestClientBase {

    public ArxivResponse search(String query) {
        String url = "https://export.arxiv.org/api/query";
        RequestEntity<String> request = request(url, new ArxivId(query), "");
        ResponseEntity<ArxivResponse> response = exchange(request, ArxivResponse.class);
        return response.getBody();
    }

    private static class ArxivId implements QueryParamSource {

        private final String id;

        private ArxivId(String id) {
            this.id = id;
        }

        @Override
        public MultiValueMap<String, String> queryParams() {
            MultiValueMap<String, String> res = new LinkedMultiValueMap<>();
            res.add("id_list", id);
            return res;
        }
    }
}
