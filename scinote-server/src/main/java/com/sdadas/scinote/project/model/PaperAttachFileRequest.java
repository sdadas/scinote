package com.sdadas.scinote.project.model;

import org.springframework.core.io.Resource;

public class PaperAttachFileRequest extends PaperActionRequest {

    private String filename;

    private Resource resource;

    public PaperAttachFileRequest() {
        this.setAction(PaperAction.ATTACH);
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
}
