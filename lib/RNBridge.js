import { NativeModules } from "react-native";
if (!NativeModules.RNGainsightPx) {
    throw new Error("Failed to load Gainsight PX native module.");
}
export const Bridge = NativeModules.RNGainsightPx;
