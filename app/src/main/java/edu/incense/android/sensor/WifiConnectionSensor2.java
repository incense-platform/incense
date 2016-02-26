/**
 * 
 */
package edu.incense.android.sensor;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.WifiData;

/**
 * @author mxpxgx
 * 
 */
public class WifiConnectionSensor2 extends Sensor {
    public final static String TAG = "WifiConnectionFilter";
    public final static String ATT_ISCONNECTED = "isConnected";
    private boolean connected;
    private ScanResult connectedResult;
    private WifiConfiguration connectedConfig;
    private List<ScanResult> scanResults;
    private List<String> ssidList;
    private WifiManager wifi;

    public WifiConnectionSensor2(Context context) {
        super(context);

        // WifiManager initiation
        String service = Context.WIFI_SERVICE;
        wifi = (WifiManager) context.getSystemService(service);

        // Initiate other attributes
        ssidList = new ArrayList<String>();
        connectedResult = null;
        connectedConfig = null;
        scanResults = null;
    }
//
//    public WifiConnectionSensor2(Context context, List<String> ssidList) {
//        this(context);
//        this.ssidList = ssidList;
//    }
//
//    /**
//     * Register an access point with its SSID (network's name).
//     * 
//     * @param ssid
//     */
//    public void addAp(String ssid) {
//        if (ssidList != null)
//            ssidList.add(ssid);
//    }
//
//    @Override
//    public void start() {
//        super.start();
//        setSensing(true);
//        enableWifi();
//        registerBroadcastReceivers();
//
//        connectedConfig = getConnectedConfig();
//        if (connectedConfig != null){
//            //remove quotes
//            String ssid = connectedConfig.SSID.substring(1, connectedConfig.SSID.length()-1);
//            // Case connected to the intended one
//            if (ssidList.contains(ssid)) {
//                setConnected(true);
//            }
//        }
//        // Case not connected to the intended one
//        if (connectedConfig == null) {
//
//        }
//        
//
//        // TODO init isconnected
//        stopConnection();// ?
//        startWifiScanProcess();
//    }
//    
//    @Override
//    public void stop() {
//        super.stop();
//        setSensing(false);
//        unregisterBroadcastReceivers();
//    }
//
//    /**
//     * Enable wifi, if needed
//     */
//    private void enableWifi() {
//        if (!wifi.isWifiEnabled()) {
//            if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
//                wifi.setWifiEnabled(true);
//            }
//        }
//    }
//
//    private void registerBroadcastReceivers() {
//        // Register wifi scan results receiver
//        IntentFilter intentFilter = new IntentFilter(
//                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        getContext().registerReceiver(this.wifiBroadcastReceiver, intentFilter);
//
//        // Register connection monitor
//        IntentFilter intentFilter2 = new IntentFilter(
//                ConnectivityManager.CONNECTIVITY_ACTION);
//        getContext().registerReceiver(this.connectionMonitor, intentFilter2);
//    }
//
//    private void unregisterBroadcastReceivers() {
//        getContext().unregisterReceiver(this.wifiBroadcastReceiver);
//        getContext().unregisterReceiver(this.connectionMonitor);
//    }
//
//    private void startWifiScanProcess() {
//        wifi.startScan();
//    }
//
//    // ?
//    private int configuredId(String ssid) {
//        if (ssid == null)
//            return -1;
//        List<WifiConfiguration> configList = wifi.getConfiguredNetworks();
//        for (WifiConfiguration config : configList) {
//            if (config.SSID != null && config.SSID.contains(ssid)) {
//                return config.networkId;
//            }
//        }
//        return -1;
//    }
//
//    /**
//     * Checks if the given ssid is currently connected
//     * 
//     * @param ssid
//     * @return
//     */
//    private boolean isConnected(String ssid) {
//        if (ssid == null)
//            return false;
//        List<WifiConfiguration> configList = wifi.getConfiguredNetworks();
//        ssid = '\"' + ssid + '\"';
//        for (WifiConfiguration config : configList) {
//            Log.d(TAG, "Compare: " + ssid + " and " + config.SSID);
//            if (config.SSID != null && (config.SSID.compareTo(ssid)==0)) {
//                Log.d(TAG, "Status: " + config.status);
//                if (config.status == WifiConfiguration.Status.CURRENT) {
//                    return true;
//                }
//
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Gets the ssid of the currently connected access point
//     * 
//     * @param ssid
//     * @return
//     */
//    private WifiConfiguration getConnectedConfig() {
//        List<WifiConfiguration> configList = wifi.getConfiguredNetworks();
//        for (WifiConfiguration config : configList) {
//            Log.d(TAG, config.SSID + ", status: " + config.status);
//            if (config.status == WifiConfiguration.Status.CURRENT) {
//                return config;
//            }
//        }
//        return null;
//    }
//
//    public void setConnected(boolean connected) {
//        this.connected = connected;
//        Data newData = new WifiData(scanResult);
//        newData.getExtras().putBoolean(this.ATT_ISCONNECTED, connected);
//        currentData = newData;
//    }
//
//    private boolean addNewAccessPoint(ScanResult scanResult) {
//        WifiConfiguration wc = new WifiConfiguration();
//        wc.SSID = '\"' + scanResult.SSID + '\"';
//        // wc.preSharedKey = "\"password\"";
//        wc.hiddenSSID = true;
//        wc.status = WifiConfiguration.Status.ENABLED;
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//        int networkId = wifi.addNetwork(wc);
//        Log.d(TAG, "addNetwork returned: " + networkId);
//        boolean disableOthers = true;
//        boolean b = wifi.enableNetwork(networkId, disableOthers);
//        Log.d(TAG, "enableNetwork returned: " + b);
//        return b;
//    }
//
//    private void startConnection(ScanResult scanResult) {
//        // Log.d(TAG, "ssid: " + scanResult.SSID);
//        boolean added = isConnected(scanResult.SSID);
//        int configuredId = configuredId(scanResult.SSID);
//        if (!added && configuredId >= 0) {
//            boolean disableOthers = true;
//            added = wifi.enableNetwork(configuredId, disableOthers);
//        } else if (!added) {
//            added = addNewAccessPoint(scanResult);
//        }
//        if (added) {
//            connected = true;
//            Data newData = new WifiData(scanResult);
//            newData.getExtras().putBoolean(this.ATT_ISCONNECTED, true);
//            currentData = newData;
//            connectedResult = scanResult;
//        }
//    }
//
//    private void stopConnection() {
//        connected = false;
//        if (connectedResult != null) {
//            Data newData = new WifiData(connectedResult);
//            newData.getExtras().putBoolean(this.ATT_ISCONNECTED, false);
//            currentData = newData;
//        }
//        startWifiScanProcess();
//    }
//
//    private boolean isConnected() {
//        return connected;
//    }
//
//    /**
//     * This BroadcastReceiver checks if a scan result has the ssid of the access
//     * points registered in this filter.
//     */
//    private BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String intentAction = intent.getAction();
//            if (intentAction
//                    .equalsIgnoreCase(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
//                    && !connected) {
//                Log.d(TAG, "Scan results available!");
//                scanResults = wifi.getScanResults();
//                for (ScanResult sr : scanResults) {
//                    if (ssidList.contains(sr.SSID)) {
//                        startConnection(sr);
//                        return;
//                    }
//                }
//            }
//
//        }
//    };
//
//    /**
//     * This BroadcastReceiver checks if the WiFi connection was lost or
//     * established.
//     */
//    private BroadcastReceiver connectionMonitor = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(
//                    android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {
//
//                NetworkInfo netInfo = (NetworkInfo) intent
//                        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//                NetworkInfo.State state = netInfo.getState();
//                boolean disconnected = (state == NetworkInfo.State.DISCONNECTED || state == NetworkInfo.State.SUSPENDED);
//                boolean isWifi = (netInfo.getType() == ConnectivityManager.TYPE_WIFI);
//
//                if (isConnected() && disconnected && isWifi) {
//                    Log.d(TAG, "Disconnected!");
//                    stopConnection();
//                    Log.d(TAG, "Connection was lost");
//                    Toast.makeText(context, "Wifi connection was lost",
//                            Toast.LENGTH_LONG).show();
//                }
//
//                if (!isConnected() && !disconnected && isWifi) {
//                    Log.d(TAG, "Might be connected!");
//                    startWifiScanProcess();
//                    Toast.makeText(context, "Wifi connection reconnected",
//                            Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//    };
}
