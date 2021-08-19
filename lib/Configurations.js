export class Configurations {
    constructor(apiKey) {
        this.flushQueueSize = 20;
        this.flushInterval = 30;
        this.enableLogs = false;
        this.trackApplicationLifeCycleEvents = true;
        this.shouldTrackTapEvents = false;
        this.enable = true;
        this.collectDeviceId = true;
        this.apiKey = apiKey;
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
})(PXHost || (PXHost = {}));
