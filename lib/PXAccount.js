import { PXGeodetails } from "./PXGeodetails";
export class PXAccount extends PXGeodetails {
    constructor(id) {
        super();
        this.id = id;
    }
    toJson() {
        const jstring = JSON.stringify(this);
        return JSON.parse(jstring);
    }
}
