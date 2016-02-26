package edu.incense.android.datatask.data;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

public class BluetoothData extends Data {
    private String address;
    private int state;
    private String name;
    private int deviceClass;
    private int majorDeviceClass;

    public BluetoothData(BluetoothDevice bluetoothDevice) {
        super(DataType.BLUETOOTH);
        address = bluetoothDevice.getAddress();
        state = bluetoothDevice.getBondState();
        name = bluetoothDevice.getName();
        BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
        deviceClass = bluetoothClass.getDeviceClass();
        majorDeviceClass = bluetoothClass.getMajorDeviceClass();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeviceClass(int deviceClass) {
        this.deviceClass = deviceClass;
    }

    public void setMajorDeviceClass(int majorDeviceClass) {
        this.majorDeviceClass = majorDeviceClass;
    }

    public String getAddress() {
        return address;
    }

    public int getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public int getDeviceClass() {
        return deviceClass;
    }

    public int getMajorDeviceClass() {
        return majorDeviceClass;
    }
}
