import * as React from "react";
import {Button, Skeleton, Radio, message} from 'antd';
import {ProjectActionRequest, ProjectInfo} from "../../model";
import {api} from "../../service/api";
import {withRouter} from "react-router-dom";
import {AppUtils} from "../../utils";

interface ProjectsViewState {
    projects?: ProjectInfo[];
}

class ProjectsView extends React.Component<any, ProjectsViewState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {projects: null};
    }

    componentDidMount(): void {
        this.fetchProjects();
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
                <strong style={{fontSize: "larger"}}>{val.title}</strong><br/>
                <span style={{fontSize: "smaller"}}>Updated: {AppUtils.formatTimestamp(val.updated)}</span>
            </Radio.Button>
        ));
        return <Radio.Group style={{width: "100%"}} buttonStyle="solid" onChange={e => this.openProject(e)}>{options}</Radio.Group>;
    }

    private openProject(e) {
        this.props.history.push(`/project/${e.target.value}`);
    }

    render(): React.ReactElement {
        const projects = this.state.projects;
        if(projects == null) {
            return <Skeleton active />
        }
        return <div>{this.projectList(projects)}{this.createProjectButton()}</div>;
    }
}

export default withRouter(ProjectsView);
