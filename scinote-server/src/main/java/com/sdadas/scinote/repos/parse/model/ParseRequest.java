package com.sdadas.scinote.repos.parse.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class ParseRequest implements Serializable {

    private String filename;

    private Resource resource;

    public ParseRequest(String filename, Resource resource) {
        this.filename = filename;
        this.resource = resource;
    }

    public ParseRequest() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ParseResponse createResponse() {
        ParseResponse response = new ParseResponse();
        String filename = this.getFilename();
        if(!StringUtils.endsWithIgnoreCase(filename, ".pdf")) {
            response.setError("Only PDF files are supported");
        }
        return response;
    }
}
