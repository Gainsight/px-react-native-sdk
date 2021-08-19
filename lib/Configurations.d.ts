export declare class Configurations {
    apiKey: string;
    flushQueueSize: number;
    flushInterval: number;
    enableLogs: boolean;
    trackApplicationLifeCycleEvents: boolean;
    shouldTrackTapEvents: boolean;
    enable: boolean;
    collectDeviceId: boolean;
    proxy?: string;
    host?: PXHost;
    android?: {};
    ios?: {};
    constructor(apiKey: string);
    toJson(): any;
}
export declare enum PXHost {
    us = "us",
    eu = "eu"
}
