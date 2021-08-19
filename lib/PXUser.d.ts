import { JsonMap } from "./RNBridge";
import { PXGeodetails } from "./PXGeodetails";
export declare class PXUser extends PXGeodetails {
    email: string;
    usem: string;
    userHash: string;
    gender: string;
    lastName: string;
    firstName: string;
    signUpDate: Date;
    title: string;
    role: string;
    subscriptionId: string;
    phone: string;
    organization: string;
    organizationEmployees: string;
    organizationRevenue: string;
    organizationIndustry: string;
    organizationSicCode: string;
    organizationDuns: number;
    accountId: string;
    firstVisitDate: Date;
    score: string;
    sfdcContactId: string;
    customAttributes: JsonMap;
    private ide;
    constructor(userId: string);
    toJson(): any;
}
