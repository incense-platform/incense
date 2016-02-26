package edu.incense.android.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import edu.incense.android.R;

public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;
    CheckBoxPreference bluetoothCheckBoxPreference;
    CheckBoxPreference wifiCheckBoxPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        
        EditTextPreference usernameEtp = (EditTextPreference) getPreferenceScreen()
        .findPreference("editTextUsername");
        usernameEtp.getEditText().setFilters(new InputFilter[] { usernameFilter });

        bluetoothCheckBoxPreference = (CheckBoxPreference) getPreferenceScreen()
                .findPreference("checkboxBluetooth");
        bluetoothCheckBoxPreference
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                        // setWifiOff();
                        wifiCheckBoxPreference.setChecked(false);
                        verifyBluetooth();
                        return true;
                    }
                });

        // Get a reference to the checkbox preference
        CheckBoxPreference gpsCheckBoxPreference = (CheckBoxPreference) getPreferenceScreen()
                .findPreference("checkboxGps");
        gpsCheckBoxPreference
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                        verifyGps();
                        return true;
                    }
                });

        wifiCheckBoxPreference = (CheckBoxPreference) getPreferenceScreen()
                .findPreference("checkboxWifi");
        wifiCheckBoxPreference
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                        // setBluetoothOff();
                        bluetoothCheckBoxPreference.setChecked(false);
                        verifyWifi();
                        return true;
                    }
                });
    }
    
    InputFilter usernameFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                Spanned dest, int dstart, int dend) {
            if(dstart == 0 && (end-start)>0 && !Character.isLetter(source.charAt(start))){
                Toast.makeText(SettingsActivity.this, "It should start with a letter.", Toast.LENGTH_SHORT).show();
                return "";
            }
            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i))) {
                    Toast.makeText(SettingsActivity.this, "Only letters and numbers please.", Toast.LENGTH_SHORT).show();
                    return "";
                }
            }
            return null;
        }
    };

    // Called at the start of the visible lifetime.
    @Override
    public void onStart() {
        super.onStart();
        // Apply any required UI change now that the Activity is visible.
    }

    private void verifyGps() {
        boolean isGpsOn = sharedPreferences.getBoolean("checkboxGps", false);

        if (isGpsOn) {
            String service = Context.LOCATION_SERVICE;
            LocationManager locationManager = (LocationManager) getSystemService(service);
            String provider = LocationManager.GPS_PROVIDER;

            if (!locationManager.isProviderEnabled(provider)) {
                // Provider not enabled, prompt user to enable it
                Toast.makeText(this, "Please turn GPS on", Toast.LENGTH_LONG)
                        .show();
                Intent myIntent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            }
        }
    }

    private void verifyWifi() {
        boolean isWifiOn = sharedPreferences.getBoolean("checkboxWifi", false);

        if (isWifiOn) {
            String service = Context.WIFI_SERVICE;
            WifiManager wifiManager = (WifiManager) getSystemService(service);
            if (!wifiManager.isWifiEnabled()) {
                // Provider not enabled, prompt user to enable it
                Toast.makeText(this, "Please turn WiFi on", Toast.LENGTH_LONG)
                        .show();
                Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            }
        }
    }

    private void verifyBluetooth() {
        boolean isBluetoothOn = sharedPreferences.getBoolean(
                "checkboxBluetooth", false);

        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (isBluetoothOn && bluetooth != null) {

            if (!bluetooth.isEnabled()) {
                // Provider not enabled, prompt user to enable it
                Toast.makeText(this, "Please turn Bluetooth on",
                        Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            }
        }
    }

    /*
     * private void setSensorOff(String sensorName){ // Retrieve an editor to
     * modify the shared preferences. SharedPreferences.Editor editor =
     * sharedPreferences.edit();
     * 
     * // Store new primitive types in the shared preferences object.
     * editor.putBoolean(sensorName, false);
     * 
     * // Commit the changes. editor.commit(); }
     * 
     * private void setWifiOff(){ setSensorOff("checkboxWifi"); }
     * 
     * private void setBluetoothOff(){ setSensorOff("checkboxBluetooth"); }
     */

    /*** MENU ***/

    // This inflates/populates your menu resource (convert the XML resource into
    // a programmable object)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    // This method passes the MenuItem that the user selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
        case R.id.new_recording:
            Intent recordIntent = new Intent(this, RecordActivity.class);
            startActivity(recordIntent);
            return true;
        case R.id.settings:
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            // startActivityForResult(settingsIntent, SHOW_PREFERENCES);
            return true;
        case R.id.results:
            Intent resultsIntent = new Intent(this, ResultsListActivity.class);
            startActivity(resultsIntent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences,
     *      java.lang.String)
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        // if (key.equals("editTextUsername")) {
        // // Search for a valid mail pattern
        // String pattern = "mailpattern";
        // String value = sharedPreferences.getString(key, null);
        // if (!Pattern.matches(pattern, value)) {
        // // The value is not a valid email address.
        // // Do anything like advice the user or change the value
        // }
        // }
    }
}
