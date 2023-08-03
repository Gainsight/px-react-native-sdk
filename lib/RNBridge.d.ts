export declare type JsonValue = boolean | number | string | null | JsonList | JsonMap;
export interface JsonMap {
    [key: string]: JsonValue;
    [index: number]: JsonValue;
}
export interface GainsightPXCallback {
    status: GainsightPXCallbackStatus;
    methodName?: string;
    params?: object;
    exceptionMessage?: string;
}
export declare const enum GainsightPXCallbackStatus {
    FAILURE = 0,
    SUCCESS = 1
}
export declare type GlobalContextJsonValue = boolean | number | string | null | Date;
export interface GlobalContextJsonMap {
    [key: string]: GlobalContextJsonValue;
    [index: number]: GlobalContextJsonValue;
}
export interface JsonList extends Array<JsonValue> {
}
export interface RNGainsightPx {
    initialize(configuration: JsonMap): Promise<GainsightPXCallback>;
    custom(event: string): Promise<GainsightPXCallback>;
    customEventWithProperties(event: string, properties?: JsonMap): Promise<GainsightPXCallback>;
    screenWithTitle(name: string): Promise<GainsightPXCallback>;
    screen(name: string, properties?: JsonMap): Promise<GainsightPXCallback>;
    screenEvent(name: string, className: string, properties?: JsonMap): Promise<GainsightPXCallback>;
    identifyUserId(userId: string): Promise<GainsightPXCallback>;
    identifyUser(user: JsonMap): Promise<GainsightPXCallback>;
    identify(user: JsonMap, account?: JsonMap): Promise<GainsightPXCallback>;
    flush(): Promise<GainsightPXCallback>;
    exitEditing(): Promise<GainsightPXCallback>;
    enterEditing(url: string): Promise<GainsightPXCallback>;
    enable(): Promise<GainsightPXCallback>;
    disable(): Promise<GainsightPXCallback>;
    setGlobalContext(map?: GlobalContextJsonMap): Promise<GainsightPXCallback>;
    hasGlobalContextKey(key: string): Promise<boolean>;
    removeGlobalContextKeys(keys: string[]): Promise<GainsightPXCallback>;
    reset(): Promise<GainsightPXCallback>;
    hardReset(): Promise<GainsightPXCallback>;
    enableEngagements(enable: boolean): Promise<GainsightPXCallback>;
}
export declare const Bridge: RNGainsightPx;
