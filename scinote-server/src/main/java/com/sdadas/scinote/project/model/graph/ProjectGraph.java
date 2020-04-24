package com.sdadas.scinote.project.model.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class ProjectGraph {

    private List<ProjectGraphNode> nodes = new ArrayList<>();

    private List<ProjectGraphEdge> edges = new ArrayList<>();

    public static ProjectGraph empty() {
        return new ProjectGraph();
    }

    public ProjectGraph() {
    }

    public ProjectGraph(List<ProjectGraphNode> nodes, List<ProjectGraphEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<ProjectGraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ProjectGraphNode> nodes) {
        this.nodes = nodes;
    }

    public List<ProjectGraphEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<ProjectGraphEdge> edges) {
        this.edges = edges;
    }
}
