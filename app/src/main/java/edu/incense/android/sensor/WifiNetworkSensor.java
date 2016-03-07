package edu.incense.android.sensor;

// Incense libraries
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.WifiNetworkData;

// Android libraries
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;

// Java libraries
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;




/**
 * Created by xilef on 3/4/2016.
 *
 * This class represents a sensor that gets the available wifi networks at a given time.
 *
 */
public class WifiNetworkSensor extends Sensor {

    private WifiManager wifiMng;
    private Timer t;

    public WifiNetworkSensor (Context context){
        super(context);
        wifiMng = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        this.dataList = new ArrayList<Data>();

        IntentFilter wifiNetworksIntent = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(this.wifiScanReceiver, wifiNetworksIntent);
    }

    // The receiver that will receive the results of a wifi scan.
    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> wifiResults =  wifiMng.getScanResults();
            WifiNetworkData wnd = new WifiNetworkData();

            // Adds each one of the networks found on a WifiNetworkData object.
            for (ScanResult sr : wifiResults){
                wnd.addWifiNetworkItem(sr);
            }

            // The WifiNetworkData is saved in the list.
            WifiNetworkSensor.this.dataList.add(wnd);
        }
    };

    // The task that will execute the network scans.
    private TimerTask sensingTask = new TimerTask(){
        @Override
        public void run() {
            wifiMng.startScan();
        }
    };

    @Override
    public void start(){
        super.start();
        setSensing(wifiMng.startScan());

        // Schedule sensing of wifi sensor.
        t = new Timer();
        t.schedule(this.sensingTask, this.getPeriodTime(), this.getPeriodTime());
    }

    @Override
    public void stop(){
        super.stop();
        getContext().unregisterReceiver(this.wifiScanReceiver);
        t.cancel();
    }
}
