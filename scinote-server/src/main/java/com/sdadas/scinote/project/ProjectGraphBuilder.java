package com.sdadas.scinote.project;

import com.sdadas.scinote.project.model.graph.ProjectGraph;
import com.sdadas.scinote.project.model.graph.ProjectGraphEdge;
import com.sdadas.scinote.project.model.graph.ProjectGraphNode;
import com.sdadas.scinote.repos.academic.model.AcademicPaper;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
public class ProjectGraphBuilder implements Serializable {

    private final List<Paper> papers;

    private Map<Long, ProjectGraphNode> nodesById;

    private List<ProjectGraphNode> nodes;

    private List<ProjectGraphEdge> edges;

    public ProjectGraphBuilder(List<Paper> papers) {
        this.papers = papers;
    }

    public ProjectGraph build() {
        this.nodes = createNodes();
        this.nodesById = createNodesById();
        this.edges = createEdges();
        return new ProjectGraph(nodes, edges);
    }

    private List<ProjectGraphEdge> createEdges() {
        List<ProjectGraphEdge> res = new ArrayList<>();
        for (Paper paper : papers) {
            if(paper instanceof AcademicPaper) {
                res.addAll(createPaperEdges((AcademicPaper) paper));
            }
        }
        return res;
    }

    private List<ProjectGraphEdge> createPaperEdges(AcademicPaper source) {
        List<Long> references = source.getReferences();
        List<ProjectGraphEdge> res = new ArrayList<>();
        for (Long reference : references) {
            ProjectGraphNode dest = nodesById.get(reference);
            if(dest == null) continue;
            ProjectGraphEdge edge = new ProjectGraphEdge(source.getUid(), dest.getId());
            res.add(edge);
        }
        return res;
    }

    private List<ProjectGraphNode> createNodes() {
        return this.papers.stream().map(this::createNode).collect(Collectors.toList());
    }

    private Map<Long, ProjectGraphNode> createNodesById() {
        Map<Long, ProjectGraphNode> res = new HashMap<>();
        for (ProjectGraphNode node : nodes) {
            if(node.getNodeId() != 0) {
                res.put(node.getNodeId(), node);
            }
        }
        return res;
    }

    private ProjectGraphNode createNode(Paper paper) {
        ProjectGraphNode res = new ProjectGraphNode();
        res.setId(paper.getUid());
        PaperId paperId = paper.getIdOfType("academic");
        if(paperId != null) {
            long nodeId = Long.parseLong(paperId.getId());
            res.setNodeId(nodeId);
        } else {
            res.setNodeId(0);
        }
        res.setLabel(StringUtils.defaultIfBlank(paper.getTitle(), "(Untitled)"));
        int citations = paper.getCitations() != null ? paper.getCitations() : 0;
        res.setSize((int) Math.round(Math.max(Math.log(citations), 1)));
        String url = paper.getUrls().isEmpty() ? null : paper.getUrls().get(0).getUrl();
        res.setUrl(url);
        return res;
    }
}
