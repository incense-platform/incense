/**
 * 
 */
package edu.incense.android.datatask.filter;

import android.util.Log;
import edu.incense.android.datatask.Input;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.DataType;
import edu.incense.android.datatask.data.WifiData;
import edu.incense.android.sensor.WifiConnectionSensor;

/**
 * Checks if the network name detected (in a scan) is included in the list of
 * access points registered
 * 
 * @author mxpxgx
 * 
 */
public class WifiTimeConnectedFilter extends DataFilter {
    public final static String TAG = "WifiTimeConnectedFilter";
    public final static String ATT_TIMECONNECTED = "timeConnected";
    public final static String ATT_TIMEDISCONNECTED = "timeDisconnected";
    private long connectionTime;
    private long disconnectionTime;
    private WifiData lastDataReceived;

    public WifiTimeConnectedFilter() {
        super();
        setFilterName("WifiTimeConnectedFilter");
        periodTime = 1000;
        connectionTime = 0;
        disconnectionTime = 0;
        lastDataReceived = null;
    }

    /**
     * Pulls data from every pipe and send them to the computeSingleData()
     * method, every cycle. The frequency is defined by setSampleFrequency.
     */
    @Override
    protected void compute() {
        Data tempData;
        for (Input i : inputs) {
            // Log.i(getClass().getName(), "Asking for new data");
            tempData = i.pullData();
            if (tempData != null) {
                computeSingleData(tempData);
                // Log.i(getClass().getName(), "GOOD");
            } else {
                if (lastDataReceived != null) {
                    lastDataReceived.getExtras().putString(ATT_TIMECONNECTED,
                            String.valueOf(getTimeConnected()));
                    lastDataReceived.getExtras().putString(ATT_TIMEDISCONNECTED,
                            String.valueOf(getTimeDisconnected()));
                    Log.d(TAG, "Time disconnected: "+lastDataReceived.getExtras().getString(ATT_TIMEDISCONNECTED));
                    pushToOutputs(lastDataReceived);
                }
            }
        }
    }

    @Override
    protected void computeSingleData(Data data) {
        if (data.getDataType() == DataType.WIFI) {
            lastDataReceived = (WifiData) data;
            boolean expectedInput = lastDataReceived.getExtras().containsKey(
                    WifiConnectionSensor.ATT_ISCONNECTED);
            if (expectedInput) {
                boolean connected = lastDataReceived.getExtras().getBoolean(
                        WifiConnectionSensor.ATT_ISCONNECTED);
                if (connected) {
                    connectionTime = System.currentTimeMillis();
                    disconnectionTime = 0;
                    Log.d(TAG, "Connected!");
                } else {
                    connectionTime = 0;
                    disconnectionTime = System.currentTimeMillis();
                    Log.d(TAG, "Disconnected!");
                }
                lastDataReceived.getExtras().putString(ATT_TIMECONNECTED,
                        String.valueOf(getTimeConnected()));
                lastDataReceived.getExtras().putString(ATT_TIMEDISCONNECTED,
                        String.valueOf(getTimeDisconnected()));
                pushToOutputs(lastDataReceived);
            } else {
                pushToOutputs(data);
            }
        }
    }

    private long getTimeDisconnected() {
        if (disconnectionTime <= 0)
            return 0;
        return System.currentTimeMillis() - disconnectionTime;
    }

    private long getTimeConnected() {
        if (connectionTime <= 0)
            return 0;
        return System.currentTimeMillis() - connectionTime;
    }

}