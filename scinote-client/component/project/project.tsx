import * as React from "react";
import {Paper, PaperId, Project, ProjectInfo, ProjectPaper, SearchResult} from "../../model";
import {Button, message, Skeleton, Spin, Radio} from "antd";
import { LoadingOutlined } from '@ant-design/icons';
import {api} from "../../service/api";
import {PaperCard} from "./paper";

interface ProjectProps {
    id: string;
    searchResult?: SearchResult;
}

interface ProjectState {
    project?: Project;
    papers: Record<string, Paper>;
    tab: "accepted" | "rejected" | "readLater" | "suggestions";
}

export class ProjectView extends React.Component<ProjectProps, ProjectState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {project: null, papers: {}, tab: "accepted"};
    }

    componentDidMount(): void {
        this.fetchProject();
    }

    componentDidUpdate(prevProps: Readonly<ProjectProps>, prevState: Readonly<ProjectState>, snapshot?: any): void {
        const prevSearchResult = prevProps.searchResult;
        const currSearchResult = this.props.searchResult;
        if(currSearchResult != null) {
            if(prevSearchResult == null || prevSearchResult.timestamp !== currSearchResult.timestamp) {
                this.addPaper(currSearchResult.paper);
            }
        }
    }

    private addPaper(paper: Paper): void {
        if(!this.state.project) return;
        const key = this.paperKey(paper.ids[0]);
        const papers = {...this.state.papers}
        papers[key] = paper;
        const project = {...this.state.project};
        const accepted = [...project.accepted];
        accepted.push({id: paper.ids[0], tags: []});
        project.accepted = accepted;
        this.setState({...this.state, papers, project});
    }

    private fetchProject(): void {
        api.projectDetails(this.props.id).then(res => {
            this.setState({...this.state, project: res});
        }).catch(err => message.error(err));
    }

    private loader(): React.ReactElement {
        return (
            <div className="loading">
                <LoadingOutlined style={{ fontSize: 50, margin: "10px" }} spin />
                <strong>Loading project</strong>
            </div>
        );
    }

    private header(): React.ReactElement {
        const project = this.state.project;
        return (
            <div className="project-header">
                <h1 style={{marginBottom: "0px"}}>{project.title}</h1>
                <span style={{color: "#999"}}>ID: {project.id}</span>
            </div>
        )
    }

    private tabs(): React.ReactElement {
        return (
            <div className="project-tabs-panel">
                <Radio.Group onChange={val => this.setState({...this.state, tab: val.target.value})} value={this.state.tab}>
                    <Radio.Button value="accepted">Accepted</Radio.Button>
                    <Radio.Button value="rejected">Rejected</Radio.Button>
                    <Radio.Button value="readLater">Read&nbsp;later</Radio.Button>
                    <Radio.Button value="suggestions">Suggestions</Radio.Button>
                </Radio.Group>
            </div>
        );
    }

    private papersPanel(): React.ReactElement {
        const tab = this.state.tab;
        if(tab === "suggestions") {
            return null;
        }
        const tabPapers: ProjectPaper[] = this.state.project[tab];
        const cache = this.state.papers;
        const cards = tabPapers.map(val => <PaperCard projectPaper={val} paper={cache[this.paperKey(val.id)]} />);
        return (
            <div className="project-papers-panel">
                {cards}
            </div>
        )
    }

    private paperKey(paperId: PaperId): string {
        return `${paperId.repo},${paperId.id}`;
    }

    render(): React.ReactElement {
        const project = this.state.project;
        if(project == null) return this.loader();
        return (
            <div className="project-content">
                {this.header()}
                {this.tabs()}
                {this.papersPanel()}
            </div>
        )
    }
}
