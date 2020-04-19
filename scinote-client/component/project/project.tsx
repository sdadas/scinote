import * as React from "react";
import {
    EditPaperRequest,
    EditProjectRequest,
    Paper,
    PaperActionRequest,
    PaperId,
    Project,
    ProjectActionRequest,
    ProjectPaper,
    UIAction
} from "../../model";
import {Button, message, Radio, Popconfirm} from "antd";
import { LoadingOutlined } from '@ant-design/icons';
import {api} from "../../service/api";
import {PaperCard} from "./paper";
import {Redirect} from "react-router-dom";
import {Inplace} from "../utils/inplace";

interface ProjectProps {
    id: string;
    action?: UIAction;
    actionEvent: Function;
}

interface ProjectState {
    project?: Project;
    papers: Record<string, Paper>;
    tab: "accepted" | "rejected" | "readLater" | "suggestions";
    deleted?: boolean;
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
        const prevAction = prevProps.action;
        const currAction = this.props.action
        if(currAction != null && currAction.type == "SEARCH") {
            if(prevAction == null || prevAction.timestamp !== currAction.timestamp) {
                this.addPaper(currAction.payload);
            }
        }
        if(prevProps.id !== this.props.id) {
            this.fetchProject();
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

        const request: PaperActionRequest = {projectId: project.id, paperId: paper.ids[0], action: "ACCEPT"};
        api.paperAction(request).then(val => {}).catch(err => message.error(err));
    }

    private fetchProject(): void {
        api.projectDetails(this.props.id).then(res => {
            this.setState({...this.state, project: res, tab: "accepted", papers: {}});
            this.fetchPaperDetails(res);
        }).catch(err => message.error(err));
    }

    private deleteProject(): void {
        const action: ProjectActionRequest = {projectId: this.props.id, action: "DELETE"};
        api.projectAction(action).then(res => {
            if(res.errors.length > 0) {
                message.error(res.errors.join("\n"));
            } else {
                this.props.actionEvent({type: "PROJECT_CHANGED"} as UIAction);
                this.setState({...this.state, deleted: true});
            }
        }).catch(err => message.error(err));
    }

    private editProject(title: string): void {
        if(title.trim().length === 0 || title == this.state.project.title) return;
        const request: EditProjectRequest = {projectId: this.props.id, title: title};
        api.editProject(request).then(res => {
            if(res.errors.length > 0) {
                message.error(res.errors.join("\n"));
            } else {
                this.props.actionEvent({type: "PROJECT_CHANGED"} as UIAction);
                const project: Project = this.state.project;
                this.setState({...this.state, project: {...project, title: title}});
            }
        }).catch(err => message.error(err));
    }

    private editPaper(request: EditPaperRequest) {
        request.projectId = this.props.id;
        api.editPaper(request).then(res => {
            if(res.errors.length > 0) {
                message.error(res.errors.join("\n"));
            } else {
                const key = this.paperKey(request.paperId);
                const project: Project = this.state.project;
                const tab = this.state.tab;
                const oldPapers: ProjectPaper[] = project[tab];
                const newPapers: ProjectPaper[] = [];
                for(const paper of oldPapers) {
                    if(this.paperKey(paper.id) == key) {
                        paper.notes = request.notes;
                        paper.tags = request.tags;
                    }
                    newPapers.push(paper);
                }
                this.setState({...this.state, project: {...project, tab: newPapers}} as any);
            }
        }).catch(err => message.error(err));
    }

    private fetchPaperDetails(project: Project): void {
        const ids: string[] = [];
        for(const list of [project.accepted, project.rejected, project.readLater]) {
            list.forEach(val => ids.push(this.paperKey(val.id)));
        }
        if(ids.length == 0) return;
        api.papers(ids).then(res => {
            const values = {};
            res.forEach(val => values[this.paperKey(val.ids[0])] = val);
            this.setState({...this.state, papers: {...this.state.papers, ...values}});
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
                <h1 style={{marginBottom: "0px"}}>
                    <Inplace type="text" value={project.title} onSave={val => this.editProject(val)} onValidate={val => val.trim().length > 0} />
                </h1>
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

                <div className="project-tabs-panel-actions" style={{float: "right"}}>
                    <Popconfirm placement="left" title="Do you really want do delete this project?"
                                onConfirm={() => this.deleteProject()} okText="Yes" cancelText="No">
                        <Button>Delete project</Button>
                    </Popconfirm>

                </div>
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
        const cards = tabPapers.map(val => {
            const key = this.paperKey(val.id);
            return <PaperCard projectPaper={val} paper={cache[key]} key={key} editEvent={req => this.editPaper(req)} />
        });
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
        if(this.state.deleted) return <Redirect to="/" />
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
