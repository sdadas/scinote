import * as React from "react";
import {
    EditPaperRequest,
    EditProjectRequest,
    Paper,
    PaperActionRequest,
    PaperDetails,
    PaperId,
    Project,
    ProjectActionRequest, ProjectGraph,
    ProjectPaper,
    UIAction
} from "../../model";
import {Button, message, Radio, Popconfirm, Skeleton} from "antd";
import { LoadingOutlined } from '@ant-design/icons';
import {api} from "../../service/api";
import {PaperCard} from "./paper";
import {Redirect} from "react-router-dom";
import {Inplace} from "../utils/inplace";
import {FilterMatcher, FiltersComponent, FiltersObject} from "./filters";
import {AppUtils} from "../../utils";
import {ProjectGraphView} from "./graph";

interface ProjectProps {
    id: string;
    action?: UIAction;
    actionEvent: Function;
}

interface ProjectState {
    project?: Project;
    suggestionsLoading: boolean;
    suggestions: Paper[];
    graph?: ProjectGraph;
    papers: Record<string, Paper>;
    tab: "accepted" | "rejected" | "readLater" | "suggestions" | "graph";
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
        return {
            project: null,
            papers: {},
            tab: "accepted",
            refreshed: new Date().getTime(),
            filters: {},
            suggestionsLoading: false,
            suggestions: []
        };
    }

    componentDidMount(): void {
        this.fetchProject();
    }

    componentDidUpdate(prevProps: Readonly<ProjectProps>, prevState: Readonly<ProjectState>, snapshot?: any): void {
        const prevAction = prevProps.action;
        const currAction = this.props.action
        if(currAction != null && currAction.type == "SEARCH") {
            if(prevAction == null || prevAction.timestamp !== currAction.timestamp) {
                const paper: Paper = currAction.payload;
                const request: PaperActionRequest = {paperId: paper.ids[0], action: "ACCEPT", projectId: this.props.id};
                this.movePaper(request, null, "accepted", paper, {});
            }
        }
        if(prevProps.id !== this.props.id) {
            this.fetchProject();
        }
    }

    private cachePaper(paper: Paper): Record<string, Paper> {
        const papers = {...this.state.papers}
        this.createCachedDataForPaper(paper);
        for(let id of paper.ids) {
            const key = this.paperKey(id);
            papers[key] = paper;
        }
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

    private paperAction(request: PaperActionRequest, paper: Paper) {
        request.projectId = this.props.id;
        let from = this.state.tab;
        let to;
        switch (request.action) {
            case "ACCEPT": to = "accepted"; break;
            case "REJECT": to = "rejected"; break;
            case "READ_LATER": to = "readLater"; break;
        }
        if(from === to) {
            return;
        } else if(from == "suggestions") {
            const paperKey = this.paperKey(request.paperId);
            const papers = (this.state.suggestions || []).filter(val => {
                if(this.paperKey(val.ids[0]) === paperKey) {
                    paper = val;
                    return false;
                } else {
                    return true;
                }
            });
            this.movePaper(request, null, to, paper, {suggestions: papers});
        } else {
            this.movePaper(request, from, to, paper, {});
        }
    }

    private movePaper(request: PaperActionRequest, source: string, dest: string, paper: Paper, newState: any) {
        if(!source && this.projectContains(request.paperId)) return;
        const project = {...this.state.project};
        const papers = paper ? this.cachePaper(paper) : this.state.papers;
        let projectPaper;
        if(source) {
            let sourcePapers = [...project[source]];
            const [resultPaper, sourcePapersFiltered] = this.takePaperIfExists(sourcePapers, request.paperId);
            sourcePapers = sourcePapersFiltered;
            project[source] = sourcePapers;
            projectPaper = resultPaper;
        } else {
            projectPaper = {id: request.paperId, tags: [], added: new Date().getTime()};
        }
        let destPapers = [...project[dest]];
        destPapers.push(projectPaper);
        project[dest] = destPapers;
        this.setState({...this.state, ...newState, papers: papers, project: project});
        api.paperAction(request).then(res => {
            if(res.errors.length > 0) {
                message.error(res.errors.join("\n"));
            } else {
                const papers = this.cachePaper(res.result);
                this.setState({...this.state, papers: papers});
            }
        }).catch(err => message.error(err.toString()));
    }

    private projectContains(paperId: PaperId) {
        const paperKey = this.paperKey(paperId);
        const proj = this.state.project;
        for(const tab of [proj.accepted, proj.readLater, proj.rejected]) {
            for(const paper of tab) {
                const otherKey = this.paperKey(paper.id);
                if(paperKey === otherKey) {
                    return true;
                }
            }
        }
        return false;
    }

    private takePaperIfExists(papers: ProjectPaper[], paperId: PaperId): [ProjectPaper, ProjectPaper[]] {
        const paperKey: string = this.paperKey(paperId);
        let found: ProjectPaper = null;
        for(const paper of papers) {
            if(this.paperKey(paper.id) === paperKey) {
                found = paper;
                break;
            }
        }
        if(found) {
            const filteredPapers = papers.filter(val => this.paperKey(val.id) !== paperKey);
            return [found, filteredPapers];
        } else {
            const paper: ProjectPaper = {id: paperId, tags: [], added: new Date().getTime()};
            return [paper, papers];
        }
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
            res.forEach(val => val.ids.forEach(id => values[this.paperKey(id)] = this.createCachedDataForPaper(val)));
            this.setState({...this.state, papers: {...this.state.papers, ...values}});
        }).catch(err => message.error(err.toString()));
    }

    private fetchSuggestions(): void {
        this.setState({...this.state, suggestionsLoading: true});
        api.projectSuggestions(this.props.id, 10).then(res => {
            this.setState({...this.state, suggestions: res, suggestionsLoading: false});
        }).catch(err => {
            message.error(err.toString());
            this.setState({...this.state, suggestions: [], suggestionsLoading: false});
        });
    }

    private onFiltersChange(filters: FiltersObject) {
        this.setState({...this.state, filters: filters});
    }

    private onShowProjectGraph(e: MouseEvent) {
        e.preventDefault();
        this.setState({...this.state, tab: "graph", graph: null});
        api.projectGraph(this.props.id).then(res => {
            this.setState({...this.state, graph: res});
        }).catch(err => {
            message.error(err.toString());
            this.setState({...this.state, graph: null});
        });
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
        const bibtexUrl = api.projectBibTeX(this.props.id);
        return (
            <div className="project-header">
                <h1 style={{marginBottom: "0px"}}>
                    <Inplace type="text" value={project.title} onSave={val => this.editProject(val)} onValidate={val => val.trim().length > 0} />
                </h1>
                <span style={{color: "#999"}}>ID: {project.id}</span>&nbsp;
                <a href={bibtexUrl} target="_blank">[BibTeX]</a>&nbsp;
                <a href="#" onClick={(e: any) => this.onShowProjectGraph(e)}>[Citation graph]</a>
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
                    <Radio.Button value="suggestions" onClick={() => this.fetchSuggestions()}>Suggestions</Radio.Button>
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
            return this.suggestionsPanel();
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
                return <PaperCard projectPaper={val.projectPaper} paper={val.paper} key={cardKey}
                                  projectId={this.props.id} editEvent={req => this.editPaper(req)}
                                  actionEvent={req => this.paperAction(req, null)} />
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

    private suggestionsPanel(): React.ReactElement {
        if(this.state.suggestionsLoading) {
            return <div className="project-papers-panel"><Skeleton active /></div>;
        }
        const papers = this.state.suggestions;
        const cards = papers.map(val => {
            const paperId = val.ids[0];
            const projectPaper: ProjectPaper = {added: new Date().getTime(), tags: [], notes: null, id: paperId};
            const cardKey = this.paperKey(paperId) + this.state.refreshed.toString();
            return <PaperCard paper={val} projectPaper={projectPaper} editEvent={() => {}} key={cardKey} readonly
                              projectId={this.props.id} actionEvent={req => this.paperAction(req, null)}/>
        })
        return (
            <div className="project-papers-panel">
                {cards.length > 0 ? cards : <div className="project-no-suggestions">No suggestions at the moment</div>}
            </div>
        )
    }

    private graphPanel(): React.ReactElement {
        return <ProjectGraphView graph={this.state.graph} />
    }

    private content(): React.ReactElement {
        const tab = this.state.tab;
        if(tab === "graph") {
            return this.graphPanel();
        } else {
            return this.papersPanel();
        }
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
                {this.content()}
            </div>
        )
    }
}
