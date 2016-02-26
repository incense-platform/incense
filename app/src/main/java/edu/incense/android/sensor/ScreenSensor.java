package edu.incense.android.sensor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.incense.android.datatask.data.ScreenData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenSensor extends Sensor{
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    
	public ScreenSensor(Context context) {
		super(context);
		setName("Screen");
	}
	
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
		IntentFilter screenOn = new IntentFilter(
                Intent.ACTION_SCREEN_ON);
        getContext().registerReceiver(batteryStateReceiver, screenOn);
        IntentFilter screenOff = new IntentFilter(
                Intent.ACTION_SCREEN_OFF);
        getContext().registerReceiver(batteryStateReceiver, screenOff);
        
	}
	
	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub
		super.stop();
		getContext().unregisterReceiver(batteryStateReceiver);
	}
	
	BroadcastReceiver batteryStateReceiver = new BroadcastReceiver() {

	    
        public void onReceive(Context context, Intent intent) {
        	if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
        		currentData = new ScreenData("SCREEN_OFF "+date);
                Log.i("softSensing", "SCREEN_OFF");
                // onPause() will be called.
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
            	currentData = new ScreenData("SCREEN ON "+date);
                Log.i("softSensing", "SCREEN ON "+date);
            }

            else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT))
            {
            	currentData = new ScreenData("Phone Unlocked "+date);
                Log.i("softSensing", "Phone Unlocked");
            // Handle resuming events
            }
        }
    };

}
