import * as React from "react";
import {UploadProps} from "antd/lib/upload/Upload";
import {api} from "../../service/api";
import {Button, Input, message, Upload} from "antd";
import {SearchProps} from "antd/lib/input/Search";
const { Search } = Input;

interface SearchPanelProps {
    searchEvent: Function;
}

interface SearchPanelState {
    search: string;
}

export class SearchPanel extends React.Component<SearchPanelProps, SearchPanelState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {search: ""};
    }

    private search(query: string, event: any): void {
        event.preventDefault();
        if(!query.trim()) return;
        api.search(query).then(val => {
            if(val.length > 0) {
                this.props.searchEvent({paper: val[0], timestamp: new Date().getTime()});
            }
            this.setState({search: ""});
        }).catch(err => console.log(err));
    }

    private searchInput(): React.ReactElement {
        const ph = "Enter article title, doi, url etc.";
        const props: SearchProps = {
            value: this.state.search,
            placeholder: ph,
            size: "large",
            onChange: val => this.setState({...this.state, search: val.target.value}),
            onSearch: (val, event) => this.search(val, event)
        };
        return <Search {...props} />
    }

    private uploadInput(): React.ReactElement {
        const props: UploadProps = {
            name: "file",
            action: api.parseUrl(),
            showUploadList: false,
            onChange: (info) => {
                if (info.file.status === 'done') {
                    const response: any = info.file.response;
                    if(response.error) {
                        message.error(response.error);
                    } else {
                        this.props.searchEvent({paper: response.paper, timestamp: new Date().getTime()});
                        message.success(`${info.file.name} file uploaded successfully`);
                    }
                } else if (info.file.status === 'error') {
                    message.error(`${info.file.name} file upload failed.`);
                }
            }
        };
        return (
            <span className="search-upload">or&nbsp;
                <Upload {...props}>
                    <Button type="link">upload article</Button>
                </Upload>
            </span>
        )
    }

    render(): React.ReactElement {
        return (
            <div className="search-panel">
                <h1 className="search-title">scinote</h1>
                <div className="search-input">
                    {this.searchInput()}
                    {this.uploadInput()}
                </div>
            </div>
        )
    }
}
