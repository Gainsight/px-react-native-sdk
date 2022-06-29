# React Native GainsightPX

- Release versions
  - [1.7.3](#173)
  - [1.3.0](#130)
  - [1.2.2](#122)
  - [1.2.1](#121)
  - [1.2.0](#120)
  - [0.1.8](#018)
- [Installation React Native](#installation-react-native)
- [iOS](#iOS)
- [Android](#android)
- [Usage in React-Native](#usage-react-native)
  _ [import](#import)
  _ [Configurations](#configurations)
  _ [Initialisation](#initilisation)
  _ [Screen Event](#screen-event)
  _ [Custom Event](#custom-event)
  _ [Identify](#identify)
  _ [User](#user)
  _ [Account](#account)

<a name="173">**1.7.3**</a>

- Release Notes:
  - Added `maxQueueSize` to Configurations.
  - Auto linking iOS files to project.

<a name="130">**1.3.0**</a>

- Editor Product Mapper preview.

- Release Notes:
  - Remove `isReady` in `GainsightPX` bridge file.

<a name="122">**1.2.2**</a>

- Release Notes:
  - Remove `isReady` in `GainsightPX` bridge file.

<a name="121">**1.2.1**</a>

- Release Notes:
  - Fix for tracking back button taps in iOS SDK.

<a name="120">**1.2.0**</a>

- Release Notes:
  - Introducing `Editor` to support preview engagements and map UI elements to features from mobile.
  - Scan QRCode from dashboard to preview engagements or create mapper feature.
  - Deprecated `sharedInstance`.
  - Supporting `Bar` type dialog engagements.
  - Fix for video in engagement by changing window level.
  - Fix for window access after completing engagement preview from editor.
  - Fix to block all the events when user is in editing mode.

<a name="018">**0.1.8**</a>

- Release Notes:
  - Added proxy to Configurations
  - Removed `recordScreenViews` from Configurations

## <a name = "installation-react-native">Installation React Native </a>

> Download `react-native-gainsight-px-1.0.0.tgz`

> Add dependencies to you project

```json
 "dependencies": {
    ...,
    ...,
    "react-native-gainsight-px": "file:/<path-to>/react-native-gainsight-px-1.0.0.tgz"
  }
```

> And

```
$ npm install
```

Or

> In React-native project folder do

`$ npm install <path-to-react-native-gainsight-px-1.0.0.tgz>`

## <a name="iOS">Installation iOS</a>

> Download `GainsightPX.framework` from url: `<gainsight-framework-url>`

> In Xcode Make sure that you select `target` Add the `framework` to the `Embedded Binaries`

> In terminal

```
$ cd <project-folder>
$ react-native run-ios
```

## <a name="android">Installation Android</a>

> TODO:

## <a name = "usage-react-native">Usage in React-Native </a>

####<a name="import">Import</a>

```javascript
import { GainsightPX, Configurations, PXAccount, PXUser } from "react-native-gainsight-px";
```

####<a name="configurations">Configurations</a>

> TODO:

#### <a name="initilisation">Initilisation</a>

```javascript
let c = new Configurations("<#API KEY#>");
c.enableLogs = true;
GainsightPX.getInstance().initialize(c);
```

#### <a name="screen-event">Screen Event</a>

> Custom Screen

```javascript
GainsightPX.getInstance().screenEvent("<#screen-name#>", "<#screen-class#>");
```

> Only Screen Name

```javascript
GainsightPX.getInstance().screen("<#screen-name#>", "<#map#>");
```

#### <a name="custom-event">Custom Event</a>

```javascript
GainsightPX.getInstance().track("<#event-name#>", "<#map#>");
```

## Internal

# react-native-gainsight-px

## publishing to Nexus

> In package Root make sure you have `.npmrc` file

```
npm run release
```

## local-linking

> In package Root

```
npm run build
```

> In Sample Application after npm install install the `react-native-gainsight-px-1.0.0.tgz` file

> To install this file `react-native-gainsight-px-1.0.0.tgz`

```
$ cd RealSample
npm install ../react-native-gainsight-px-1.0.0.tgz
```

> This command will add the path to the package.json in the Application

## Getting started

> Make sure you have `.npmrc` file

`$ npm install react-native-gainsight-px --save`

### Mostly automatic installation

`$ react-native link react-native-gainsight-px`

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-gainsight-px` and add `RNGainsightPx.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNGainsightPx.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`

- Add `import com.reactlibrary.RNGainsightPxPackage;` to the imports at the top of the file
- Add `new RNGainsightPxPackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:
   ```
   include ':react-native-gainsight-px'
   project(':react-native-gainsight-px').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-gainsight-px/android')
   ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
   ```
     compile project(':react-native-gainsight-px')
   ```

## Usage

```javascript
import RNGainsightPx from "react-native-gainsight-px";

// TODO: What to do with the module?
RNGainsightPx;
```
