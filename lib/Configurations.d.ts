export declare class Configurations {
    apiKey: string;
    flushQueueSize: number;
    maxQueueSize: number;
    flushInterval: number;
    enableLogs: boolean;
    trackApplicationLifeCycleEvents: boolean;
    shouldTrackTapEvents: boolean;
    enable: boolean;
    collectDeviceId: boolean;
    proxy?: string;
    host?: PXHost;
    reportTrackingIssues: boolean;
    android?: {};
    ios?: {};
    constructor(apiKey: string);
    toJson(): any;
}
export declare enum PXHost {
    us = "us",
    eu = "eu",
    us2 = "us2"
}
