
//
//  BatteryManager.h
//  RCTBattery
//
//  Created by Olajide Ogundipe Jr on 9/15/15.


#import "RCTBridgeModule.h"


@interface BatteryManager : NSObject <RCTBridgeModule>
@property (nonatomic) bool isPlugged;

@end
