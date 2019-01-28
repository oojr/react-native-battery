# react-native-battery

A cross-platform React Native module that returns the battery level/status of a device. Supports iOS and Android.

## Package Installation
`npm install react-native-battery --save`

### iOS automatic setup
*   `react-native link react-native-battery`


### Android setup
*   `react-native link react-native-battery` may work, but it sometimes munges files. If automatic installation fails, use the following manual steps.
*   Add to `MainApplication.java`:
```
import com.rctbattery.BatteryManagerPackage;
// ...

@Override
protected List<ReactPackage> getPackages() {
  return Arrays.<ReactPackage>asList(
      new MainReactPackage(),
        new BatteryManagerPackage(),
        // ...
  );
}
```
*   Add to `android/settings.gradle`:
```
include ':react-native-battery'
project(':react-native-battery').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-battery/android')
//...
```
*   Add to `android/app/build.gradle`:
```
dependencies {
  compile project(':react-native-battery')
  //...
}
```


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
