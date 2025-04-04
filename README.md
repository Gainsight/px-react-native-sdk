# React Native GainsightPX

[![N|Solid](https://app-dev.aptrinsic.com/home/gainsight-px-logo.svg)](https://app.aptrinsic.com)

![version](https://img.shields.io/badge/version-1.12.3-blue.svg)

# Installation

GainsighPX is available through npm.

### npm

```json
 "dependencies": {
    ...,
    ...,
    "@gainsight-px/react-native-gainsight-px": "https://github.com/Gainsight/px-react-native-sdk.git"
  }
```

OR

```
npm install https://github.com/Gainsight/px-react-native-sdk.git --save
```

ALSO it's available directly [on npm](https://www.npmjs.com/package/@gainsight-px/react-native-gainsight-px)

```
npm i @gainsight-px/react-native-gainsight-px
```

### Expo

Run the expo command as directed after installing the package using the npm command.

```
npx expo run:ios
```

```
npx expo run:android
```

#### Building on EAS (Expo Application Service)

If there are problem building the apps on EAS or even locally, please add the following to your `package.json` file:

```
"expo": {
    "autolinking": {
      "exclude": ["@gainsight-px/react-native-gainsight-px"]
    }
  }
```

## Usage

```javascript
import { GainsightPX, Configurations, PXUser } from "@gainsight-px/react-native-gainsight-px";
```

# Documentation

More detailed documentation is available at: <https://support.gainsight.com/PX/Mobile/Mobile_Platforms/Install_Gainsight_PX_React-Native>

## Editor Deeplinking

More detailed documentation is available at: <https://support.gainsight.com/PX/Mobile/01Getting_Started/Integrate_Gainsight_PX_Editor_with_your_Mobile_Platform>

## License

MIT License

Copyright (c) 2019 aptrinsic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
