package edu.incense.android.sensor;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import edu.incense.android.datatask.data.GpsData;

/**
 * Receives location updates with a BroadcastReceiver. It includes a controller
 * (Runnable) that turns off GPS for while (MAX_TIME_WITHOUT_NEW_LOCATION +
 * RESTART_TIME), if there is no new location in certain time
 * (MAX_TIME_WITHOUT_NEW_LOCATION).
 * 
 * @author alanmtz
 * 
 */

public class GpsSensor extends Sensor {
    private final static String LOCATION_UPDATE_ACTION = "locationUpdate";
    private final static long MIN_RATE_TIME = 20L * 1000L; // 20 seconds
    private final static float MIN_DISTANCE = 5.0F; // In meters
    private final static long MAX_TIME_WITHOUT_NEW_LOCATION = 2L * 60L * 1000L; // 2
                                                                                // minutes
    private final static long RESTART_TIME = 5L * 60L * 1000L; // 5 minutes
    private final static String TAG = "GpsSensor";
    private ScheduledThreadPoolExecutor stpe;

    private LocationManager locationManager;
    private long lastLocationTime;
    private boolean locationAdded;
    // private boolean start;
    // private HandlerThread handlerThread;
    private PendingIntent pendingIntent;

    public GpsSensor(Context context) {
        super(context);
        setName("GPS");
        locationAdded = false;
        // LocationManager initialization
        String service = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) context.getSystemService(service);
        Intent intent = new Intent(LOCATION_UPDATE_ACTION);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 5000, intent,
                0);
    }

    private Location registerProvider(String provider) {
        long minTime = this.getPeriodTime() < MIN_RATE_TIME ? MIN_RATE_TIME
                : getPeriodTime();
        Location location = null;
        try {
            Log.d(TAG, "Time rate: " + minTime);
            locationManager.requestLocationUpdates(provider, minTime,
                    MIN_DISTANCE, pendingIntent);
            // Initialize it with the last known location (it is better than
            // nothing at all).
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.i(TAG, "New location: " + location.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Requesting location updates failed", e);
        }
        Log.i(TAG, "Location Provider registered: " + provider);
        return location;
    }

    @Override
    public void start() {
        super.start();
        // We are using any provider (GPS, NETWORK or PASSIVE)
        addLocationListenerWithAllProviders();
        // Thread thread = new Thread(controller);
        // thread.start();
        stpe = new ScheduledThreadPoolExecutor(1);
        stpe.scheduleAtFixedRate(controller, MAX_TIME_WITHOUT_NEW_LOCATION,
                MAX_TIME_WITHOUT_NEW_LOCATION, TimeUnit.MILLISECONDS);
        // try {
        // start = true;
        // handlerThread = new HandlerThread("GPS Thread");
        // handlerThread.start();
        // new Handler(handlerThread.getLooper()).post(controller);
        // } catch (Exception e) {
        // Log.e(TAG, "GpsSensor start failed", e);
        // }
        IntentFilter intentFilter = new IntentFilter(LOCATION_UPDATE_ACTION);
        getContext().registerReceiver(locationReceiver, intentFilter);
        Log.d(TAG, "Finished starting");
    }

    private void addLocationListenerWithAllProviders() {
        List<String> providers = locationManager.getAllProviders();
        Location location = null;
        if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            location = registerProvider(LocationManager.PASSIVE_PROVIDER);
        }
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            location = registerProvider(LocationManager.NETWORK_PROVIDER);
        }
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            location = registerProvider(LocationManager.GPS_PROVIDER);
        }

        if (location != null) {
            GpsData newData = new GpsData(location);
            currentData = newData;
        }
        // Very important flags
        lastLocationTime = System.currentTimeMillis();
        locationAdded = true;
        Log.d(TAG, "Finished adding listener");
    }

    private void removeLocationListener() {
        locationManager.removeUpdates(pendingIntent);
        locationAdded = false;
    }

    @Override
    public void stop() {
        super.stop();
        stpe.shutdown();
        removeLocationListener();
        getContext().unregisterReceiver(locationReceiver);
        // handlerThread.getLooper().quit();
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction.equalsIgnoreCase(LOCATION_UPDATE_ACTION)) {
                Location location = intent
                        .getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
                GpsData newData = null;
                if (location != null)
                    newData = new GpsData(location);
                currentData = newData;
                if (location != null) {
                    Log.i(TAG, "New location: " + location.toString());
                    Log.i(TAG, "With provider: " + location.getProvider());
                    lastLocationTime = newData.getTimestamp();
                }
            }

        }
    };

    private Runnable controller = new Runnable() {
        public void run() {
            // long timeElapsed;
            // if (start) {
            // addLocationListenerWithAllProviders();
            // start = false;
            // }
            // while (isSensing()) {
            // Log.d(TAG, "Sleeping " + MAX_TIME_WITHOUT_NEW_LOCATION);
            // try {
            // Thread.sleep(MAX_TIME_WITHOUT_NEW_LOCATION);
            // } catch (Exception e) {
            // Log.e(TAG, "GpsSensor run failed", e);
            // }
            long timeElapsed = System.currentTimeMillis() - lastLocationTime;

            Log.d(TAG, "Checked " + timeElapsed + " > "
                    + MAX_TIME_WITHOUT_NEW_LOCATION);
            if (timeElapsed > MAX_TIME_WITHOUT_NEW_LOCATION && locationAdded) {
                removeLocationListener();
                Log.d(TAG, "LocationListener removed: " + !locationAdded);
            } else if (timeElapsed > (MAX_TIME_WITHOUT_NEW_LOCATION + RESTART_TIME)
                    && !locationAdded) {
                addLocationListenerWithAllProviders();
                Log.d(TAG, "LocationListener added: " + locationAdded);
            }
            // }
            Log.d(TAG, "sensorController finished");
        }
    };

}
