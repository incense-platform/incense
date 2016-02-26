/**
 * 
 */
package edu.incense.android.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import edu.incense.android.datatask.data.others.BooleanData;

/**
 * Generates TRUE BooleanData when the device is power/AC connected (charging), or a FALSE
 * BooleanData when is not connected.
 * @author mxpxgx
 * 
 */
public class PowerConnectionSensor extends Sensor {
    /**
     * @param context
     */
    public PowerConnectionSensor(Context context) {
        super(context);
        setName("Power");
    }

    @Override
    public void start() {
        super.start();

        IntentFilter connectionFilter = new IntentFilter(
                Intent.ACTION_POWER_CONNECTED);
        getContext()
                .registerReceiver(powerConnectionReceiver, connectionFilter);
        IntentFilter disconnectionFilter = new IntentFilter(
                Intent.ACTION_POWER_DISCONNECTED);
        getContext().registerReceiver(powerConnectionReceiver,
                disconnectionFilter);
    }

    @Override
    public void stop() {
        super.stop();
        getContext().unregisterReceiver(powerConnectionReceiver);
    }

    BroadcastReceiver powerConnectionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_POWER_CONNECTED) == 0) {
                setNewState(true);
            } else if (intent.getAction().compareTo(
                    Intent.ACTION_POWER_DISCONNECTED) == 0) {
                setNewState(false);
            }
        }
    };

    private void setNewState(boolean state) {
        currentData = new BooleanData(state);
    }
}
