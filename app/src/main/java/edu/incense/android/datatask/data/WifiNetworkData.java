package edu.incense.android.datatask.data;

// Android libraries.
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

// Java libraries
import java.util.*;
/**
 * Created by xilef on 3/3/2016.
 *
 * This class is used to represent the available wifi networks at a given time.
 */
public class WifiNetworkData extends Data{

    // This class represents a single wifi access point.
    public class WifiNetworkItem{
        public String bssid; // The mac address of the access point.
        public String ssid; // The name of the access point.
        public int frequency; // The frequency of the channel the client is using to communicate.
        public int rssi; // The level of the signal the client is getting.

        public WifiNetworkItem(ScanResult scanResult){
            this.bssid = scanResult.BSSID;
            this.ssid = scanResult.SSID;
            this.frequency = scanResult.frequency;
            this.rssi = scanResult.level;
        }
    }

    private List<WifiNetworkItem> wifiNetworks;

    // Creates a new instance of the WifiNetworkData class.
    public WifiNetworkData(){
        super(DataType.WIFI);
        wifiNetworks = new LinkedList<WifiNetworkItem>();
    }

    // Creates a nre instance of the WifiNetworkData class and adds a WifiNetworkItem to its list.
    public WifiNetworkData(ScanResult scanResult){
        super(DataType.WIFI);
        wifiNetworks = new LinkedList<WifiNetworkItem>();
        WifiNetworkItem newItem = new WifiNetworkItem(scanResult);

        wifiNetworks.add(newItem);
    }

    // Adds an item to the list of detected networks.
    public void addWifiNetworkItem(ScanResult scanResult){
        WifiNetworkItem newItem = new WifiNetworkItem(scanResult);
        wifiNetworks.add(newItem);
    }

    public List<WifiNetworkItem> getWifiNetworks(){
        return this.wifiNetworks;
    }
}
