@import React;

@interface RNImmediatePhoneCall : NSObject <RCTBridgeModule>
@end

@implementation RNImmediatePhoneCall

RCT_EXPORT_MODULE();

+ (BOOL)requiresMainQueueSetup {
  return true;
}

RCT_EXPORT_METHOD(immediatePhoneCall:(NSString *)number resolve:(RCTPromiseResolveBlock _Nonnull)resolve rejecter:(RCTPromiseRejectBlock _Nonnull)reject)
{
    UIApplication *application = RCTSharedApplication();
    if (application == nil) {
      reject(@"OPEN_URL_NOT_AVAILABLE_IN_EXTENSION", @"does not support open url from app extension", nil);
      return;
    }
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"tel:%@", number]];
    if ([number isEqualToString:@""] || url == nil || [application canOpenURL:url] == false) {
      reject(@"OPEN_URL_NOT_AVAILABLE", @"device does not support calling", nil);
      return;
    }
    
    [application openURL: url options: @{} completionHandler:^(BOOL success) {
      if(success) {
        resolve(@"Success");
      } else {
        reject(@"OPEN_URL_FAILED", @"failed to open url", nil);
      }
    }];
};

RCT_EXPORT_METHOD(canCall:(RCTPromiseResolveBlock _Nonnull)resolve rejecter:(RCTPromiseRejectBlock _Nonnull)reject)
{
    UIApplication *application = RCTSharedApplication();
    if (application == nil) {
      reject(@"OPEN_URL_NOT_AVAILABLE_IN_EXTENSION", @"does not support open url from app extension", nil);
      return;
    }
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"tel:%@", @"//"]];
    if ([application canOpenURL:url] == false) {
      resolve(@NO);
      return;
    }
    
    resolve(@YES);
};

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(canCall)
{
  __block NSNumber *canCall = false;
  
  dispatch_sync(dispatch_get_main_queue(), ^{
    UIApplication *application = RCTSharedApplication();
    if (application == nil) {
      return ;
    }
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"tel:%@", @"//"]];
    if ([application canOpenURL:url] == false) {
      return ;
    }
    
    canCall = @YES;
    return ;
  });
  
  return canCall;
};

RCT_EXPORT_METHOD(canCallAsync:(RCTPromiseResolveBlock _Nonnull)resolve rejecter:(RCTPromiseRejectBlock _Nonnull)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    UIApplication *application = RCTSharedApplication();
    if (application == nil) {
      reject(@"OPEN_URL_NOT_AVAILABLE_IN_EXTENSION", @"does not support open url from app extension", nil);
      return;
    }
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"tel:%@", @"//"]];
    if ([application canOpenURL:url] == false) {
      resolve(@NO);
      return;
    }
    
    resolve(@YES);
  });
};

@end
