import * as React from "react";
import {Paper, Project, ProjectInfo} from "../../model";
import {ProjectCard} from "../projects/card";
import {Button} from "antd";

interface ProjectProps {
    id: string;
}

interface ProjectState {
    project?: Project;
    papers: Record<string, Paper>;
}

export class ProjectView extends React.Component<ProjectProps, ProjectState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {project: null, papers: {}};
    }

    componentDidMount(): void {

    }

    render(): React.ReactElement {
        return (
            <div>
                <h1>{this.props.id}</h1>
            </div>
        )
    }
}
