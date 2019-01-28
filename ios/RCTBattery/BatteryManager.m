//
//  BatteryManager.m
//  RCTBattery
//
//  Created by Olajide Ogundipe Jr on 9/15/15.
//

#import <React/RCTBridge.h>
#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>

#import "BatteryManager.h"

@implementation BatteryManager

@synthesize bridge = _bridge;
@synthesize isPlugged;

RCT_EXPORT_MODULE();

- (instancetype)init
{
    if ((self = [super init])) {
        [[UIDevice currentDevice] setBatteryMonitoringEnabled:YES];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(batteryLevelChanged:)
                                                     name:UIDeviceBatteryLevelDidChangeNotification
                                                   object: nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(batteryLevelChanged:)
                                                     name:UIDeviceBatteryStateDidChangeNotification
                                                   object: nil];
    }
    return self;
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

RCT_EXPORT_METHOD(updateBatteryLevel:(RCTResponseSenderBlock)callback)
{
    callback(@[[self getBatteryStatus]]);
}

-(NSDictionary*)getBatteryStatus
{
    
    float batteryLevel = [UIDevice currentDevice].batteryLevel;
    UIDeviceBatteryState batteryState = [UIDevice currentDevice].batteryState;
    
    isPlugged = FALSE;
    
    NSObject* currentLevel = nil;
    
    if (batteryState == UIDeviceBatteryStateCharging) {
        currentLevel = [NSNumber numberWithFloat:(batteryLevel * 100)];
        isPlugged = TRUE;
    }
    
    if(batteryState == UIDeviceBatteryStateFull){
        currentLevel = [NSNumber numberWithFloat:(batteryLevel * 100)];
        isPlugged = TRUE;
    }
    
    if(batteryState == UIDeviceBatteryStateUnplugged){
        currentLevel = [NSNumber numberWithFloat:(batteryLevel * 100)];
    }
    
    if(batteryState == UIDeviceBatteryStateUnknown || batteryState == -1.0){
        currentLevel = [NSNull null];
    } else {
        currentLevel = [NSNumber numberWithFloat:(batteryLevel * 100)];
    }
    
    NSMutableDictionary* batteryData = [NSMutableDictionary dictionaryWithCapacity:2];
    [batteryData setObject:[NSNumber numberWithBool:isPlugged] forKey:@"isPlugged"];
    [batteryData setObject:currentLevel forKey:@"level"];
    return batteryData;
    
}

-(void)batteryLevelChanged:(NSNotification*)notification {
    
    NSDictionary* batteryData = [self getBatteryStatus];
    [self.bridge.eventDispatcher sendDeviceEventWithName:@"BatteryStatus" body:batteryData];
    
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
