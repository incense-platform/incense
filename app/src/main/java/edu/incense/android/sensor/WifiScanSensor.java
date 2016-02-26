package edu.incense.android.sensor;

import java.util.ArrayList;
import java.util.List;

import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.WifiData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiScanSensor extends Sensor {

    private WifiManager wifi;

    public WifiScanSensor(Context context) {
        super(context);
        setName("WiFi scan");
        // Initialize list where results will be stored
        dataList = new ArrayList<Data>();

        String service = Context.WIFI_SERVICE;
        wifi = (WifiManager) context.getSystemService(service);
        if (!wifi.isWifiEnabled())
            if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                wifi.setWifiEnabled(true);
        IntentFilter intentFilter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(this.wifiBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction
                    .equalsIgnoreCase(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                List<ScanResult> scanResults = wifi.getScanResults();
                WifiData newData;
                for (ScanResult sr : scanResults) {
                    newData = new WifiData(sr);
                    if (!dataList.contains(newData)) {
                        dataList.add(newData);
                    }
                }
            }

        }
    };

    @Override
    public void start() {
        super.start();
        startWifiProcess();
    }

    @Override
    public void stop() {
        super.stop();
        finishWifiProcess();
    }

    private void startWifiProcess() {
        setSensing(wifi.startScan());
    }

    private void finishWifiProcess() {
        // Disable wifi
        if (wifi.isWifiEnabled())
            wifi.setWifiEnabled(false);

        getContext().unregisterReceiver(this.wifiBroadcastReceiver);
    }

}
