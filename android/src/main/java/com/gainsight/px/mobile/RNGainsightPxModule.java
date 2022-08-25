package com.gainsight.px.mobile;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;
import android.os.Handler;
import android.graphics.Rect;
import android.graphics.Point;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

@SuppressWarnings("unchecked")
public class RNGainsightPxModule extends ReactContextBaseJavaModule {

  public static final String KEY_ENABLE = "enable";
  private static final String TAG = "GainsightPX";
  private final ReactApplicationContext reactContext;
  private Handler handler;
  private Runnable runnable;
  private final int delayMillis = 1000;
  private final UIDelegate delegate = new UIDelegate() {
    @Override
    public void startTrackingUserInteractions(InteractionReport interactionReport, Activity activity) {}

    @Override
    public String getContainerViewClass() { return getName(); }

    @Override
    public void getViewAtPosition(Point point, final UIDelegate.Callback<TreeBuilder> callback) {
      callback.onResponse(null);
    }

    @Override
    public void startDomBuilder(final Callback<JSONObject> callback) {
      callback.onResponse(null);
      handler = new Handler(Looper.getMainLooper());
      runnable = new Runnable() {
        @Override
        public void run() {
          Activity activity = getCurrentActivity();
          if (null != activity) {
            ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
            if (rootView != null) {
              rootView.requestLayout();
            }
          }
          if (handler != null) {
            handler.postDelayed(runnable, delayMillis);
          }
        }
      };
      handler.postDelayed(runnable, delayMillis);
    }

    @Override
    public void stopDomBuilder() {
      if (handler != null && runnable != null) {
        handler.removeCallbacks(runnable);
        handler = null;
        runnable = null;
      }
    }

    @Override
    public void startScrollListener(Callback<Boolean> callback) {}

    @Override
    public void stopScrollListener() {}

    @Override
    public void getViewPosition(final JSONObject jsonObject, final Callback<Rect> callback) {
      callback.onResponse(null);
    }
  };

  public RNGainsightPxModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNGainsightPx";
  }

  //  initialize(configuration: JsonMap): Promise<void>
  @ReactMethod
  public void initialize(ReadableMap configuration, final Promise promise) {
    try {
      ReactApplicationContext reactContext = getReactApplicationContext();
      Context context = reactContext;
      if (null != reactContext) {
        Activity activity = getCurrentActivity();
        if (null != activity) {
          context = activity;
        }
      }
      String apiKey = configuration.getString("apiKey");
      apiKey += ",RN";
      // it could crash if not exist. which is still good for us
      GainsightPX.Builder builder = new GainsightPX.Builder(context, apiKey, new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("initialize", params, exceptionMessage));
        }
      });
      builder.engagementCallback(new GainsightPX.EngagementCallback() {
        public boolean onCallback(EngagementMetaData engagementMetaData) {
          WritableMap args = Arguments.createMap();
          args.putString("engagementId", engagementMetaData.engagementId);
          args.putString("engagementName", engagementMetaData.engagementName);
          args.putString("actionText", engagementMetaData.actionText);
          args.putString("actionData", engagementMetaData.actionData);
          args.putString("actionType", engagementMetaData.actionType);
          args.putMap("params", castToWritableMap(engagementMetaData.params));
          args.putMap("scope", castToWritableMap(engagementMetaData.scope));
          if (RNGainsightPxModule.this.reactContext != null && RNGainsightPxModule.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class) != null) {
            RNGainsightPxModule.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("engagementCallBack", args);
          }
          return true;
        }
      });
      if (configuration.hasKey("shouldTrackTapEvents")) {
        builder.shouldTrackTapEvents(configuration.getBoolean("shouldTrackTapEvents"));
      }
      if (configuration.hasKey("reportTrackingIssues")) {
        builder.shouldReportIssuesToServer(configuration.getBoolean("reportTrackingIssues"));
      }
      if (configuration.hasKey("flushQueueSize")) {
        builder.flushQueueSize(configuration.getInt("flushQueueSize"));
      }
      if (configuration.hasKey("maxQueueSize")) {
        builder.maxQueueSize(configuration.getInt("maxQueueSize"));
      }
      if (configuration.hasKey("flushInterval")) {
        builder.flushInterval(configuration.getInt("flushInterval"), TimeUnit.SECONDS);
      }
      if (configuration.hasKey("enableLogs")) {
        if (configuration.getBoolean("enableLogs")) {
          builder.logLevel(LogLevel.VERBOSE);
        } else {
          builder.logLevel(LogLevel.NONE);
        }
      }
      if (configuration.hasKey("trackApplicationLifeCycleEvents")) {
        builder.trackApplicationLifecycleEvents(configuration.getBoolean("trackApplicationLifeCycleEvents"));
      }
      // recordScreenViews should always disabled because we can't catch RN screen changes
      builder.recordScreenViews(false);
      if (configuration.hasKey("proxy")) {
        builder.proxy(configuration.getString("proxy"));
      } else if (configuration.hasKey("host")) {
        builder.pxHost(configuration.getString("host"));
      }
      if (configuration.hasKey("collectDeviceId")) {
        builder.collectDeviceId(configuration.getBoolean("collectDeviceId"));
      }
      GainsightPX instance;
      try {
        instance = builder.build();
      } catch (IllegalStateException e) {
        if (null != e.getMessage() && e.getMessage().contains("Duplicate gainsightPX client created with tag")) {
          instance = GainsightPX.with(context);
        } else {
          throw e;
        }
      }
      if (instance != null) {
        instance.addUiDelegate(this.delegate);
        if (configuration.hasKey(KEY_ENABLE)) {
          instance.setEnable(configuration.getBoolean(KEY_ENABLE));
        }
      }
      try {
        GainsightPX.setSingletonInstance(instance);
      } catch (IllegalStateException e) {
        if (!(null != e.getMessage() && e.getMessage().contains("Singleton instance already exists."))) {
          throw e;
        }
      }
      promise.resolve(onRsolve("initialize"));
    } catch (Throwable tr) {
      // promise.reject(tr);
      Log.e(TAG, "initialize: ", tr);
    }
  }

  @ReactMethod
//  custom(event: string): Promise<void>
  public void custom(String event, final Promise promise) {
    try {
      GainsightPX.with(getReactApplicationContext()).custom(event, new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("custom", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("custom"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "custom: ", tr);
    }
  }

  @ReactMethod
//	customEventWithProperties(event: string, properties?: JsonMap): Promise<void>
  public void customEventWithProperties(String event, ReadableMap properties, final Promise promise) {
    try {
      HashMap<String, Object> values = convertToMap(properties);
      GainsightPX.with(getReactApplicationContext()).custom(event, values, new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("customEventWithProperties", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("customEventWithProperties"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "customEventWithProperties: ", tr);
    }
  }

  @ReactMethod
  // screenWithTitle(name: string): Promise<void>
  public void screenWithTitle(String name, final Promise promise) {
    try {
      GainsightPX.with(getReactApplicationContext()).screen(name, new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("screenWithTitle", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("screenWithTitle"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "screenWithTitle: ", tr);
    }
  }

  @ReactMethod
  //	screen(name: string, properties?: JsonMap): Promise<void>
  public void screen(String name, ReadableMap properties, final Promise promise) {
    try {
      HashMap<String, Object> values = convertToMap(properties);
      GainsightPX.with(getReactApplicationContext()).screen(name, values, new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("screen", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("screen"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "screen: ", tr);
    }
  }

  @ReactMethod
  //	screenEvent(name: string, className: string, properties?: JsonMap): Promise<void>
  public void screenEvent(String name, String className, ReadableMap properties, final Promise promise) {
    try {
      HashMap<String, Object> values = convertToMap(properties);
      GainsightPX.with(getReactApplicationContext()).screen(new ScreenEventData(name).putScreenClass(className).putProperties(values), new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("screenEvent", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("screenEvent"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "screenEvent: ", tr);
    }
  }

  @ReactMethod
//    identifyUserId(userId: String): Promise<void>
  public void identifyUserId(String userId, final Promise promise) {
    try {
      GainsightPX.with(getReactApplicationContext()).identify(userId, new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("identifyUserId", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("identifyUserId"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "identifyUserId: ", tr);
    }
  }

  @ReactMethod
//  identifyUser(user: JsonMap): Promise<void>
  public void identifyUser(ReadableMap user, final Promise promise) {
    try {
      User gainsightUser = buildUser(user);
      GainsightPX.with(getReactApplicationContext()).identify(gainsightUser, new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("identifyUser", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("identifyUser"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "identifyUser: ", tr);
    }
  }

  @ReactMethod
//  identify(user: JsonMap, account?: JsonMap): Promise<void>
  public void identify(ReadableMap user, ReadableMap account, final Promise promise) {
    try {
      User gainsightUser = buildUser(user);
      Account gainsightAccount = buildAccount(account);
      GainsightPX.with(getReactApplicationContext()).identify(gainsightUser, gainsightAccount, new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("identify", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("identify"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "identify: ", tr);
    }
  }

  @ReactMethod
//  flush(): Promise<void>
  public void flush(final Promise promise) {
    try {
      GainsightPX.with(getReactApplicationContext()).flush(new GainsightPX.ExceptionHandler() {
        public void onExceptionOccurred(String methodName, ValueMap params, String exceptionMessage) {
          promise.reject(methodName, onReject("flush", params, exceptionMessage));
        }
      });
      promise.resolve(onRsolve("flush"));
    } catch (Throwable tr) {
      // promise.reject(e);
      Log.e(TAG, "flush: ", tr);
    }
  }

  @ReactMethod
//  enterEditing(url: string): Promise<void>
  public void enterEditing(String url, final Promise promise) {
    try {
      Uri uri = Uri.parse(url);
      Intent intent = new Intent();
      intent.setData(uri);
      GainsightPX.with(getReactApplicationContext()).enterEditingMode(intent);
      promise.resolve(onRsolve("enterEditing"));
    } catch (Throwable tr) {
      Log.e(TAG, "enterEditing: ", tr);
      promise.reject(tr);
    }
  }

  @ReactMethod
//  exitEditing(): Promise<void>
  public void exitEditing(final Promise promise) {
    try {
      GainsightPX.with(getReactApplicationContext()).exitEditingMode();
      promise.resolve(onRsolve("exitEditing"));
    } catch (Throwable tr) {
      Log.e(TAG, "exitEditing: ", tr);
      promise.reject(tr);
    }
  }

  @ReactMethod
//  setGlobalContext(globalContextJsonMap: ReadableMap): Promise<void>
  public void setGlobalContext(ReadableMap globalContextJsonMap, Promise promise) {
    try {
      if (globalContextJsonMap != null) {
        GlobalContextData globalContextData;
        if (GainsightPX.with(getReactApplicationContext()).getGlobalContext() != null) {
          globalContextData = GainsightPX.with(getReactApplicationContext()).getGlobalContext();
        } else {
          globalContextData = new GlobalContextData();
          GainsightPX.with(getReactApplicationContext()).setGlobalContext(globalContextData);
        }
        ReadableMapKeySetIterator iterator = globalContextJsonMap.keySetIterator();
        while (iterator.hasNextKey()) {
          String key = iterator.nextKey();
          ReadableType type = globalContextJsonMap.getType(key);
          if (type == ReadableType.String) {
            globalContextData.putString(key, globalContextJsonMap.getString(key));
          } else if (type == ReadableType.Number) {
            globalContextData.putNumber(key, globalContextJsonMap.getDouble(key));
          } else if (type == ReadableType.Boolean) {
            globalContextData.putBoolean(key, globalContextJsonMap.getBoolean(key));
          }
        }
      } else {
        GainsightPX.with(getReactApplicationContext()).setGlobalContext(null);
      }
       promise.resolve(true);
    } catch (Throwable tr) {
       promise.reject(tr);
      Log.e(TAG, "globalContext: ", tr);
    }
  }

  @ReactMethod
//  hasKey(key: String): Promise<void>
  public void hasGlobalContextKey(String key, Promise promise) {
    try {
      boolean result = false;
      if (key != null) {
        if (GainsightPX.with(getReactApplicationContext()).getGlobalContext() != null) {
          result = GainsightPX.with(getReactApplicationContext()).getGlobalContext().hasKey(key);
        }
      }
      promise.resolve(result);
    } catch (Throwable tr) {
      Log.e(TAG, "globalContext: ", tr);
      promise.reject(tr);
    }
  }

  @ReactMethod
//  removeKey(key: String): Promise<void>
  public void removeGlobalContextKeys(ReadableArray keys, Promise promise) {
    try {
      if (keys != null) {
        GlobalContextData globalContextData = GainsightPX.with(getReactApplicationContext()).getGlobalContext();
        if (globalContextData != null) {
          for (int i = 0; i < keys.size(); i++) {
            globalContextData.removeKey(keys.getString(i));
          }
        }
      }
       promise.resolve(true);
    } catch (Throwable tr) {
       promise.reject(tr);
      Log.e(TAG, "globalContext: ", tr);
    }
  }

  @ReactMethod
//  enable(): Promise<void>
  public void enable(Promise promise) {
    try {
      GainsightPX.with(getReactApplicationContext()).setEnable(true);
       promise.resolve(true);
    } catch (Throwable tr) {
       promise.reject(tr);
      Log.e(TAG, "enable: ", tr);
    }
  }

  @ReactMethod
//  disable(): Promise<void>
  public void disable(Promise promise) {
    try {
      GainsightPX.with(getReactApplicationContext()).setEnable(false);
       promise.resolve(true);
    } catch (Throwable tr) {
       promise.reject(tr);
      Log.e(TAG, "disable: ", tr);
    }
  }

  @ReactMethod
//  reset(): Promise<void>
  public void reset(Promise promise) {
    try {
      GainsightPX.with(getReactApplicationContext()).reset();
       promise.resolve(true);
    } catch (Throwable tr) {
       promise.reject(tr);
      Log.e(TAG, "reset: ", tr);
    }
  }

  private User buildUser(ReadableMap user) {
    User gainsightUser = null;
    if (null != user) {
      gainsightUser = new User(user.getString("ide"));
      HashMap<String, Object> customAttributes = new HashMap<>();
      for (ReadableMapKeySetIterator iterator = user.keySetIterator(); iterator.hasNextKey(); ) {
        String key = iterator.nextKey();
        switch (key) {
          case "ide":
            break;
          case "userHash":
            gainsightUser.putIdentifyIdHash(user.getString(key));
            break;
          case "email":
          case "usem":
            gainsightUser.putEmail(user.getString(key));
            break;
          case "gender":
            gainsightUser.putGender(User.Gender.getGender(user.getString(key).toUpperCase()));
            break;
          case "lastName":
            gainsightUser.putLastName(user.getString(key));
            break;
          case "firstName":
            gainsightUser.putFirstName(user.getString(key));
            break;
          case "signUpDate": {
            ReadableType type = user.getType(key);
            if (type == ReadableType.String) {
              gainsightUser.putSignUpDate(user.getString(key));
            } else if (type == ReadableType.Number) {
              gainsightUser.putSignUpDate((long)user.getDouble(key));
            }
          }
          break;
          case "title":
            gainsightUser.putTitle(user.getString(key));
            break;
          case "role":
            gainsightUser.putRole(user.getString(key));
            break;
          case "subscriptionId":
            gainsightUser.putSubscriptionId(user.getString(key));
            break;
          case "phone":
            gainsightUser.putPhone(user.getString(key));
            break;
          case "countryCode":
            gainsightUser.putCountryCode(user.getString(key));
            break;
          case "countryName":
            gainsightUser.putCountryName(user.getString(key));
            break;
          case "stateCode":
            gainsightUser.putStateCode(user.getString(key));
            break;
          case "stateName":
            gainsightUser.putStateName(user.getString(key));
            break;
          case "city":
            gainsightUser.putCity(user.getString(key));
            break;
          case "street":
            gainsightUser.putStreet(user.getString(key));
            break;
          case "continent":
            gainsightUser.putContinentCode(user.getString(key));
            break;
          case "postalCode":
            gainsightUser.putPostalCode(user.getString(key));
            break;
          case "regionName":
            gainsightUser.putRegionName(user.getString(key));
            break;
          case "timeZone":
            gainsightUser.putTimeZone(user.getString(key));
            break;
          case "latitude":
            gainsightUser.putLatitude(user.getDouble(key));
            break;
          case "longitude":
            gainsightUser.putLongitude(user.getDouble(key));
            break;
          case "organization":
            gainsightUser.putOrganizationName(user.getString(key));
            break;
          case "organizationEmployees":
            gainsightUser.putOrganizationEmployees(user.getString(key));
            break;
          case "organizationRevenue":
            gainsightUser.putOrganizationRevenue(user.getString(key));
            break;
          case "organizationIndustry":
            gainsightUser.putOrganizationIndustry(user.getString(key));
            break;
          case "organizationSicCode":
            gainsightUser.putOrganizationSicCode(user.getString(key));
            break;
          case "organizationDuns":
            gainsightUser.putOrganizationDuns(user.getInt(key));
            break;
          case "accountId":
            gainsightUser.putAccountId(user.getString(key));
            break;
          case "firstVisitDate": {
            ReadableType type = user.getType(key);
            if (type == ReadableType.String) {
              gainsightUser.putFirstVisitDate(user.getString(key));
            } else if (type == ReadableType.Number) {
              gainsightUser.putFirstVisitDate((long)user.getDouble(key));
            }
          }
          break;
          case "score":
            gainsightUser.putScore(user.getInt(key));
            break;
          case "sfdcContactId":
            gainsightUser.putSfdcContactId(user.getString(key));
            break;
          case "customAttributes":
            HashMap<String, Object> data = convertToMap(user.getMap(key));
            customAttributes.putAll(data);
            break;
          default:
            copyReadableMapValue(user, customAttributes, key);
        }
      }
      if (customAttributes.size() > 0) {
        gainsightUser.putCustomAttributes(customAttributes);
      }
    }
    return gainsightUser;
  }

  private Account buildAccount(ReadableMap account) {
    Account gainsightAccount = null;
    if (null != account) {
      gainsightAccount = new Account(account.getString("id"));
      HashMap<String, Object> customAttributes = new HashMap<>();
      for (ReadableMapKeySetIterator iterator = account.keySetIterator(); iterator.hasNextKey(); ) {
        String key = iterator.nextKey();
        switch (key) {
          case "id":
            break;
          case "name":
            gainsightAccount.putName(account.getString(key));
            break;
          case "trackedSubscriptionId":
            gainsightAccount.putSubscriptionId(account.getString(key));
            break;
          case "industry":
            gainsightAccount.putIndustry(account.getString(key));
            break;
          case "numberOfEmployees":
            gainsightAccount.putEmployees(account.getInt(key));
            break;
          case "sicCode":
            gainsightAccount.putSicCode(account.getString(key));
            break;
          case "website":
            gainsightAccount.putWebsite(account.getString(key));
            break;
          case "naicsCode":
            gainsightAccount.putNaicsCode(account.getString(key));
            break;
          case "plan":
            gainsightAccount.putPlan(account.getString(key));
            break;
          case "countryCode":
            gainsightAccount.putCountryCode(account.getString(key));
            break;
          case "countryName":
            gainsightAccount.putCountryName(account.getString(key));
            break;
          case "stateCode":
            gainsightAccount.putStateCode(account.getString(key));
            break;
          case "stateName":
            gainsightAccount.putStateName(account.getString(key));
            break;
          case "city":
            gainsightAccount.putCity(account.getString(key));
            break;
          case "street":
            gainsightAccount.putStreet(account.getString(key));
            break;
          case "continent":
            gainsightAccount.putContinent(account.getString(key));
            break;
          case "postalCode":
            gainsightAccount.putPostalCode(account.getString(key));
            break;
          case "regionName":
            gainsightAccount.putRegionName(account.getString(key));
            break;
          case "timeZone":
            gainsightAccount.putTimeZone(account.getString(key));
            break;
          case "latitude":
            gainsightAccount.putLatitude(account.getDouble(key));
            break;
          case "longitude":
            gainsightAccount.putLongitude(account.getDouble(key));
            break;
          case "sfdcId":
            gainsightAccount.putSfdcContactId(account.getString(key));
            break;
          case "customAttributes":
            HashMap<String, Object> data = convertToMap(account.getMap(key));
            customAttributes.putAll(data);
            break;
          default:
            copyReadableMapValue(account, customAttributes, key);
        }
      }
      if (customAttributes.size() > 0) {
        gainsightAccount.putCustomAttributes(customAttributes);
      }
    }
    return gainsightAccount;
  }

  private HashMap<String, Object> convertToMap(ReadableMap readableMap) {
    HashMap<String, Object> values = null;
    if (null != readableMap) {
      values = new HashMap<>();
      ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
      while (iterator.hasNextKey()) {
        copyReadableMapValue(readableMap, values, iterator.nextKey());
      }
    }
    return values;
  }

  private void copyReadableMapValue(ReadableMap source, Map<String, Object> target, String key) {
    if ((null != source) &&
            (null != target) &&
            (null != key) &&
            (source.hasKey(key))) {
      switch (source.getType(key)) {
        case Boolean:
          target.put(key, source.getBoolean(key));
          break;
        case Number:
          try {
            target.put(key, source.getDouble(key));
          } catch (Throwable throwable) {
            target.put(key, source.getInt(key));
          }
          break;
        case String:
          target.put(key, source.getString(key));
          break;
        case Array:
          List<Object> list = convertToArray(source.getArray(key));
          if (null != list) {
            target.put(key, list);
          }
          break;
        case Map:
          HashMap<String, Object> data = convertToMap(source.getMap(key));
          if (null != data) {
            target.put(key, data);
          }
          break;
        case Null:
        default:
          break;
      }
    }
  }

  private List<Object> convertToArray(ReadableArray source) {
    List<Object> values = null;
    if (null != source) {
      values = new ArrayList<>();
      for (int i = 0; i < source.size(); i++) {
        switch (source.getType(i)) {
          case Boolean:
            values.add(source.getBoolean(i));
            break;
          case Number:
            try {
              values.add(source.getDouble(i));
            } catch (Throwable throwable) {
              values.add(source.getInt(i));
            }
            break;
          case String:
            values.add(source.getString(i));
            break;
          case Array:
            List<Object> list = convertToArray(source.getArray(i));
            if (null != list) {
              values.add(list);
            }
            break;
          case Map:
            HashMap<String, Object> data = convertToMap(source.getMap(i));
            if (null != data) {
              values.add(data);
            }
            break;
          case Null:
          default:
            break;
        }
      }
    }
    return values;
  }

  private WritableMap onRsolve(String methodName) {
    WritableMap args = Arguments.createMap();
    args.putString("status", "SUCCESS");
    args.putString("methodName", methodName);
    return args;
  }

  private String onReject(String methodName, ValueMap params, String exceptionMessage) {
    WritableMap args = Arguments.createMap();
    args.putString("status", "FAILURE");
    args.putMap("params", castToWritableMap(params));
    args.putString("methodName", methodName);
    args.putString("exceptionMessage", exceptionMessage);
    return args.toString();
  }

  private WritableMap castToWritableMap(Map<String, Object> map) {
    WritableMap writableMap = Arguments.createMap();
    Iterator iterator = map.entrySet().iterator();

    while (iterator.hasNext()) {
      Map.Entry pair = (Map.Entry)iterator.next();
      Object value = pair.getValue();

      if (value == null) {
        writableMap.putNull((String) pair.getKey());
      } else if (value instanceof Boolean) {
        writableMap.putBoolean((String) pair.getKey(), (Boolean) value);
      } else if (value instanceof Double) {
        writableMap.putDouble((String) pair.getKey(), (Double) value);
      } else if (value instanceof Integer) {
        writableMap.putInt((String) pair.getKey(), (Integer) value);
      } else if (value instanceof String) {
        writableMap.putString((String) pair.getKey(), (String) value);
      } else if (value instanceof Map) {
        writableMap.putMap((String) pair.getKey(), castToWritableMap((Map<String, Object>) value));
      } else if (value.getClass() != null && value.getClass().isArray()) {
        writableMap.putArray((String) pair.getKey(), castToWritableArray((Object[]) value));
      }

      iterator.remove();
    }

    return writableMap;
  }

  private WritableArray castToWritableArray(Object[] array) {
    WritableArray writableArray = Arguments.createArray();

    for (Object value : array) {
      if (value == null) {
        writableArray.pushNull();
      }
      if (value instanceof Boolean) {
        writableArray.pushBoolean((Boolean) value);
      }
      if (value instanceof Double) {
        writableArray.pushDouble((Double) value);
      }
      if (value instanceof Integer) {
        writableArray.pushInt((Integer) value);
      }
      if (value instanceof String) {
        writableArray.pushString((String) value);
      }
      if (value instanceof Map) {
        writableArray.pushMap(castToWritableMap((Map<String, Object>) value));
      }
      if (value.getClass().isArray()) {
        writableArray.pushArray(castToWritableArray((Object[]) value));
      }
    }

    return writableArray;
  }
}
