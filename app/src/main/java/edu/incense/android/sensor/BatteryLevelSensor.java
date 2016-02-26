/**
 * 
 */
package edu.incense.android.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import edu.incense.android.datatask.data.BatteryLevelData;

/**
 * Based on: http://mobile.dzone.com/news/getting-battery-level-android
 * 
 * Computes the battery level by registering a receiver to the intent triggered
 * by a battery status/level chang
 * 
 * @author mxpxgx
 * 
 */
public class BatteryLevelSensor extends Sensor {

    /**
     * @param context
     */
    public BatteryLevelSensor(Context context) {
        super(context);
        setName("Battery");
    }
    
    @Override
    public void start() {
        super.start();
        
        IntentFilter batteryLevelFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        getContext().registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }
    
    @Override
    public void stop() {
        super.stop();
        getContext().unregisterReceiver(batteryLevelReceiver);
    }

    BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }
            setNewLevel(level);
        }
    };
    
    private void setNewLevel(int level){
        currentData = new BatteryLevelData(level);
    }

}
