import { PXGeodetails } from "./PXGeodetails";
export class PXUser extends PXGeodetails {
    constructor(userId) {
        super();
        this.ide = userId;
    }
    toJson() {
        const jstring = JSON.stringify(this);
        return JSON.parse(jstring);
    }
}
