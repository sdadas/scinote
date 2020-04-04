package com.sdadas.scinote.repos.academic.model.api;

import org.springframework.util.MultiValueMap;

/**
 * @author Sławomir Dadas
 */
public interface QueryParamSource {

    MultiValueMap<String, String> queryParams();
}
