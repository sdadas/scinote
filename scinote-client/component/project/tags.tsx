import * as React from "react";
import { Select } from 'antd';

interface TagsInputProps {
    setParentValue?: Function;
    onChange?: Function;
    onBlur?: Function;
    value?: string[];
}

export class TagsInput extends React.Component<TagsInputProps, any> {

    constructor(props: Readonly<TagsInputProps>) {
        super(props);
        this.state = {};
    }

    private onChange(value: string[]) {
        if(this.props.setParentValue) {
            this.props.setParentValue(value);
        }
        if(this.props.onChange) {
            this.props.onChange(value);
        }
    }

    render(): React.ReactElement {
        const style = {width: "100%", marginTop: "2px"};
        return (
            <Select mode="tags" value={this.props.value} style={style} onChange={val => this.onChange(val)}
                    onBlur={this.props.onBlur as any} defaultOpen={true} autoFocus={true} />
        )
    }
}
