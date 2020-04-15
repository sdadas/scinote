import * as React from "react";

export class HomeView extends React.Component<any, any> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
    }

    render(): React.ReactElement {
        return (
            <div>
                <h1>Home View</h1>
                <h2>Home View</h2>
                <h3>Home View</h3>
                <h4>Home View</h4>
                <h5>Home View</h5>
                <h6>Home View</h6>
            </div>
        )
    }
}
