package com.sdadas.scinote.project.model.graph;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class ProjectGraphNode implements Serializable {

    private String id;

    private long nodeId;

    private String label;

    private int size = 1;

    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
