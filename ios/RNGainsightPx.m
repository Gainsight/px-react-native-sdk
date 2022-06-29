
#import "RNGainsightPx.h"
#import <PXKit/PXKit-Swift.h>
#import <UIKit/UIKit.h>

NSString *const EngagementId = @"engagementId";
NSString *const EngagementName = @"engagementName";
NSString *const ActionText = @"actionText";
NSString *const ActionType = @"actionType";
NSString *const ActionData = @"actionData";
NSString *const Scope = @"scope";
NSString *const Params = @"params";
NSString *const EngagementCallBack = @"engagementCallBack";

typedef enum {
  Initialize = 0,
  Custom,
  CustomEventWithProperties,
  ScreenWithTitle,
  Screen,
  ScreenWithEvent,
  IdentifyUserId,
  IdentifyUser,
  Identify,
  SetGlobalContext,
  RemoveGlobalContextKeys,
  Flush,
  EnterEditing,
  ExitEditing,
  Enable,
  Disable
} GainsightPXMethod;

 NSString *gainsightPXMethodName(GainsightPXMethod input) {
    NSArray *names = @[
    @"initialize",
    @"custom",
    @"customEventWithProperties",
    @"screenWithTitle",
    @"screen",
    @"screenEvent",
    @"identifyUserId",
    @"identifyUser",
    @"identify",
    @"setGlobalContext",
    @"removeGlobalContextKeys",
    @"flush",
    @"enterEditing",
    @"exitEditing",
    @"enable",
    @"disable"
    ];
    return (NSString *)[names objectAtIndex:input];
}

@implementation RNGainsightPx
{
  bool hasListeners;
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

// Will be called when this module's first listener is added.
-(void)startObserving {
    hasListeners = YES;
}

// Will be called when this module's last listener is removed, or on dealloc.
-(void)stopObserving {
    hasListeners = NO;
}

- (void)sendEvent: (PXEngagementCallBackModel *_Nonnull)params {
  if (hasListeners) {
    NSDictionary *paramsDict = @{EngagementId: params.engagementId != nil ? params.engagementId : @"",
                                 EngagementName: params.engagementName != nil ? params.engagementName : @"",
                                 ActionText: params.actionText != nil ? params.actionText : @"",
                                 ActionType: params.actionsType != nil ? params.actionsType : @"",
                                 ActionData: params.actionData != nil ? params.actionData : @"",
                                 Scope: params.scope != nil ? @{@"screenName": params.scope.screenName,
                                                                @"screenClass": params.scope.screenClass
                                 } : @{},
                                 Params: params.params
    };
    [self sendEventWithName:EngagementCallBack body:paramsDict];
  }
}
  
- (NSArray<NSString *> *)supportedEvents {
    return @[EngagementCallBack];
}

RCT_EXPORT_MODULE()

//initialize(params: JsonMap): Promise<void>
RCT_EXPORT_METHOD(initialize:(NSDictionary *_Nonnull)params
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject)
{
  NSString *apiKey = [params[@"apiKey"] stringByAppendingString:@"&RN"];
  PXAnalyticsConfigurations *configurations = [[PXAnalyticsConfigurations alloc] initWithApiKey:apiKey];
  
  [configurations setEnabled:[[params objectForKey:@"enable"] boolValue]];
  [configurations setFlushInterval:[[params objectForKey:@"flushInterval"] doubleValue]];
  [configurations setFlushQueueSize:[[params objectForKey:@"flushQueueSize"] intValue]];
  [configurations setTrackApplicationLifecycleEvents:[[params objectForKey:@"trackApplicationLifeCycleEvents"] boolValue]];
  [configurations setShouldTrackTapEvents: [[params objectForKey:@"shouldTrackTapEvents"] boolValue]];
  [configurations setReportTrackingIssues: [[params objectForKey:@"reportTrackingIssues"] boolValue]];
  [configurations setRecordScreenViews:false];
  [configurations setMaxQueueSize: [[params objectForKey:@"maxQueueSize"] intValue]];
  
  NSString *proxy = params[@"proxy"];
  if(proxy != nil) {
    PXConnection *connection = [[PXConnection alloc] initWithCustomHost:proxy];
    [configurations setConnection: connection];
  }else {
    NSString *host = params[@"host"];
    if (host != nil) {
      if ([host isEqualToString:@"us"]) {
        [configurations setConnection: [[PXConnection alloc] initWithHost:PXHostUs]];
      } else if ([host isEqualToString:@"eu"]){
        [configurations setConnection: [[PXConnection alloc] initWithHost:PXHostEu]];
      } else if ([host isEqualToString:@"us2"]){
        [configurations setConnection: [[PXConnection alloc] initWithHost:PXHostUs2]];
      }
    }
  }
  
  [GainsightPX debugLogsWithEnable:[[params objectForKey:@"enableLogs"] boolValue]];
  configurations.currentWindow = UIApplication.sharedApplication.windows.firstObject;
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  
  [[GainsightPX shared] initialiseWithConfigurations:configurations completionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nullable error) {
    nativeError = error;
    props = properties;
  } callback:^BOOL(PXEngagementCallBackModel * _Nullable engagementCallBack, NSError * _Nullable error) {
    if (engagementCallBack != Nil) {
      [self sendEvent: engagementCallBack];
    }
    return true;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(Initialize)
            properties:props
                 error:nativeError];
}

//custom(event: string): Promise<void>
RCT_EXPORT_METHOD(custom:(NSString * _Nonnull)name
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  [[GainsightPX shared] customWithEvent:name errorCompletionBlock:^ (NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(Custom)
            properties:props
                 error:nativeError];
}

//customEventWithProperties(event: string, properties?: JsonMap): Promise<void>
RCT_EXPORT_METHOD(customEventWithProperties:(NSString * _Nonnull)name
                  properties:(NSDictionary *)properties
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  [[GainsightPX shared] customWithEvent:name properties:properties errorCompletionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(CustomEventWithProperties)
            properties:props
                 error:nativeError];
}

//screenWithTitle(name: string): Promise<void>
RCT_EXPORT_METHOD(screenWithTitle:(NSString *_Nonnull)title
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  [[GainsightPX shared] screenWithTitle:title errorCompletionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(ScreenWithTitle)
            properties:props
                 error:nativeError];
}


//screen(name: string, properties?: JsonMap): Promise<void>
RCT_EXPORT_METHOD(screen:(NSString *_Nonnull)name
                  properties:(NSDictionary *)properties
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  [[GainsightPX shared] screenWithTitle:name properties:properties errorCompletionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(Screen)
            properties:props
                 error:nativeError];
}

//screenEvent(name: string, className: string, properties?: JsonMap): Promise<void>
RCT_EXPORT_METHOD(screenEvent:(NSString *_Nonnull)name
                  className:(NSString *)className
                  properties:(NSDictionary *)properties
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  ScreenEvent *event = [[ScreenEvent alloc] initWithScreenName:name screenClass:className];
  [[GainsightPX shared] screenWithScreen:event properties:properties errorCompletionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(ScreenWithEvent)
            properties:props
                 error:nativeError];
}

//identifyUserId(userId: String): Promise<void>
RCT_EXPORT_METHOD(identifyUserId:(NSString *_Nonnull)userId
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  [[GainsightPX shared] identifyWithUserId:userId errorCompletionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(IdentifyUserId)
            properties:props
                 error:nativeError];
}

//identifyUser(user: JsonMap): Promise<void>
RCT_EXPORT_METHOD(identifyUser:(NSDictionary *_Nonnull)userParams
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  PXUser *user = [self fetchUserForParams:userParams];
  [[GainsightPX shared] identifyWithUser:user errorCompletionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(IdentifyUser)
            properties:props
                 error:nativeError];
}

//identify(user: JsonMap, account?: JsonMap): Promise<void>
RCT_EXPORT_METHOD(identify:(NSDictionary *_Nonnull)userParams
                  account:(NSDictionary *)accountParams
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  PXUser *user = [self fetchUserForParams:userParams];
  PXAccount *account = [self fetchAccountForParams:accountParams];
  [[GainsightPX shared] identifyWithUser:user account:account errorCompletionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(Identify)
            properties:props
                 error:nativeError];
}

//setGlobalContext(map?: GlobalContextJsonMap): Promise<void>;
RCT_EXPORT_METHOD(setGlobalContext: (NSDictionary *)map
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject){
  GainsightPX *instance = [self sharedGainsight];
  if (map == nil) {
    [instance globalContextWithContext: nil];
    [self handleCallBack:reject
                 resolve:resolve
            functionName:gainsightPXMethodName(SetGlobalContext)
              properties:nil
                   error:nil];
    return;
  }
  __block PXGlobalContext *context = [[self sharedGainsight] globalContext];
  
  if (context == nil) {
    context = [[PXGlobalContext alloc] init];
    [instance globalContextWithContext: context];
  }
  [map enumerateKeysAndObjectsUsingBlock:^(id key, id value, BOOL* stop) {
    if ([value isKindOfClass: [NSString class]]) {
      NSString *strValue = (NSString *)value;
      context = [context setStringWithKey: key value: strValue];
    } else if ([value isKindOfClass: [NSNumber class]]) {
      NSNumber *numberValue = (NSNumber *)value;
      BOOL isBoolean = [self isBoolNumber:numberValue];
      if (isBoolean) {
        context = [context setBooleanWithKey:key value:[numberValue boolValue]];
      } else {
        context = [context setDoubleWithKey: key value: [numberValue doubleValue]];
      }
    }
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(SetGlobalContext)
            properties:nil
                 error:nil];
}

//hasGlobalContextKey(key: string): Promise<boolean>;
RCT_EXPORT_METHOD(hasGlobalContextKey: (NSString *)key
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject){
  BOOL keyExists = NO;
  if ([[self sharedGainsight] globalContext] == nil) {
    NSLog(@"No GlobalContext exists");
    resolve([NSNumber numberWithBool:keyExists]);
  }
  keyExists = [[[self sharedGainsight] globalContext] hasKeyWithKey: key];
  NSNumber *boolNumber = [NSNumber numberWithBool:keyExists];
  resolve(boolNumber);
}

//removeGlobalContextKeys(key: string): Promise<void>;
RCT_EXPORT_METHOD(removeGlobalContextKeys: (NSArray<NSString *> * _Nonnull)keys
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject){
  if ([[self sharedGainsight] globalContext] == nil) {
    [self handleCallBack:reject
                 resolve:resolve
            functionName:gainsightPXMethodName(RemoveGlobalContextKeys)
              properties:nil error:nil];
    return;
  }
  [[[self sharedGainsight] globalContext] removeKeysWithKeys: keys];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(RemoveGlobalContextKeys)
            properties:nil error:nil];
}

//flush(): Promise<void>
RCT_EXPORT_METHOD(flush
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
  __block NSError *nativeError;
  __block NSDictionary<NSString *,id> *props;
  [[GainsightPX shared] flushWithErrorCompletionBlock:^(NSString * _Nonnull functionName, NSDictionary<NSString *,id> * _Nullable properties, NSError * _Nonnull error) {
    nativeError = error;
    props = properties;
  }];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(Flush)
            properties:props
                 error:nativeError];
}

//enable(): Promise<void>
RCT_EXPORT_METHOD(enable
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
  [GainsightPX enable];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(Enable)
            properties:nil error:nil];
}

//disable(): Promise<void>;
RCT_EXPORT_METHOD(disable
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
  [GainsightPX disable];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(Disable)
            properties:nil error:nil];
}

//enterEditing(url: string): Promise<void>;
RCT_EXPORT_METHOD(enterEditing: (NSString * _Nonnull)url
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
  NSURL *editorURL = [NSURL URLWithString:url];
  NSError *nativeError;
  if (editorURL != NULL) {
    [[GainsightPX shared] enterEditingModeWithUrl:editorURL];
  } else {
    nativeError = [NSError errorWithDomain:@"com.gpx.error" code:1 userInfo:@{NSLocalizedDescriptionKey: @"Invalid url"}];
  }
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(EnterEditing)
            properties:nil error:nativeError];
}

//exitEditing(): Promise<void>;
RCT_EXPORT_METHOD(exitEditing
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
  [[GainsightPX shared] exitEditingMode];
  [self handleCallBack:reject
               resolve:resolve
          functionName:gainsightPXMethodName(ExitEditing)
            properties:nil error:nil];
}

//MARK: - Private Methods

- (PXUser *)fetchUserForParams: (NSDictionary *)userParams {
  
  PXUser *user = [[PXUser alloc] initWithUserId:userParams[@"ide"]];
  NSMutableDictionary *userCustomAttribs = [[NSMutableDictionary alloc] init];
  
  //User
  for (NSString *key in [userParams allKeys]) {
    id object = userParams[key];
    if ([user respondsToSelector:NSSelectorFromString(key)]){
      [user setValue:object forKey:key];
    }else {
      [userCustomAttribs setObject:object forKey:key];
    }
  }
  
  if ([[userCustomAttribs allKeys] count] != 0){
    [user setCustomAttributes:userCustomAttribs];
  }
  return  user;
}

- (PXAccount *)fetchAccountForParams: (NSDictionary *)accountParams {
  
  NSMutableDictionary *accountCustomAttribs = [[NSMutableDictionary alloc] init];
  PXAccount *account = [[PXAccount alloc] initWithId:accountParams[@"id"]];
  
  for (NSString *key in [accountParams allKeys]) {
    
    if ([key isEqual: @"id"]) {continue;}
    
    id object = accountParams[key];
    if ([account respondsToSelector:NSSelectorFromString(key)]){
      [account setValue:object forKey:key];
    }else {
      [accountCustomAttribs setObject:object forKey:key];
    }
  }
  
  if ([[accountCustomAttribs allKeys] count] != 0) {
    [account setCustomAttributes:accountCustomAttribs];
  }
  return account;
  
}


- (GainsightPX *)sharedGainsight {
  return  [GainsightPX shared];
}

- (BOOL) isBoolNumber:(NSNumber *)num
{
  CFTypeID boolID = CFBooleanGetTypeID(); // the type ID of CFBoolean
  CFTypeID numID = CFGetTypeID((__bridge CFTypeRef)(num)); // the type ID of num
  return numID == boolID;
}

- (void)handleCallBack: (RCTPromiseRejectBlock) reject
               resolve: (RCTPromiseResolveBlock) resolve
          functionName: (NSString * _Nonnull) functionName
            properties: (NSDictionary<NSString *,id> * _Nullable) properties
                 error: (NSError *) error {
  NSMutableDictionary *userInfo = [[NSMutableDictionary alloc]
                                   initWithDictionary: @{@"methodName": functionName}];
  if (error != nil) {
    [userInfo setValue:@0 forKey:@"status"];
    [userInfo setValue:error.localizedDescription forKey:@"exceptionMessage"];
    [userInfo setValue:error.localizedDescription forKey:NSLocalizedDescriptionKey];
    if (properties != nil) {
      userInfo[@"params"] = properties;
    }
    NSError *gpxError = [[NSError alloc] initWithDomain:@"com.gpx.error" code:1 userInfo:userInfo];
    reject(@"0", [error localizedDescription], gpxError);
  } else {
    [userInfo setValue:@1 forKey:@"status"];
    resolve(userInfo);
  }
}

@end
