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

    private title(): React.ReactElement {
        const urls = this.props.paper.urls;
        if(urls && urls.length) {
            const url = urls[0];
            return <a className="paper-title" href={url.url} target="_blank">{this.props.paper.title}</a>
        } else {
            return <span className="paper-title">{this.props.paper.title}</span>
        }
    }

    private authors(): React.ReactElement {
        const authors = this.props.paper.authors;
        const line = authors.map(val => `${val.firstName} ${val.lastName}`).join(", ");
        return <div className="paper-authors">{line}</div>
    }

    private source(): React.ReactElement {
        const source = this.props.paper.source;
        const line = [source.name, source.publisher, this.props.paper.year].filter(val => val).join(", ");
        return <span>{line}</span>
    }

    render(): React.ReactElement {
        const paper = this.props.paper;
        if(!paper) return <Skeleton active />
        return (
            <div className="paper-card">
                <div className="paper-card-title">
                    {this.title()}
                </div>
                {this.authors()}
                {this.source()}
            </div>
        )
    }
}
