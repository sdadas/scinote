import * as React from "react";
import { Input, Select } from 'antd';
import { FilterOutlined, SortAscendingOutlined } from '@ant-design/icons';
import {AppUtils} from "../../utils";
import {Paper, PaperDetails, ProjectPaper} from "../../model";
const { Option } = Select;

export interface FiltersObject {
    text?: string;
    tags?: string[];
    sort?: string;
}

interface FiltersProps {
    onChange: (val: FiltersObject) => void;
    tags: string[];
}

export class FilterMatcher {

    private text: string[];
    private tags: string[];
    private sort: string;

    constructor(filter: FiltersObject) {
        this.text = filter.text && filter.text.trim().length > 0 ? this.createText(filter.text) : null;
        this.tags = filter.tags && filter.tags.length > 0 ? filter.tags : null;
        this.sort = filter.sort ? filter.sort : "default";
    }

    private createText(value: string) : string[] {
        const text = AppUtils.removeNonAlphanumeric(value.toLowerCase());
        return text.split(" ").filter(val => val.trim().length > 0);
    }

    public matches(paper: Paper, projectPaper: ProjectPaper) {
        if(!paper || !projectPaper) return true;
        if(this.text != null) {
            for(const word of this.text) {
                if(!paper.cachedText.has(word)) {
                    return false;
                }
            }
        }
        if(this.tags != null) {
            for(const tag of this.tags) {
                if(!projectPaper.tags || projectPaper.tags.indexOf(tag) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public sortFunction(): (o1: PaperDetails, o2: PaperDetails) => number {
        if (this.sort == "title:asc") {
            return FilterMatcher.sortTitleAsc;
        } else if(this.sort == "title:desc") {
            return FilterMatcher.sortTitleDesc;
        } else if(this.sort == "year:asc") {
            return FilterMatcher.sortYearAsc;
        } else if(this.sort == "year:desc") {
            return FilterMatcher.sortYearDesc;
        } else if(this.sort == "citations") {
            return FilterMatcher.sortCitations;
        } else {
            return FilterMatcher.sortDefault;
        }
    }

    private static sortDefault(o1: PaperDetails, o2: PaperDetails): number {
        return (o2.projectPaper.added||0) - (o1.projectPaper.added||0);
    }

    private static sortTitleAsc(o1: PaperDetails, o2: PaperDetails): number {
        return (o1.paper.title||"").localeCompare((o2.paper.title||""));
    }

    private static sortTitleDesc(o1: PaperDetails, o2: PaperDetails): number {
        return (o2.paper.title||"").localeCompare((o1.paper.title||""));
    }

    private static sortYearDesc(o1: PaperDetails, o2: PaperDetails): number {
        return (o2.paper.year||0) - (o1.paper.year||0);
    }

    private static sortYearAsc(o1: PaperDetails, o2: PaperDetails): number {
        return (o1.paper.year||9999) - (o2.paper.year||9999);
    }

    private static sortCitations(o1: PaperDetails, o2: PaperDetails): number {
        return (o2.paper.citations||0) - (o1.paper.citations||0);
    }
}

export class FiltersComponent extends React.Component<FiltersProps, FiltersObject> {

    private readonly debouncedChange: any;

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
        this.debouncedChange = this.debounce(() => this.props.onChange(this.state), 250, false);
    }

    private tagsChanged(values: string[]) {
        const state = {...this.state, tags: values};
        this.setState(state);
        this.props.onChange(state);
    }

    private textChanged(event: any) {
        const value = event.target.value;
        const state = {...this.state, text: value};
        this.setState(state);
        this.debouncedChange();
    }

    private debounce(func, wait, immediate) {
        let timeout;
        return function() {
            let context = this, args = arguments;
            let later = function() {
                timeout = null;
                if (!immediate) func.apply(context, args);
            };
            let callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) func.apply(context, args);
        }
    }

    private sortChanged(value: string) {
        const state = {...this.state, sort: value};
        this.setState(state);
        this.props.onChange(state);
    }

    render(): React.ReactElement {
        const tags = this.props.tags || [];
        const options = tags.map(val => <Option key={val} value={val}>{val}</Option>);
        return (
            <div className="project-filters">
                <Input className="project-filters-search" placeholder="Filter by text" suffix={<FilterOutlined />}
                       onChange={val => this.textChanged(val)} allowClear={true} />
                <Select className="project-filters-tags" mode="multiple" placeholder="Filter by tags" defaultValue={[]}
                        onChange={val => this.tagsChanged(val)} allowClear={true} maxTagCount={3} maxTagTextLength={10}>
                    {options}
                </Select>
                <Select className="project-filters-sort" defaultValue="default" onChange={val => this.sortChanged(val)}
                        suffixIcon={<SortAscendingOutlined />}>
                    <Option key="default" value="default">Date added</Option>
                    <Option key="citations" value="citations">Citations</Option>
                    <Option key="title:asc" value="title:asc">Title ascending</Option>
                    <Option key="title:desc" value="title:desc">Title descending</Option>
                    <Option key="year:asc" value="year:asc">Year ascending</Option>
                    <Option key="year:desc" value="year:desc">Year descending</Option>
                </Select>
            </div>
        )
    }
}
