import * as React from "react";
import {
    EditPaperRequest,
    EditProjectRequest,
    Paper,
    PaperActionRequest, PaperDetails,
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
import {FilterMatcher, FiltersComponent, FiltersObject} from "./filters";
import {AppUtils} from "../../utils";
import {match} from "assert";

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
    refreshed: number;
    filters: FiltersObject;
}

export class ProjectView extends React.Component<ProjectProps, ProjectState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = this.initialState();
    }

    private initialState(): ProjectState {
        return {project: null, papers: {}, tab: "accepted", refreshed: new Date().getTime(), filters: {}};
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

    private cachePaper(paper: Paper): Record<string, Paper> {
        const key = this.paperKey(paper.ids[0]);
        const papers = {...this.state.papers}
        this.createCachedDataForPaper(paper);
        papers[key] = paper;
        return papers;
    }

    private createCachedDataForPaper(paper: Paper): Paper {
        const title = paper.title || "";
        const authors = paper.authors.map(val => `${val.firstName} ${val.lastName}`);
        const year = paper.year ? paper.year.toString() : "";
        const journal = paper.source.name || "";
        const publisher = paper.source.publisher || "";
        const text = AppUtils.removeNonAlphanumeric([title, authors, year, journal, publisher].join(" ").toLowerCase());
        const words = text.split(" ").filter(val => val.trim().length > 0)
        const cachedText = new Set<string>();
        for(const word of words) {
            cachedText.add(word);
        }
        paper.cachedText = cachedText;
        return paper;
    }

    private addPaper(paper: Paper): void {
        if(!this.state.project) return;
        const project = {...this.state.project};
        const papers = this.cachePaper(paper);
        const accepted = [...project.accepted];
        accepted.push({id: paper.ids[0], tags: [], added: new Date().getTime()});
        project.accepted = accepted;
        this.setState({...this.state, papers, project});
        const request: PaperActionRequest = {projectId: project.id, paperId: paper.ids[0], action: "ACCEPT"};
        api.paperAction(request).then(val => {}).catch(err => message.error(err.toString()));
    }

    private fetchProject(): void {
        api.projectDetails(this.props.id).then(res => {
            this.setState({...this.initialState(), project: res});
            this.fetchPaperDetails(res);
        }).catch(err => message.error(err.toString()));
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
        }).catch(err => message.error(err.toString()));
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
        }).catch(err => message.error(err.toString()));
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
        }).catch(err => message.error(err.toString()));
    }

    private fetchPaperDetails(project: Project): void {
        const ids: string[] = [];
        for(const list of [project.accepted, project.rejected, project.readLater]) {
            list.forEach(val => ids.push(this.paperKey(val.id)));
        }
        if(ids.length == 0) return;
        api.papers(ids).then(res => {
            const values = {};
            res.forEach(val => values[this.paperKey(val.ids[0])] = this.createCachedDataForPaper(val));
            this.setState({...this.state, papers: {...this.state.papers, ...values}});
        }).catch(err => message.error(err.toString()));
    }

    private onFiltersChange(filters: FiltersObject) {
        this.setState({...this.state, filters: filters});
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
        const project = this.state.project;
        const count = tab => project[tab] ? project[tab].length : 0;
        return (
            <div className="project-tabs-panel">
                <Radio.Group onChange={val => this.setState({...this.state, tab: val.target.value})} value={this.state.tab}>
                    <Radio.Button value="accepted">Accepted [{count("accepted")}]</Radio.Button>
                    <Radio.Button value="rejected">Rejected [{count("rejected")}]</Radio.Button>
                    <Radio.Button value="readLater">Read&nbsp;later [{count("readLater")}]</Radio.Button>
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
        const cache = this.state.papers;
        const matcher = new FilterMatcher(this.state.filters);
        const tabPapers: PaperDetails[] = [...this.state.project[tab]].map(pp => {
            const key = this.paperKey(pp.id);
            const paper = cache[key];
            return {key: key, projectPaper: pp, paper: paper};
        }).sort(matcher.sortFunction());
        const cards = tabPapers.map(val => {
            const cardKey = val.key + this.state.refreshed.toString();
            if(matcher.matches(val.paper, val.projectPaper)) {
                return <PaperCard projectPaper={val.projectPaper} paper={val.paper} key={cardKey} editEvent={req => this.editPaper(req)} />
            } else {
                return null;
            }
        });
        const tags = this.getAllTags();
        return (
            <div className="project-papers-panel">
                <FiltersComponent key={this.props.id} onChange={val => this.onFiltersChange(val)} tags={tags} />
                {cards}
            </div>
        )
    }

    private getAllTags(): string[] {
        const tags = new Set<string>();
        const proj = this.state.project;
        for(const tab of [proj.accepted, proj.readLater, proj.rejected]) {
            for(const paper of tab) {
                if(paper.tags && paper.tags.length) {
                    paper.tags.forEach(val => tags.add(val));
                }
            }
        }
        return Array.from(tags).sort();
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
