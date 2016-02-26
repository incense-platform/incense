/**
 * 
 */
package edu.incense.android.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import edu.incense.android.datatask.data.NfcData;
import edu.incense.android.ui.NfcActivity;

/**
 * 
 * @author mxpxgx
 * 
 */
public class NfcSensor extends Sensor {

    private static final String TAG = "NfcSensor";
//    private String note;
    private IntentFilter filter;

    /**
     * @param context
     */
    public NfcSensor(Context context) {
        super(context);
        setName("NFC");
        filter = new IntentFilter();
        filter.addAction(NfcActivity.NFC_TAG_ACTION);
    }
    
    /*** BROADCAST_RECEIVER ***/
    private BroadcastReceiver nfcTagReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(
                    NfcActivity.NFC_TAG_ACTION) == 0) {
                
                    String message = intent.getStringExtra(NfcActivity.ACTION_NFC_TAG);
                    Toast.makeText(getContext(), message,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "NFC tag received: "+message);
                    currentData = new NfcData(message);
            }
        }

    };

    /**
     * @see edu.incense.android.sensor.Sensor#start()
     */
    @Override
    public synchronized void start() {
        super.start();
        getContext().registerReceiver(nfcTagReceiver, filter);
    }

    /**
     * @see edu.incense.android.sensor.Sensor#stop()
     */
    @Override
    public synchronized void stop() {
        super.stop();
        getContext().unregisterReceiver(nfcTagReceiver);
    }
}
