import * as React from "react";
import {Button, Skeleton, Radio, message} from 'antd';
import {ProjectActionRequest, ProjectInfo, UIAction} from "../../model";
import {api} from "../../service/api";
import {withRouter} from "react-router-dom";
import {AppUtils} from "../../utils";

interface ProjectsViewProps {
    action?: UIAction;
    history: any;
}

interface ProjectsViewState {
    projects?: ProjectInfo[];
    currentProject?: string;
}

class ProjectsView extends React.Component<ProjectsViewProps, ProjectsViewState> {

    constructor(props: Readonly<ProjectsViewProps>) {
        super(props);
        this.state = {projects: null, currentProject: this.currentProjectFromPath(props)};
    }

    private currentProjectFromPath(props: any): string {
        const path = props.location.pathname;
        const regex = /\/project\/(\w+)/i;
        const match = path.match(regex);
        if(match) {
            return match[1];
        }
        return null;
    }

    componentDidMount(): void {
        this.fetchProjects();
    }

    componentDidUpdate(prevProps: Readonly<ProjectsViewProps>, prevState: Readonly<ProjectsViewState>, snapshot?: any): void {
        const prevAction = prevProps.action;
        const currAction = this.props.action
        if(currAction != null && currAction.type == "PROJECT_CHANGED") {
            if(prevAction == null || prevAction.timestamp !== currAction.timestamp) {
                this.fetchProjects();
            }
        }
    }

    private fetchProjects(): void {
        api.projectList()
            .then(res => this.setState({...this.state, projects: res}))
            .catch(err => message.error(err));
    }

    private createProject(): void {
        const projectId: string = Math.random().toString(19).slice(2);
        const request: ProjectActionRequest = {projectId: projectId, action: "CREATE"};
        api.projectAction(request).then(res => {
            if(res.errors.length === 0) {
                this.fetchProjects();
            } else {
                message.error(res.errors.join("\n"));
            }
        }).catch(err => message.error(err));
    }

    private createProjectButton(): React.ReactElement {
        const style = {marginTop: "1em", width: "100%"}
        return (
            <Button type="primary" ghost shape="round" size="large" style={style} onClick={() => this.createProject()}>
                Create new project
            </Button>
        )
    }

    private projectList(projects: ProjectInfo[]): React.ReactElement {
        const options = projects.map(val => (
            <Radio.Button value={val.id} className="project-button" key={val.id}>
                <strong style={{fontSize: "larger"}}>{AppUtils.abbr(val.title, 20)}</strong><br/>
                <span style={{fontSize: "smaller"}}>Updated: {AppUtils.formatTimestamp(val.updated)}</span>
            </Radio.Button>
        ));
        return (
            <Radio.Group className="projects-list" buttonStyle="solid" onChange={e => this.openProject(e)}
                         value={this.state.currentProject}>
                {options}
            </Radio.Group>
        );
    }

    private openProject(e) {
        this.props.history.push(`/project/${e.target.value}`);
        this.setState({...this.state, currentProject: e.target.value});
    }

    render(): React.ReactElement {
        const projects = this.state.projects;
        if(projects == null) {
            return <Skeleton active />
        }
        return <div>{this.projectList(projects)}{this.createProjectButton()}</div>;
    }
}

export default withRouter(ProjectsView as any);
