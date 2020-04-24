import * as React from "react";
import {HashRouter, Route, Switch} from "react-router-dom";
import {HomeView} from "./component/home/home";
import {UIAction} from "./model";
import ProjectsView from "./component/projects/projects";
import {ProjectView} from "./component/project/project";
import {SearchPanel} from "./component/search/search";

interface ApplicationState {
    action?: UIAction;
}

export class Application extends React.Component<any, ApplicationState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {action: null};
    }

    private actionEvent(result: UIAction) {
        result.timestamp = new Date().getTime();
        this.setState({...this.state, action: result});
    }

    render(): React.ReactElement {
        return (
            <HashRouter>
                <div className="app-layout">
                    <section className="projects-section">
                        <ProjectsView {...{action: this.state.action}} />
                    </section>
                    <section className="content-section">
                        <SearchPanel actionEvent={(res) => this.actionEvent(res)} />
                        <div className="content-panel">
                            <Switch>
                                <Route exact path="/" component={HomeView}  />
                                <Route path="/project/:id"  render={(props) => {
                                    return <ProjectView {...props.match.params} action={this.state.action}
                                                        actionEvent={(res) => this.actionEvent(res)} />
                                }}/>
                            </Switch>
                        </div>
                    </section>
                </div>
            </HashRouter>
        );
    }
}
