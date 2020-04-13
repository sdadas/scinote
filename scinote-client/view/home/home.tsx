import * as React from "react";

export class HomeView extends React.Component<any, any> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
    }

    render(): React.ReactElement {
        return (
            <div><h1>Home View</h1></div>
        )
    }
}
