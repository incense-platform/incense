package edu.incense.android.project.validator;

import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import edu.incense.android.datatask.model.TaskType;
import edu.incense.android.project.ProjectSignature;

public class DeviceProjectValidator implements ProjectValidator {
    Context context;

    public DeviceProjectValidator(Context context) {
        this.context = context;
    }

    private boolean isSensorAvailable(int SensorType) {
        String service = Context.SENSOR_SERVICE;
        SensorManager sensorManager = (SensorManager) context
                .getSystemService(service);
        List<Sensor> sensors = sensorManager.getSensorList(SensorType);
        if (!sensors.isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean isBluetoothAvailabe() {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == null)
            return false;
        else
            return true;
    }

    private boolean isGpsAvailabe() {
        String service = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) context
                .getSystemService(service);
        List<String> providers = locationManager.getAllProviders();

        if (!providers.isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean isWifiAvailabe() {
        String service = Context.WIFI_SERVICE;
        WifiManager wifi = (WifiManager) context.getSystemService(service);
        if (wifi != null) {
            return true;
        }
        return false;
    }

    private boolean isSensorEnabled(TaskType taskType) {
        boolean enabled = false;

        switch (taskType) {
        case AccelerometerSensor:
            enabled = isSensorAvailable(Sensor.TYPE_ACCELEROMETER);
            break;
        case AudioSensor:
            enabled = true; // Mobile Phones always include a microphone
            break;
        case BluetoothSensor:
            enabled = isBluetoothAvailabe();
            break;
        case GpsSensor:
            enabled = isGpsAvailabe();
            break;
        case CallSensor:
            enabled = true; // Mobile Phones always include calls
            break;
        case StateSensor:
            enabled = true; // Mobile Phones always include states
            break;
        case WifiScanSensor:
            enabled = isWifiAvailabe();
            break;
        case WifiConnectionSensor:
            enabled = isWifiAvailabe();
            break;
        default:
            Log.i(getClass().getName(), "Sensor not available.");
            enabled = false;
            break;
        }
        return enabled;
    }

    public boolean isValid(ProjectSignature projectSignature) {
        for (TaskType taskType : projectSignature.getSensors()) {
            if (!isSensorEnabled(taskType))
                return false;
        }
        return true;
    }
}
