package edu.incense.android.sensor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.incense.android.datatask.data.BatteryStateData;
import edu.incense.android.datatask.data.NfcData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryStateSensor extends Sensor{
	
    private float batteryPercentage=0;
    private boolean isAlreadyCharging=false;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    
	public  BatteryStateSensor(Context context) {
		super(context);
		setName("StateBat");		
		// TODO Auto-generated constructor stub
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
		IntentFilter batteryLevelFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
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
	        	 int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	             int isPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
	             if(status == BatteryManager.BATTERY_STATUS_CHARGING) {
	                 isAlreadyCharging = true;
	                 isCharging();
	             }

	             if(isPlugged == BatteryManager.BATTERY_PLUGGED_USB) {
	            	 
	            	 currentData = new BatteryStateData("Plugged Usb "+date);
	                 Log.i("softSensing", "Plugged USB");
	             }

	             if(isPlugged == BatteryManager.BATTERY_PLUGGED_AC) {
	            	 currentData = new BatteryStateData("Plugged AC "+date);
	            	 Log.i("softSensing", "Plugged AC");
	             }
	        }
	    };
	    
	    public void isCharging() {
	        isAlreadyCharging = true;
	        currentData = new BatteryStateData("Phone is charging "+date);
	        Log.i("softSensing", "Phone is charging ");
	    }

	    public void isLowBattery() {
	    	currentData = new BatteryStateData("Battery LOW "+date);
	        Log.i("softSensing", "Battery LOW");
	    }

	    public void usbCharge() {
	    	currentData = new BatteryStateData("Battery LOW "+date);
	        Log.i("softSensing", "batteryPercentage: "+batteryPercentage);
	        Log.i("softSensing", "batteryPercentage: "+batteryPercentage);
	    }

	    public void acCharge() {
	    	currentData = new BatteryStateData("AC Charging "+date);
	        Log.i("softSensing", "AC Charging");
	    }

	 
}
