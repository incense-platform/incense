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
 * @author mxpxgx
 * @version 1.1, 05/09/2011
 */

public class AccelerometerSensor_old extends edu.incense.android.sensor.Sensor
        implements SensorEventListener {
    public static final String ATT_FRAMETIME = "frameTime";
    public static final String ATT_DURATION = "duration";
    public static final int MAX_FRAME_SIZE = 20;

    private static final String TAG = "AccelerometerSensor";

    /* Attributes needed to register SensorEventListener */
    private SensorManager sm;
    private Sensor accelerometer;
    private int sensorType;

    /* Attributes need to control sample rate */
    private long lastTimestamp; // last reading time
    private long wantedPeriod; // In nanoseconds

    /* Attributes needed to control frame times */
    private long frameTime; // frame time wanted
    private long duration; // duration of reading wanted within a frame
    private long frameStartTime; // starting time of a frame
    private Queue<double[]> frame;

    public AccelerometerSensor_old(Context context, int sensorType, long frameTime,
            long duration) {
        super(context);
        setName("Accel");
        this.sensorType = sensorType;
        this.frameTime = frameTime;
        this.duration = duration;
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
    public static AccelerometerSensor_old createAccelerometer(Context context,
            long frameTime, long duration) {
        AccelerometerSensor_old sensor = new AccelerometerSensor_old(context,
                Sensor.TYPE_ACCELEROMETER, frameTime, duration);
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
    public static AccelerometerSensor_old createGyroscope(Context context,
            long frameTime, long duration) {
        AccelerometerSensor_old sensor = new AccelerometerSensor_old(context,
                Sensor.TYPE_GYROSCOPE, frameTime, duration);
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
        if (duration > frameTime) {
            duration = frameTime;
        }
        if (this.getPeriodTime() > duration) {
            this.setPeriodTime(duration);
        }
        wantedPeriod = getPeriodTime() * 1000000L; // milliseconds to
                                                   // nanoseconds?
        if (getSampleFrequency() == 44 || getSampleFrequency() == 4) {
            wantedPeriod = 1000000L;
        }
        frameStartTime = System.currentTimeMillis();
        lastTimestamp = 0;
        boolean success = false;
        if (this.getSampleFrequency() > 4) {
            success = sm.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        } else {
            success = sm.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

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
            frameStartTime = frameStartTime + frameTime;
        }

        if (currentFrameTime <= duration) {
            frame.offer(new double[] { newX, newY, newZ, timestamp });
            if (frame.size() >= this.getSampleFrequency()) {
                // Make available to DataSource
                currentData = createNewData();
            }
        } else if (!frame.isEmpty()) {
            // Make available to DataSource
            currentData = createNewData();
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

        if (frame.size() > 0) {
            long time = (long) (doubleFrame[frame.size() - 1][3] - doubleFrame[0][3]);
            int freq = computeFrameFrequency(time);
            autoFixFrequency(freq);
            // Log.d(TAG, "Accelerometer with time: " + time + "ms");
            // Log.d(TAG, "Accelerometer with frequency: " + freq + "Hz");
        }
        frame.clear();
        return data;
    }

    private int computeFrameFrequency(long frameTime) {
        // Careful not to divide by zero
        return (int) (frame.size() / (frameTime / 1000f));
    }

    private void autoFixFrequency(int freq) {

        // Auto regulate frequency
        if (freq != getSampleFrequency() && getSampleFrequency() != 44
                || getSampleFrequency() != 4) {
            long e = 2;
            if (freq > (this.getSampleFrequency() + e)) {
                wantedPeriod = wantedPeriod < (n * 1000L) ? wantedPeriod + n
                        : wantedPeriod;
            } else if (freq < (this.getSampleFrequency() - e)) {
                wantedPeriod = wantedPeriod > n ? wantedPeriod - n
                        : wantedPeriod;
            } else if (n > 100) {
                n = n / 10;
            }
        }
    }

    /* SensorEventListener methods */

    /**
     * Stores new accelerometer values when a change is sensed
     */
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == sensorType) {
            long currentTime = System.currentTimeMillis();
            long period = event.timestamp - lastTimestamp;
            // Log.d(TAG,
            // "Substracting: "+event.timestamp+" - "+lastTimestamp+" = "+period);
            // Log.d(TAG, "Comparing: "+period+" >= "+wantedPeriod);
            if (period >= wantedPeriod) {
                double xAxis_lateralA = event.values[0];
                double yAxis_longitudinalA = event.values[1];
                double zAxis_verticalA = event.values[2];
                setNewReadings(xAxis_lateralA, yAxis_longitudinalA,
                        zAxis_verticalA, currentTime);
                lastTimestamp = event.timestamp;
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
