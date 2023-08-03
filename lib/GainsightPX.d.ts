import { Configurations } from "./Configurations";
import { JsonMap, GlobalContextJsonMap, GainsightPXCallback } from "./RNBridge";
import { PXUser } from "./PXUser";
import { PXAccount } from "./PXAccount";
export declare class GainsightPX {
    static getInstance(): GainsightPX;
    private static instance;
    initialize(config: Configurations): Promise<GainsightPXCallback>;
    customEvent(name: string): Promise<GainsightPXCallback>;
    customEventWithProperties(name: string, properties?: JsonMap): Promise<GainsightPXCallback>;
    screen(name: string, properties?: JsonMap): Promise<GainsightPXCallback>;
    screenEvent(name: string, className: string, properties?: JsonMap): Promise<GainsightPXCallback>;
    identifyWithUserId(userId: string): Promise<GainsightPXCallback>;
    identify(user: PXUser, account?: PXAccount): Promise<GainsightPXCallback>;
    flush(): Promise<GainsightPXCallback>;
    enterEditing(url: string): Promise<GainsightPXCallback>;
    exitEditing(): Promise<GainsightPXCallback>;
    enable(): Promise<GainsightPXCallback>;
    setGlobalContext(globalContextData?: GlobalContextJsonMap): Promise<GainsightPXCallback>;
    removeGlobalContextKeys(keys: string[]): Promise<GainsightPXCallback>;
    hasGlobalContextKey(key: string): Promise<boolean>;
    disable(): Promise<GainsightPXCallback>;
    reset(): Promise<GainsightPXCallback>;
    hardReset(): Promise<GainsightPXCallback>;
    enableEngagements(enable: boolean): Promise<GainsightPXCallback>;
    private execute;
    private createError;
}
