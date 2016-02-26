/**
 * 
 */
package edu.incense.android.sensor;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.provider.Settings;
import android.util.Log;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.WifiData;

/**
 * @author mxpxgx
 * 
 */
public class WifiConnectionSensor extends Sensor {
    public final static String TAG = "WifiConnectionSensor";
    public final static String ATT_ISCONNECTED = "isConnected";
    private boolean connected;
    private WifiConfiguration connectedConfig;
    private WifiInfo connectedWifiInfo;
    private WifiManager wifiManager;

    public WifiConnectionSensor(Context context) {
        super(context);
        setName("WiFi");
        // WifiManager initiation
        String service = Context.WIFI_SERVICE;
        wifiManager = (WifiManager) context.getSystemService(service);

        // Initiate other attributes
        connectedConfig = null;
        connectedWifiInfo = null;

    }

    @Override
    public void start() {
        super.start();
        setSensing(true);
        enableWifi();

        connectedConfig = getConnectedConfig();
        if (connectedConfig != null) {
            connectedWifiInfo = wifiManager.getConnectionInfo();
            setConnected(true);
        } else {
            // Generate empty data
            Data newData = new WifiData();
            newData.getExtras().putBoolean(ATT_ISCONNECTED, false);
            currentData = newData;
        }
        registerBroadcastReceivers();
        
        forceWifiToStayOn();
    }
    
    @Override
    public void stop() {
        super.stop();
        setSensing(false);
        unregisterBroadcastReceivers();
        disableWifi();
        
        letWifiToTurnOff();
    }
    
    private WifiLock wifiLock;
    private void forceWifiToStayOn(){
        // Set WiFi sleep policy to never
        Settings.System.putInt(getContext().getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_NEVER);
        
        WifiLock wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL , "MyWifiLock");
        if(!wifiLock.isHeld()){
            wifiLock.acquire();
        }
    }
    
    private void letWifiToTurnOff(){
        // Set WiFi sleep policy to default
        Settings.System.putInt(getContext().getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        wifiLock.release();
    }

    /**
     * Enable wifi, if needed
     */
    boolean wasEnabled = false;

    private void enableWifi() {
        wasEnabled = wifiManager.isWifiEnabled();
        if (!wasEnabled) {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                wifiManager.setWifiEnabled(true);
            }
        }
    }

    private void disableWifi() {
        if (wifiManager.isWifiEnabled() && !wasEnabled) {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLING) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    private void registerBroadcastReceivers() {
        // Register connection monitor
        IntentFilter intentFilter2 = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(this.connectionMonitor, intentFilter2);
    }

    private void unregisterBroadcastReceivers() {
        getContext().unregisterReceiver(this.connectionMonitor);
    }

    /**
     * Gets the ssid of the currently connected access point
     * 
     * @param ssid
     * @return
     */
    private WifiConfiguration getConnectedConfig() {
        List<WifiConfiguration> configList = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configList) {
            Log.d(TAG, config.SSID + ", status: " + config.status);
            if (config.status == WifiConfiguration.Status.CURRENT) {
                return config;
            }
        }
        return null;
    }

    private void setConnected(boolean connected) {
        if (connectedWifiInfo != null) {
            this.connected = connected;
            Data newData = new WifiData(connectedWifiInfo);
            newData.getExtras().putBoolean(ATT_ISCONNECTED, connected);
            currentData = newData;
        }
    }

    private boolean isConnected() {
        return connected;
    }

    /**
     * This BroadcastReceiver checks if the WiFi connection was lost or
     * established.
     */
    private BroadcastReceiver connectionMonitor = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {

                NetworkInfo netInfo = (NetworkInfo) intent
                        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = netInfo.getState();
                boolean disconnected = (state == NetworkInfo.State.DISCONNECTED || state == NetworkInfo.State.SUSPENDED);
                boolean isWifi = (netInfo.getType() == ConnectivityManager.TYPE_WIFI);

                if (isConnected() && disconnected && isWifi) {
                    Log.d(TAG, "WiFi connection lost!");
                    setConnected(false);
                    // Toast.makeText(context, "WiFi connection lost",
                    // Toast.LENGTH_LONG).show();
                    //TODO should try to reconnect
                }

                if (!isConnected() && !disconnected && isWifi) {
                    Log.d(TAG, "WiFi connection established!");
                    connectedWifiInfo = wifiManager.getConnectionInfo();
                    setConnected(true);
                    // Toast.makeText(context, "WiFi connection established",
                    // Toast.LENGTH_LONG).show();
                }

            }
        }
    };
}
