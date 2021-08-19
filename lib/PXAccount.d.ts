import { JsonMap } from "./RNBridge";
import { PXGeodetails } from "./PXGeodetails";
export declare class PXAccount extends PXGeodetails {
    id: string;
    name: string;
    trackedSubscriptionId: string;
    industry: string;
    numberOfEmployees: number;
    sicCode: string;
    website: string;
    naicsCode: string;
    plan: string;
    sfdcId: string;
    customAttributes: JsonMap;
    constructor(id: string);
    toJson(): any;
}
