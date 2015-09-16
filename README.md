# react-native-battery

A React Native module that returns the battery level/status of a device

### Add it to your iOS project

1. Run `npm install react-native-battery --save`
2. Open your project in XCode, right click on `Libraries` and click `Add
   Files to "Your Project Name"` [(Screenshot)](http://url.brentvatne.ca/jQp8) then [(Screenshot)](http://url.brentvatne.ca/1gqUD).
3. Add `libRCTBattery.a` to `Build Phases -> Link Binary With Libraries`
   [(Screenshot)](http://url.brentvatne.ca/17Xfe).
4. Whenever you want to use it within React code now you can: `var BatteryManager = require('NativeModules').BatteryManager;`


## Example
```javascript
'use strict';
var React = require('react-native');
var BatteryManager = require('NativeModules').BatteryManager;
var {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  DeviceEventEmitter,
} = React;

var RCTBattery = React.createClass({

  getInitialState: function() {
    return {batteryLevel: null, charging:false};
  },

  onBatteryStatus: function(info){
    this.setState({batteryLevel: info.level});
    this.setState({charging: info.isPlugged});
  },

  componentDidMount: function(){
    BatteryManager.updateBatteryLevel(function(info){
      this._subscription = DeviceEventEmitter.addListener('BatteryStatus', this.onBatteryStatus);
      this.setState({batteryLevel: info.level});
      this.setState({charging: info.isPlugged});
    }.bind(this));
  },

  componentWillUnmount: function(){
    this._subscription.remove();
  },
  
  render: function() {
    var chargingText;
    if(this.state.charging){
      chargingText =<Text style={styles.instructions}>Charging </Text>;
    } else {
      chargingText =<Text style={styles.instructions}>Not Charging </Text>;
    }
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Battery Level {this.state.batteryLevel}
        </Text>
        {chargingText}
      </View>
    );
  }
});

var styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('RCTBattery', () => RCTBattery);
  
```

