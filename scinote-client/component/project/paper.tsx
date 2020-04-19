import * as React from "react";
import {EditPaperRequest, Paper, ProjectPaper} from "../../model";
import {Skeleton, Tag} from "antd";
import {Inplace} from "../utils/inplace";
import {FileOutlined, TagsOutlined} from '@ant-design/icons';


interface PaperCardProps {
    projectPaper: ProjectPaper;
    paper?: Paper;
    editEvent: Function;

}

interface PaperCardState {
    notes: string;
    tags: string[];
}

export class PaperCard extends React.Component<PaperCardProps, PaperCardState> {

    constructor(props: Readonly<any>) {
        super(props);
        const pp = this.props.projectPaper;
        this.state = {notes: pp.notes, tags: pp.tags};
    }

    private editNotes(value: string) {
        const request: EditPaperRequest = {paperId: this.props.projectPaper.id, notes: value, tags: this.state.tags};
        this.props.editEvent(request);
        this.setState({...this.state, notes: value});
    }

    private editTags(value: string) {
        if(value.trim().length === 0) {
            this.setState({...this.state, tags: []});
            const request: EditPaperRequest = {paperId: this.props.projectPaper.id, notes: this.state.notes, tags: []};
            this.props.editEvent(request);
        } else {
            const tags: string[] = value.split(",").filter(val => val.trim().length > 0);
            const request: EditPaperRequest = {paperId: this.props.projectPaper.id, notes: this.state.notes, tags: tags};
            this.props.editEvent(request);
            this.setState({...this.state, tags: tags});
        }
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

    private notes(): React.ReactElement {
        const notes = this.state.notes || " ";
        const rows = notes.split("\n").length;
        let display;
        let className;
        if(notes.trim().length > 0) {
            display = <div className="paper-notes">{notes}</div>;
            className = "paper-edits paper-block-edits";
        } else {
            display = <span className="paper-edits-icon"><FileOutlined /> Notes</span>;
            className = "paper-edits paper-inline-edits";
        }
        return (
            <div className={className} key="notes">
                <Inplace type="textarea" value={notes} onSave={val => this.editNotes(val)} displayComponent={display} attributes={{rows: rows}} />
            </div>
        );
    }

    private tags(): React.ReactElement {
        const tags = this.state.tags || [];
        let display;
        let className;
        if(tags.length > 0) {
            const elements = tags.map(val => <Tag key={val} color="geekblue">{val}</Tag>);
            display = <div className="paper-tags">{elements}</div>;
            className = "paper-edits paper-inline-edits";
        } else {
            display = <span className="paper-edits-icon"><TagsOutlined /> Tags</span>;
            className = "paper-edits paper-inline-edits";
        }
        const tagsValue = tags.length > 0 ? tags.join(",") : " ";
        return (
            <div className={className} key="tags">
                <Inplace type="textarea" value={tagsValue} onSave={val => this.editTags(val)} displayComponent={display} />
            </div>
        )
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
                {this.tags()}
                {this.notes()}
            </div>
        )
    }
}