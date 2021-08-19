export class PXGeodetails {
    toJson() {
        const jstring = JSON.stringify(this);
        return JSON.parse(jstring);
    }
}
