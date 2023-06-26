export class Configurations {
    constructor(apiKey) {
        this.flushQueueSize = 20;
        this.maxQueueSize = 1000;
        this.flushInterval = 30;
        this.enableLogs = false;
        this.trackApplicationLifeCycleEvents = true;
        this.shouldTrackTapEvents = false;
        this.enable = true;
        this.collectDeviceId = true;
        this.reportTrackingIssues = false;
        this.apiKey = apiKey;
        this.isTrackTapInAllLayouts = false;
    }
    toJson() {
        const json = JSON.stringify(this);
        return JSON.parse(json);
    }
}
export var PXHost;
(function (PXHost) {
    PXHost["us"] = "us";
    PXHost["eu"] = "eu";
    PXHost["us2"] = "us2";
})(PXHost || (PXHost = {}));
