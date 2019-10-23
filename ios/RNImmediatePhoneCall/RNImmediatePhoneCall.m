@import React;

@interface RNImmediatePhoneCall : NSObject <RCTBridgeModule>
@end

@implementation RNImmediatePhoneCall

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(immediatePhoneCall:(NSString *)number resolve:(RCTPromiseResolveBlock _Nonnull)resolve rejecter:(RCTPromiseRejectBlock _Nonnull)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
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
  });
};

@end
