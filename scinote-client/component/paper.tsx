import * as React from "react";
import {Paper} from "../model";

interface PaperCardProps {
    paper: Paper;
}

export class PaperCard extends React.Component<PaperCardProps, any> {

    private authors(): React.ReactElement {
        const authors = this.props.paper.authors;
        const line = authors.map(val => `${val.firstName} ${val.lastName}`).join(", ");
        return <div className="paper-authors">{line}</div>
    }

    private title(): React.ReactElement {
        const urls = this.props.paper.urls;
        if(urls && urls.length) {
            const url = urls[0];
            return <a className="paper-title" href={url.url}>{this.props.paper.title}</a>
        } else {
            return <span className="paper-title">{this.props.paper.title}</span>
        }
    }

    private source(): React.ReactElement {
        const source = this.props.paper.source;
        const line = [source.name, source.publisher, this.props.paper.year].filter(val => val).join(", ");
        return <span>{line}</span>
    }

    private links(): React.ReactElement {
        const urls = this.props.paper.urls;
        if(!urls || urls.length == 0) return null;
        const elements = urls.map((val, idx) => <a key={val.url} href={val.url}>[{idx}]&nbsp;</a>);
        return <span className="paper-links">links: {elements}</span>
    }

    render(): React.ReactElement {
        return (
            <div className="paper-card">
                {this.title()}
                {this.links()}
                {this.authors()}
                {this.source()}
            </div>
        )
    }
}
