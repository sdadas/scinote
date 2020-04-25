import * as React from "react";
import EasyEdit from 'react-easy-edit';
import {RefObject} from "react";

interface InplaceProps {
    [index: string]:any;
}

export class Inplace extends React.Component<InplaceProps, any> {

    private ref: RefObject<EasyEdit>;

    constructor(props: Readonly<any>) {
        super(props);
        this.state = {};
        this.ref = React.createRef();
    }

    private onBlur() {
        const current: EasyEdit = this.ref.current;
        setTimeout(() => current._onSave(), 100);

    }

    render(): React.ReactElement {
        let editComponent = this.props["editComponent"];
        if(editComponent) {
            editComponent = React.cloneElement(editComponent, {onBlur: () => this.onBlur()});
        }
        return (
            <EasyEdit type="text" saveButtonLabel="Save" cancelButtonLabel="Cancel" ref={this.ref}
                      onBlur={() => this.onBlur()} {...this.props} editComponent={editComponent} />
        )
    }
}
