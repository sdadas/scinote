import * as React from "react";
import {Button} from 'antd';
import {ProjectInfo} from "../../model";
import {ProjectCard} from "./card";

export class ProjectsView extends React.Component<any, any> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
    }

    render(): React.ReactElement {
        const projects: ProjectInfo[] = [
            {id: "first", title: "First project", updated: "2020-02-01T13:52"},
            {id: "second", title: "Second project", updated: "2020-02-01T13:52"}
        ];
        const cards = projects.map(val => <ProjectCard project={val} />);
        return (
            <div>
                {cards}
                <Button type="primary" ghost shape="round" size="large" style={{marginTop: "1em", width: "100%"}}>
                    Create new project
                </Button>
            </div>
        )
    }
}
