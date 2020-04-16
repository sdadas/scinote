import * as React from "react";
import {Button, Input, Layout, message, Upload} from 'antd';
import {Route, Switch, HashRouter} from "react-router-dom";
import {HomeView} from "./component/home/home";
import {api} from "./service/api";
import {Paper, SearchResult} from "./model";
import {SearchProps} from "antd/lib/input/Search";
import {PaperCard} from "./component/paper";
import {UploadProps} from "antd/lib/upload/Upload";
import ProjectsView from "./component/projects/projects";
import {ProjectView} from "./component/project/project";
import {SearchPanel} from "./component/search/search";

interface ApplicationState {
    searchResult?: SearchResult;
}

export class Application extends React.Component<any, ApplicationState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {searchResult: null};
    }

    private searchEvent(result: SearchResult) {
        this.setState({...this.state, searchResult: result});
    }

    render(): React.ReactElement {
        return (
            <HashRouter>
                <div className="app-layout">
                    <section className="projects-section">
                        <ProjectsView />
                    </section>
                    <section className="content-section">
                        <SearchPanel searchEvent={(res) => this.searchEvent(res)} />
                        <div className="content-panel">
                            <Switch>
                                <Route exact path="/" component={HomeView}  />
                                <Route path="/project/:id"  render={(props) => {
                                    return <ProjectView {...props.match.params} searchResult={this.state.searchResult} />
                                }}/>
                            </Switch>
                        </div>
                    </section>
                </div>
            </HashRouter>
        );
    }
}
