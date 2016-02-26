package edu.incense.android.sensor;

import java.util.ArrayList;

import edu.incense.android.datatask.data.BluetoothData;
import edu.incense.android.datatask.data.Data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BluetoothSensor extends Sensor {

    public BluetoothSensor(Context context) {
        super(context);
        setName("BT");
        // Initialize list where results will be stored
        dataList = new ArrayList<Data>();
        context.registerReceiver(discoveryResult, new IntentFilter(
                BluetoothDevice.ACTION_FOUND));
    }

    // Initialize broadcast receiver
    private BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String remoteDeviceName = intent
                    .getStringExtra(BluetoothDevice.EXTRA_NAME);
            Log.i(getClass().getName(), "New device discovered: "
                    + remoteDeviceName + ".");

            BluetoothDevice remoteDevice = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothData newData = new BluetoothData(remoteDevice);
            if (!dataList.contains(newData)) {
                dataList.add(newData);
            }
        }
    };

    @Override
    public void start() {
        super.start();
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!bluetooth.isDiscovering()) {
            bluetooth.startDiscovery();
        }
    }

    @Override
    public void stop() {
        super.stop();
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null) {
            if (bluetooth.isDiscovering()) {
                bluetooth.cancelDiscovery();
            }
        }
        getContext().unregisterReceiver(this.discoveryResult);
    }

}
