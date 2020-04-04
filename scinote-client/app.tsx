import * as React from "react";
import { Layout } from 'antd';
import {Route, Switch, HashRouter} from "react-router-dom";
import {HomeView} from "./view/home";

export class Application extends React.Component<any, any> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
    }

    render(): React.ReactElement {
        return (
            <HashRouter>
                <Layout>
                    <Switch>
                        <Route exact path="/" component={HomeView}  />
                    </Switch>
                </Layout>
            </HashRouter>
        );
    }
}
