import * as React from "react";
import {EditPaperRequest, Paper, PaperActionRequest, ProjectPaper, WebLocation} from "../../model";
import {Button, message, Popover, Skeleton, Tag, Upload} from "antd";
import {Inplace} from "../utils/inplace";
import {FileOutlined, TagsOutlined, LinkOutlined, DownCircleOutlined, CheckCircleOutlined, CloseCircleOutlined, QuestionCircleOutlined, FormOutlined, GlobalOutlined} from '@ant-design/icons';
import {api} from "../../service/api";
import {TagsInput} from "./tags";
import {UploadProps} from "antd/lib/upload/Upload";

interface PaperCardProps {
    projectPaper: ProjectPaper;
    projectId: string;
    paper?: Paper;
    editEvent: Function;
    actionEvent: Function;
    readonly?: boolean;
}

interface PaperCardState {
    notes: string;
    tags: string[];
    files?: WebLocation[];
}

export class PaperCard extends React.Component<PaperCardProps, PaperCardState> {

    constructor(props: Readonly<any>) {
        super(props);
        const pp = this.props.projectPaper;
        this.state = {notes: pp.notes, tags: pp.tags, files: pp.files};
    }

    private editNotes(value: string) {
        if(value === this.state.notes) return;
        const request: EditPaperRequest = {paperId: this.props.projectPaper.id, notes: value, tags: this.state.tags};
        this.props.editEvent(request);
        this.setState({...this.state, notes: value});
    }

    private editTags(value: string | string[]) {
        const tags = Array.isArray(value) ? value : value.split(",").map(v => v.trim()).filter(v => v.length > 0);
        const prev = this.state.tags;
        if(prev && tags) {
            if(prev.length === tags.length && prev.every(function(value, index) { return value === tags[index]})) {
                return;
            }
        }
        if(tags.length === 0) {
            this.setState({...this.state, tags: []});
            const request: EditPaperRequest = {paperId: this.props.projectPaper.id, notes: this.state.notes, tags: []};
            this.props.editEvent(request);
        } else {
            const request: EditPaperRequest = {paperId: this.props.projectPaper.id, notes: this.state.notes, tags: tags};
            this.props.editEvent(request);
            this.setState({...this.state, tags: tags});
        }
    }

    private paperAction(action: any) {
        const request: PaperActionRequest = {paperId: this.props.projectPaper.id, action: action, projectId: null};
        this.props.actionEvent(request);
    }

    private title(): React.ReactElement {
        const urls = this.props.paper.urls;
        const title = this.props.paper.title || "(Untitled)";
        const year = this.props.paper.year;
        const yearBadge = year ? <Tag className="paper-year">{year}</Tag> : null;
        if(urls && urls.length) {
            const url = urls[0];
            return <span>{yearBadge}<a className="paper-title" href={this.url(url.url)} target="_blank">{title}</a></span>
        } else {
            return <span className="paper-title">{yearBadge}{title}</span>
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
            display = <span className="paper-edits-icon"><FileOutlined />&nbsp;Notes</span>;
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
            const elements = tags.map((val, idx) => <Tag key={val + idx.toString()} color="geekblue">{val}</Tag>);
            display = <div className="paper-tags">{elements}</div>;
            className = "paper-edits paper-inline-edits";
        } else {
            display = <span className="paper-edits-icon"><TagsOutlined />&nbsp;Tags</span>;
            className = "paper-edits paper-inline-edits";
        }
        const edit = <TagsInput />;
        return (
            <div className={className} key="tags">
                <Inplace value={tags} onSave={val => this.editTags(val)} disableAutoSubmit={true}
                         displayComponent={display} editComponent={edit} />
            </div>
        )
    }

    private actions(): React.ReactElement {
        const urls: WebLocation[] = this.props.paper.urls;
        let links = null;
        if(urls && urls.length > 0) {
            links = <Popover placement="leftTop" title="Paper links" content={() => this.links()} trigger="click"><LinkOutlined /></Popover>;
        }
        return (
            <div className="paper-actions">
                {links}
                &nbsp;
                <Popover placement="leftTop" title="Actions" content={() => this.actionMenu()} trigger="click">
                    <DownCircleOutlined />
                </Popover>
            </div>
        );
    }

    private actionMenu(): React.ReactElement {
        const bibtexUrl = api.paperBibTeX(this.props.projectPaper.id);
        return (
            <div className="paper-actions-menu">
                <div className="paper-actions-menu-tabs">
                    <CheckCircleOutlined onClick={() => this.paperAction("ACCEPT")} className="paper-actions-menu-icon" title="Accept" />
                    <CloseCircleOutlined onClick={() => this.paperAction("REJECT")}  className="paper-actions-menu-icon" title="Reject" />
                    <QuestionCircleOutlined onClick={() => this.paperAction("READ_LATER")}  className="paper-actions-menu-icon" title="Read later" />
                </div>
                <a href={bibtexUrl} target="_blank">Export to BibTeX</a>
                {this.uploadAction()}
            </div>
        )
    }

    private uploadAction(): React.ReactElement {
        const props: UploadProps = {
            name: "file",
            action: api.paperUploadAttachment(this.props.projectId, this.props.projectPaper.id),
            showUploadList: false,
            onChange: (info) => {
                if (info.file.status === 'done') {
                    const response: any = info.file.response;
                    if(response.error) {
                        message.error(response.error);
                    } else {
                        message.success(`${info.file.name} file uploaded successfully`);
                        this.setState({...this.state, files: response.result.files});
                    }
                } else if (info.file.status === 'error') {
                    message.error(`${info.file.name} file upload failed.`);
                }
            }
        };
        return (
            <Upload {...props}>
                <Button type="link">Upload attachment</Button>
            </Upload>
        )
    }

    private links(): React.ReactElement {
        const urls: WebLocation[] = this.props.paper.urls;
        const paperLinks = urls.map(val => this.link(val.url, val.url, <GlobalOutlined/>));
        let attachedLinks = [];
        if(this.state.files) {
            attachedLinks = this.state.files.map(val => this.link(val.url, val.name, <LinkOutlined/>));
        }
        return <div className="paper-links">{paperLinks}{attachedLinks}</div>;
    }

    private link(url: string, name: string, icon: React.ReactElement): React.ReactElement {
        return (
            <div key={url}>
                <a href={this.url(url)} title={url} target="_blank">
                    {icon}&nbsp;&nbsp;{this.abbrUrl(name, 40)}
                </a>
            </div>
        );
    }

    private url(url: string): string {
        const lower = url.toLowerCase();
        if(lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("www")) {
            return url;
        } else {
            return api.fileUrl(url);
        }
    }

    private abbrUrl(url: string, chars: number): string {
        if(url.startsWith("http://")) {
            url = url.substring(7);
        } else if(url.startsWith("https://")) {
            url = url.substring(8);
        }
        if(url.length > chars) {
            const half = (chars / 2) - 3;
            return url.substr(0, half) + "[...]" + url.substr(url.length - half);
        } else {
            return url;
        }
    }

    private citations(): React.ReactElement {
        const citations = this.props.paper.citations;
        const title = "Cited by " + citations;
        return citations ? <span className="paper-edits-icon" title={title}><FormOutlined/>&nbsp;{citations}</span> : null;
    }

    render(): React.ReactElement {
        const paper = this.props.paper;
        const readonly = this.props.readonly;
        if(!paper) return <div className="paper-card"><Skeleton active paragraph={{rows: 1}} /></div>;
        return (
            <div className="paper-card">
                <div className="paper-card-title">
                    {this.title()}
                    {this.actions()}
                </div>
                {this.authors()}
                {this.source()}
                <div>
                    {this.citations()}
                    {readonly ? null : this.tags()}
                    {readonly ? null : this.notes()}
                </div>
            </div>
        )
    }
}
