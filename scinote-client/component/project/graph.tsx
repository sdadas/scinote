import * as React from "react";
import { LoadingOutlined } from '@ant-design/icons';
import {Sigma, RandomizeNodePositions, RelativeSize} from 'react-sigma';
import ForceAtlas2 from 'react-sigma/lib/ForceAtlas2'
import {ProjectGraph} from "../../model";

interface ProjectGraphProps {
    graph?: ProjectGraph;
}

export class ProjectGraphView extends React.Component<ProjectGraphProps, any> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
    }

    private loader(): React.ReactElement {
        return (
            <div className="loading">
                <LoadingOutlined style={{ fontSize: 50, margin: "10px" }} spin />
                <strong>Loading graph</strong>
            </div>
        );
    }

    private graph(): React.ReactElement {
        const graph: ProjectGraph = this.props.graph;
        graph.edges.forEach(edge => {edge.color = "rgba(0,0,0,0.02)"; edge.originalColor = edge.color;});
        graph.nodes.forEach(node => {node.color = "#b9b9b9"; node.originalColor = node.color;});
        const settings = {
            drawEdges: true,
            clone: false,
            hideEdgesOnMove: true,
            defaultEdgeType: "curvedArrow",
            minArrowSize: 5,
            minNodeSize: 0,
            maxNodeSize: 30,
            labelThreshold: 25
        };
        const forceAtlas2Settings = {
            worker: true,
            barnesHutOptimize: true,
            gravity: 1,
            strongGravityMode: true,
            barnesHutTheta: 0.6,
            iterationsPerRender: 0,
            linLogMode: true,
            timeout: 1000,
            scalingRatio: 10,
            adjustSizes: true
        }
        return (
            <Sigma renderer="canvas" graph={graph} settings={settings} style={{height: "1000px"}}
                   onClickStage={e => this.onClickStage(e)} onClickNode={e => this.onClickNode(e)}>
                <RandomizeNodePositions>
                    <ForceAtlas2 {...forceAtlas2Settings} />
                    <RelativeSize initialSize={1} />
                </RandomizeNodePositions>
            </Sigma>
        );
    }

    private graphWithHandlers(): React.ReactElement {
        const handlers = {
            onClickStage: e => this.onClickStage(e),
            onClickNode: e => this.onClickStage(e)
        };
        const graph: any = this.graph();
        console.log(graph);
        Sigma.bindHandlers(handlers, graph);
        return graph;
    }

    private onClickStage(event: any) {
        const sigma = event.target;
        sigma.graph.nodes().forEach(n => n.color = n.originalColor);
        sigma.graph.edges().forEach(e => e.color = e.originalColor);
        sigma.refresh();
    }

    private onClickNode(event: any) {
        const sigma = event.target;
        const nodeId = event.data.node.id;
        const neighbors = sigma.graph.adjacentNodes(nodeId);
        const keep = {};
        for(const node of neighbors) {
            keep[node.id] = node;
        }
        keep[nodeId] = event.data.node;

        sigma.graph.nodes().forEach(n => {
            if (keep[n.id]) {
                n.color = n.originalColor;
            } else {
                n.color = "rgba(0,0,0,0)";
            }
        });

        sigma.graph.edges().forEach(e => {
            if (e.source === nodeId) {
                e.color = "#ffadd2";
                keep[e.target].color = "#ffadd2";
            } else if(e.target === nodeId) {
                e.color = "#adc6ff";
                keep[e.source].color = "#adc6ff";
            } else {
                e.color = "rgba(0,0,0,0)";
            }
        });
        event.data.node.color = "#eb2f96";

        sigma.refresh();
    }

    render(): React.ReactElement {
        const graph: ProjectGraph = this.props.graph;
        return (
            <div className="project-graph-panel">
                {graph ? this.graph() : this.loader()}
            </div>
        )
    }
}
