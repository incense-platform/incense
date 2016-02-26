package edu.incense.android.datatask.data;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;

public class WifiData extends Data {
    private String bssid;
    private String ssid;
    private String capabilities;
    private int frequency;
    private int level;

    public WifiData() {
        super(DataType.WIFI);
    }
    public WifiData(ScanResult scanResult) {
        super(DataType.WIFI);
        setBssid(scanResult.BSSID);
        setFrequency(scanResult.frequency);
        setLevel(scanResult.level);
        setSsid(scanResult.SSID);
        setCapabilities(scanResult.capabilities);
    }

    public WifiData(WifiConfiguration config) {
        super(DataType.WIFI);
        setBssid(config.BSSID);
        // remove quotes
        String ssid = config.SSID.substring(1, config.SSID.length() - 1);
        setSsid(ssid);
    }

    public WifiData(WifiInfo wifiInfo) {
        super(DataType.WIFI);
        setBssid(wifiInfo.getBSSID());
        setLevel(wifiInfo.getRssi());
        setSsid(wifiInfo.getSSID());
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
