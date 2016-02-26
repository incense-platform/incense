package edu.incense.android.sensor;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import edu.incense.android.datatask.data.AccelerometerFrameData;

/**
 * Implements SensorEventListener, receives accelerometer or gyroscope readings,
 * packs them into AccelerometerFrameData objects and makes them available for a
 * DataSource.
 * 
 * Update June/2012 Now it only handles frequencies provided by
 * android.hardware.SensorManager: SENSOR_DELAY_FASTEST, SENSOR_DELAY_GAME,
 * SENSOR_DELAY_NORMAL or SENSOR_DELAY_UI. SENSOR_DELAY_GAME is the default
 * value.
 * 
 * @author mxpxgx
 * @version 1.1, 05/09/2011
 */

public class AccelerometerSensor extends edu.incense.android.sensor.Sensor
        implements SensorEventListener {
    public static final String ATT_FRAMETIME = "frameTime";
    public static final String ATT_SENSOR_DELAY = "sensorDelay";
    public static final int MAX_FRAME_SIZE = 20;

    private static final String TAG = "AccelerometerSensor";

    /* Attributes needed to register SensorEventListener */
    private SensorManager sm;
    private Sensor accelerometer;
    private int sensorType;
    private int sensorDelay; // It can be set to android.hardware.SensorManager:
    // SENSOR_DELAY_FASTEST, SENSOR_DELAY_GAME, SENSOR_DELAY_NORMAL or
    // SENSOR_DELAY_UI.

    /* Attributes need to control sample rate */
    //private long lastTimestamp; // last reading time
    // private long wantedPeriod; // In nanoseconds

    /* Attributes needed to control frame times */
    private long frameTime; // frame time wanted, every frame will contain
    // readings for the length time specified by [frameTime].
    // [frameTime] is INDEPENDENT of the sample rate.

    // private long duration; // duration of reading wanted within a frame
    private long frameStartTime; // starting time of a frame
    private Queue<double[]> frame;

    public AccelerometerSensor(Context context, int sensorType, long frameTime,
            int sensorDelay) {
        super(context);
        setName("Accel");
        this.sensorType = sensorType;
        this.frameTime = frameTime;
        setSensorDelay(sensorDelay);
        // this.duration = duration;
        frameStartTime = System.currentTimeMillis();
        frame = new LinkedList<double[]>(); // Adding null elements to the
                                            // LinkedList implementation of
                                            // Queue should be prevented.

        // AccelerometerManager initialization
        String service = Context.SENSOR_SERVICE;
        sm = (SensorManager) context.getSystemService(service);
        accelerometer = sm.getDefaultSensor(sensorType);
        Log.d(TAG, "Sensor initialized: " + accelerometer.getName());
    }

    /**
     * Static method to construct an AccelerometerSensor with accelerometer
     * readings
     * 
     * @param context
     * @param frameTime
     * @return
     */
    public static AccelerometerSensor createAccelerometer(Context context,
            long frameTime, int sensorDelay) {
        AccelerometerSensor sensor = new AccelerometerSensor(context,
                Sensor.TYPE_ACCELEROMETER, frameTime, sensorDelay);
        sensor.setName("Accel");
        return sensor;
    }

    /**
     * Static method to construct an AccelerometerSensor with gyroscope readings
     * 
     * @param context
     * @param frameTime
     * @return
     */
    public static AccelerometerSensor createGyroscope(Context context,
            long frameTime, int sensorDelay) {
        AccelerometerSensor sensor = new AccelerometerSensor(context,
                Sensor.TYPE_GYROSCOPE, frameTime, sensorDelay);
        sensor.setName("Gyro");
        return sensor;
    }

    /**
     * NOTE: this method doesn't call super.start()
     * 
     * @see edu.incense.android.sensor.Sensor#start()
     */
    @Override
    public void start() {
        frameStartTime = System.currentTimeMillis();
        //lastTimestamp = 0;
        boolean success = false;
        success = sm.registerListener(this, accelerometer, getSensorDelay());

        if (success) {
            sensingNotification.updateNotificationWith(getName());
            Log.d(TAG, "SensingNotification updated");
            super.setSensing(true);
            Log.d(TAG, "SensorEventLister registered!");
        } else {
            super.setSensing(false);
            Log.d(TAG, "SensorEventLister NOT registered!");
        }
    }

    @Override
    public void stop() {
        sm.unregisterListener(this);
        Log.d(TAG, "SensorEventLister unregistered!");
        super.setSensing(false);
        super.stop();
    }

    /**
     * Stores new axis values in a AccelerometerData object.
     * 
     * @param newX
     * @param newY
     * @param newZ
     */
    long n = 100000;

    private void setNewReadings(double newX, double newY, double newZ,
            long timestamp) {

        long currentFrameTime = timestamp - frameStartTime;
        if (currentFrameTime > frameTime) {
            frame.offer(new double[] { newX, newY, newZ, timestamp });
            if (frame.size() >= this.getSampleFrequency()) {
                // Make available to DataSource
                currentData = createNewData();
            }
            frameStartTime = frameStartTime + frameTime;
        }
    }

    private AccelerometerFrameData createNewData() {
        double[][] doubleFrame = frame.toArray(new double[frame.size()][]);
        AccelerometerFrameData data = null;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            data = new AccelerometerFrameData(doubleFrame);
        } else {
            data = AccelerometerFrameData.createGyroFrameData(doubleFrame);
        }

        //if (frame.size() > 0) {
            //long time = (long) (doubleFrame[frame.size() - 1][3] - doubleFrame[0][3]);
            //int freq = computeFrameFrequency(time);
            // Log.d(TAG, "Accelerometer with time: " + time + "ms");
            // Log.d(TAG, "Accelerometer with frequency: " + freq + "Hz");
        //}
        frame.clear();
        return data;
    }

//    private int computeFrameFrequency(long frameTime) {
//        // Careful not to divide by zero
//        return (int) (frame.size() / (frameTime / 1000f));
//    }

    /* SensorEventListener methods */

    /**
     * Stores new accelerometer values when a change is sensed
     */
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == sensorType) {
            long currentTime = System.currentTimeMillis();
            double xAxis_lateralA = event.values[0];
            double yAxis_longitudinalA = event.values[1];
            double zAxis_verticalA = event.values[2];
            setNewReadings(xAxis_lateralA, yAxis_longitudinalA,
                    zAxis_verticalA, currentTime);
            //lastTimestamp = event.timestamp;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * @param sensorDelay
     *            the sensorDelay to set
     */
    public void setSensorDelay(int sensorDelay) {
        if (sensorDelay != SensorManager.SENSOR_DELAY_FASTEST
                || sensorDelay != SensorManager.SENSOR_DELAY_GAME
                || sensorDelay != SensorManager.SENSOR_DELAY_NORMAL
                || sensorDelay != SensorManager.SENSOR_DELAY_UI)
            // Set default
            sensorDelay = SensorManager.SENSOR_DELAY_GAME;
        this.sensorDelay = sensorDelay;
    }

    /**
     * @return the sensorDelay
     */
    public int getSensorDelay() {
        return sensorDelay;
    }

}
