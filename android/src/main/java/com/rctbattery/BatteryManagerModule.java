// Android port based in part on
// https://stackoverflow.com/questions/44920299/react-native-get-battery-status-level
// https://github.com/robinpowered/react-native-device-battery
// http://developer.android.com/training/monitoring-device-state/battery-monitoring.html

package com.rctbattery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.BatteryManager;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.bridge.LifecycleEventListener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class BatteryManagerModule extends ReactContextBaseJavaModule {

  public static final String EVENT_NAME = "BatteryStatus";
  public static final String IS_CHARGING_KEY = "isPlugged";
  public static final String BATTERY_LEVEL_KEY = "level";

  private Intent batteryStatus = null;
  private @Nullable PowerConnectionReceiver batteryStateReceiver;

  public BatteryManagerModule(ReactApplicationContext reactApplicationContext) {
    super(reactApplicationContext);
  }

  @Override
  public String getName() {
    return "BatteryManager";
  }

  private WritableNativeMap getJSMap (Intent intent) {
    int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    WritableNativeMap params = new WritableNativeMap();
    params.putBoolean(IS_CHARGING_KEY, isCharging);
    params.putInt(BATTERY_LEVEL_KEY, batteryLevel);
    return params;
  }

  public void notifyBatteryStateChanged(Intent intent) {
    batteryStatus = intent;
    // only emit an event if the Catalyst instance is available
    if (getReactApplicationContext().hasActiveCatalystInstance()) {
      WritableNativeMap params = getJSMap(intent);
      try {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(EVENT_NAME, params);
      } catch (Exception e) {
        Log.e(getName(), "notifyBatteryStateChanged called before bundle loaded");
      }
    }
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("BATTERY_CHANGE_EVENT", EVENT_NAME);
    return constants;
  }

  @ReactMethod
  public void updateBatteryLevel(Callback cb) {
    WritableNativeMap params;
    if (batteryStatus != null) {
      params = getJSMap(batteryStatus);
    } else {
      params = new WritableNativeMap();
      params.putString("error", "Battery manager is not active");
    }
    cb.invoke(params);
  }

  private void maybeRegisterReceiver() {
    if (batteryStateReceiver != null) {
      return;
    }
    batteryStateReceiver = new PowerConnectionReceiver();
    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    batteryStatus = getReactApplicationContext().registerReceiver(batteryStateReceiver, filter);
  }

  private void maybeUnregisterReceiver() {
    if (batteryStateReceiver == null) {
      return;
    }
    getReactApplicationContext().unregisterReceiver(batteryStateReceiver);
    batteryStateReceiver = null;
    batteryStatus = null;
  }

  LifecycleEventListener listener = new LifecycleEventListener() {
    @Override
    public void onHostResume() {
      maybeRegisterReceiver();
    }
    @Override
    public void onHostPause() {
      maybeUnregisterReceiver();
      }
    @Override
    public void onHostDestroy() {
      maybeUnregisterReceiver();
    }
  };

  @Override
  public void initialize() {
    getReactApplicationContext().addLifecycleEventListener(listener);
    maybeRegisterReceiver();
  }

  class PowerConnectionReceiver extends BroadcastReceiver {
    private BatteryManagerModule mBatteryModule;

    @Override
    public void onReceive(Context context, Intent intent) {
      notifyBatteryStateChanged(intent);
    }
  }

}
