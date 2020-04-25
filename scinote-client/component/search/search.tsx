import * as React from "react";
import {UploadProps} from "antd/lib/upload/Upload";
import {api} from "../../service/api";
import {Button, Input, message, Spin, Upload} from "antd";
import {SearchProps} from "antd/lib/input/Search";
import {UIAction} from "../../model";
import { LoadingOutlined } from '@ant-design/icons';
const { Search } = Input;

interface SearchPanelProps {
    actionEvent: Function;
}

interface SearchPanelState {
    search: string;
    loading: boolean;
    parseServiceAvailable: boolean;
}

export class SearchPanel extends React.Component<SearchPanelProps, SearchPanelState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {search: "", loading: false, parseServiceAvailable: false};
    }

    componentDidMount(): void {
        this.isParseServiceAvailable();
    }

    private isParseServiceAvailable() {
        api.parseServiceAvailable().then(res => {
            this.setState({...this.state, parseServiceAvailable: res});
        }).catch(err => message.error(err));
    }

    private search(query: string, event: any): void {
        event.preventDefault();
        if(!query.trim()) return;
        this.setState({...this.state, loading: true});
        api.search(query).then(val => {
            if(val.length > 0) {
                this.props.actionEvent({payload: val[0], type: "SEARCH"} as UIAction);
            }
            this.setState({...this.state, search: "", loading: false});
        }).catch(err => {
            message.error(err.toString());
            this.setState({...this.state, loading: false});
        });
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
                        this.props.actionEvent({payload: response.paper, type: "SEARCH"} as UIAction);
                        message.success(`${info.file.name} file uploaded successfully`);
                    }
                    this.setState({...this.state, loading: false});
                } else if (info.file.status === 'error') {
                    message.error(`${info.file.name} file upload failed.`);
                    this.setState({...this.state, loading: false});
                } else if (info.file.status === "uploading") {
                    this.setState({...this.state, loading: true});
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
                <img src={api.logoUrl()} alt="scinote" title="scinote" style={{width: "200px", opacity: 0.85}} />
                <div className="search-input">
                    <Spin tip="Searching..." indicator={<LoadingOutlined spin />} spinning={this.state.loading}>
                        {this.searchInput()}
                        {this.state.parseServiceAvailable ? this.uploadInput() : null}
                    </Spin>
                </div>
            </div>
        )
    }
}
