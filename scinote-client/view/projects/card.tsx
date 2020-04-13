import * as React from "react";
import {Card} from 'antd';
import {ProjectInfo} from "../../model";

interface ProjectCardProps {
    project: ProjectInfo;
}

export class ProjectCard extends React.Component<ProjectCardProps, any> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
    }

    render(): React.ReactElement {
        const project = this.props.project;
        return (
            <Card size="small" title={project.title} extra={<span>{project.updated}</span>}>
                {project.id}
            </Card>
        )
    }
}
