package edu.incense.android.sensor;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class BluetoothConnectionSensor extends Sensor implements Runnable{
    private static final String TAG = "BluetoothConnectionSensor";
    private BluetoothDevice device;
    private Thread thread;

    public BluetoothConnectionSensor(Context context, String address) {
        super(context);
        setName("BT");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(BluetoothAdapter.checkBluetoothAddress(address)){
            device = adapter.getRemoteDevice(address);
        } else {
            Log.e(TAG, "Bluetooth address is incorrect");
            Toast.makeText(getContext(), "Bluetooth address is incorrect", Toast.LENGTH_SHORT);
        }
    }


    @Override
    public void start() {
        super.start();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() {
        super.stop();
    }


    /**
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while(isSensing()){
            try {
                Thread.sleep(getPeriodTime());
            } catch (InterruptedException e) {
                Log.e(TAG, "Sleep failed", e);
            }
            
            try {
               BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
               if(socket != null){
                   socket.connect();
               }
               //socket.setupHttpRequest();
            } catch (IOException e) {
                Log.e(TAG, "Connection failed", e);
//                Toast.makeText(getContext(), "Connection failed"+e, Toast.LENGTH_LONG);
            }
        }
    }

}
