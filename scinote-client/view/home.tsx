import * as React from "react";
import { Input } from 'antd';
import {Paper} from "../model";
import {PaperCard} from "../component/paper";
import {api} from "../service/api";
const { Search } = Input;

interface HomeViewState {
    papers: Paper[];
    search: string;
}

export class HomeView extends React.Component<any, HomeViewState> {

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {papers: [], search: ""};
    }

    private search(query: string, event: any): void {
        if(!query.trim()) return;
        api.search(query)
            .then(val => this.addResults(val))
            .catch(err => console.log(err));
    }

    private addResults(results: Paper[]) {
        const papers = this.state.papers.concat(results);
        this.setState({...this.state, papers: papers, search: ""});
    }

    private searchInput(): React.ReactElement {
        const ph = "Enter article title, doi, url etc.";
        return <Search className="search-input" value={this.state.search} placeholder={ph} size="large"
                       onChange={val => this.setState({...this.state, search: val.target.value})}
                       onSearch={(val, event) => this.search(val, event)} />
    }

    private results(): React.ReactElement {
        const cards = [...this.state.papers].reverse().map((val, idx) => <PaperCard paper={val} key={idx.toString()} />);
        return (
            <div className="results-panel">
                {cards}
            </div>
        )
    }

    render(): React.ReactElement {
        return (
            <div className="search-panel">
                <h1 className="search-title">Sciggle</h1>
                {this.searchInput()}
                {this.results()}
            </div>
        )
    }
}
