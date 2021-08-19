import { NativeModules } from "react-native";
if (!NativeModules.RNGainsightPx) {
    throw new Error("Failed to load Analytics native module.");
}
export const Bridge = NativeModules.RNGainsightPx;
