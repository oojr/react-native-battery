# react-native-battery

A React Native module that returns the battery level/status of a device

### Add it to your iOS project

1. Run `npm install react-native-battery --save`
2. `react-native link`


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
