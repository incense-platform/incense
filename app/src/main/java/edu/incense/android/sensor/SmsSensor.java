package edu.incense.android.sensor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.incense.android.datatask.data.BatteryStateData;
import edu.incense.android.datatask.data.SmsData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class SmsSensor extends Sensor{
	static final String ACTION_RECEIVED ="android.provider.Telephony.SMS_RECEIVED";
    static final String ACTION_SENT ="android.provider.Telephony.SMS_SENT";
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    
	public SmsSensor(Context context) {
		super(context);
		setName("Sms");	
	}
	@Override
		public synchronized void start() {
			// TODO Auto-generated method stub
			super.start();
			IntentFilter smsReceived = new IntentFilter(ACTION_RECEIVED);
	        getContext().registerReceiver(batteryStateReceiver, smsReceived);
	        IntentFilter smsSent = new IntentFilter(ACTION_SENT);
	        getContext().registerReceiver(batteryStateReceiver, smsSent);
		}
	
	@Override
		public synchronized void stop() {
			// TODO Auto-generated method stub
			super.stop();
			getContext().unregisterReceiver(batteryStateReceiver);
		}
	
	 BroadcastReceiver batteryStateReceiver = new BroadcastReceiver() {

		    
	        public void onReceive(Context context, Intent intent) {
	        	if (intent.getAction().equals(ACTION_RECEIVED)){
	                smsReceived();
	            }else if(intent.getAction().equals(ACTION_SENT)){
	                smsSent();
	            }
	        }
	    };
	    public void smsReceived() {
	    	currentData = new SmsData("SmsReceived");
	        Log.i("softSensing", "smsReceived "+date);
	    }

	    public void smsSent() {
	    	currentData = new SmsData("SmsSent");
	        Log.i("softSensing", "smsSent "+date);
	    }
}
