package com.sdadas.scinote.project.model.graph;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class ProjectGraphEdge implements Serializable {

    private String id;

    private String source;

    private String target;

    public ProjectGraphEdge() {
    }

    public ProjectGraphEdge(String source, String target) {
        this.source = source;
        this.target = target;
        this.id = source + "_" + target;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
