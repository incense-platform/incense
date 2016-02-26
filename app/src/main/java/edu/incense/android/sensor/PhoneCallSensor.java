package edu.incense.android.sensor;

import java.util.ArrayList;

import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.PhoneCallData;

import android.content.Context;
//import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneCallSensor extends Sensor {
    private TelephonyManager telephonyManager;

    public PhoneCallSensor(Context context) {
        super(context);
        setName("Calls");
        dataList = new ArrayList<Data>();

        String service = Context.TELEPHONY_SERVICE;
        telephonyManager = (TelephonyManager) context.getSystemService(service);
        telephonyManager.listen(new PhoneStateListener() {
            // Log received calls
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    PhoneCallData newData = new PhoneCallData(incomingNumber,
                            true);
                    dataList.add(newData);
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

}
