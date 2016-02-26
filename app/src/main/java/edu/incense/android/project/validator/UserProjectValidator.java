package edu.incense.android.project.validator;

import edu.incense.android.datatask.model.TaskType;
import edu.incense.android.project.ProjectSignature;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class UserProjectValidator implements ProjectValidator {

    private SharedPreferences sp;

    public UserProjectValidator(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private boolean isSensorEnabled(TaskType taskType) {
        boolean enabled = false;

        switch (taskType) {
        case AccelerometerSensor:
            enabled = sp.getBoolean("checkboxAccelerometer", false);
            break;
        case AudioSensor:
            enabled = sp.getBoolean("checkboxAudio", false);
            break;
        case BluetoothSensor:
            enabled = sp.getBoolean("checkboxBluetooth", false);
            break;
        case GpsSensor:
            enabled = sp.getBoolean("checkboxGps", false);
            break;
        case CallSensor:
            enabled = sp.getBoolean("checkboxCall", false);
            break;
        case StateSensor:
            enabled = sp.getBoolean("checkboxState", false);
            break;
        case WifiScanSensor:
            enabled = sp.getBoolean("checkboxWifi", false);
            break;
        case WifiConnectionSensor:
            enabled = sp.getBoolean("checkboxWifi", false);
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
