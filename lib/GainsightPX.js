import { Bridge } from "./RNBridge";
export class GainsightPX {
    static getInstance() {
        if (!GainsightPX.instance) {
            GainsightPX.instance = new GainsightPX();
        }
        return GainsightPX.instance;
    }
    initialize(config) {
        return this.execute(() => {
            return Bridge.initialize(config.toJson());
        });
    }
    customEvent(name) {
        return this.execute(() => {
            return Bridge.custom(name);
        });
    }
    customEventWithProperties(name, properties) {
        return this.execute(() => {
            return Bridge.customEventWithProperties(name, properties);
        });
    }
    screen(name, properties) {
        return this.execute(() => {
            return Bridge.screen(name, properties);
        });
    }
    screenEvent(name, className, properties) {
        return this.execute(() => {
            return Bridge.screenEvent(name, className, properties);
        });
    }
    identifyWithUserId(userId) {
        return this.execute(() => {
            return Bridge.identifyUserId(userId);
        });
    }
    identify(user, account) {
        return this.execute(() => {
            if (account) {
                return Bridge.identify(user.toJson(), account.toJson());
            }
            else {
                return Bridge.identifyUser(user.toJson());
            }
        });
    }
    flush() {
        return this.execute(() => {
            return Bridge.flush();
        });
    }
    enterEditing(url) {
        return this.execute(() => {
            return Bridge.enterEditing(url);
        });
    }
    exitEditing() {
        return this.execute(() => {
            return Bridge.exitEditing();
        });
    }
    enable() {
        return this.execute(() => {
            return Bridge.enable();
        });
    }
    setGlobalContext(globalContextData) {
        return this.execute(() => {
            if (globalContextData == null) {
                return Bridge.setGlobalContext(null);
            }
            const obj = {};
            for (const k in globalContextData) {
                if (Object.prototype.toString.call(globalContextData[k]) === "[object Date]") {
                    obj[k] = globalContextData[k].toISOString();
                }
                else if (typeof globalContextData[k] === "number" ||
                    typeof globalContextData[k] === "string" ||
                    typeof globalContextData[k] === "boolean") {
                    obj[k] = globalContextData[k];
                }
                else {
                    console.log('key "' + k + "\" isn't a valid value type and was ignored");
                }
            }
            return Bridge.setGlobalContext(obj);
        });
    }
    removeGlobalContextKeys(keys) {
        return this.execute(() => {
            return Bridge.removeGlobalContextKeys(keys);
        });
    }
    async hasGlobalContextKey(key) {
        const k = await Bridge.hasGlobalContextKey(key);
        return k;
    }
    disable() {
        return this.execute(() => {
            return Bridge.disable();
        });
    }
    execute(callback) {
        return callback()
            .then(() => {
            const response = { status: 1 /* SUCCESS */ };
            return Promise.resolve(response);
        })
            .catch(error => {
            return Promise.reject(this.createError(error));
        });
    }
    createError(error) {
        const gainsightPXCallback = {
            status: 0 /* FAILURE */,
            methodName: error.userInfo.methodName,
            params: error.userInfo.params,
            exceptionMessage: error.userInfo.exceptionMessage
        };
        return gainsightPXCallback;
    }
}
