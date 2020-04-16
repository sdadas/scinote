import * as React from "react";
import {Paper, ProjectPaper} from "../../model";
import {Skeleton} from "antd";

interface PaperProps {
    projectPaper: ProjectPaper;
    paper?: Paper;
}

export class PaperCard extends React.Component<PaperProps, any> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
    }

    render(): React.ReactElement {
        const paper = this.props.paper;
        if(!paper) return <Skeleton active />
        return (
            <div><h1>{paper.title}</h1></div>
        )
    }
}
