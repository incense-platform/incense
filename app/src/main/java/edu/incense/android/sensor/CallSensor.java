package edu.incense.android.sensor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.incense.android.datatask.data.BatteryStateData;
import edu.incense.android.datatask.data.CallData;
import edu.incense.android.datatask.data.SmsData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallSensor extends Sensor {	
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	  Date date = new Date();
	static final String ACTION_RECEIVED ="android.intent.action.PHONE_STATE";
	  
	    
	public CallSensor(Context context) {
		super(context);
		setName("Calls");
		
	}
	
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
		IntentFilter batteryLevelFilter = new IntentFilter(ACTION_RECEIVED);		
        getContext().registerReceiver(batteryStateReceiver, batteryLevelFilter);
		
	}
	
	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub
		super.stop();
		getContext().unregisterReceiver(batteryStateReceiver);
	}
	
	BroadcastReceiver batteryStateReceiver = new BroadcastReceiver() {

	    
        public void onReceive(Context context, Intent intent) {
        	try
            {
             
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

             
              
                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
                {
                	currentData = new CallData("Phone is rinning "+date);
                	Log.i("softSensing", "Phone is rinning ");
                     // Your Code
                }
                
                if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
                {
                	currentData = new SmsData("Call Recieved "+date);
                	Log.i("softSensing", "Call Recieved ");
                         // Your Code
                }
                
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
                {
                	currentData = new SmsData("Phone Is Idle "+date);
                	Log.i("softSensing", "Phone Is Idle");
                    
                        // Your Code
                  
                }
            }
            catch(Exception e)
            {
                //your custom message
            }
         
       
        }
    };

}


