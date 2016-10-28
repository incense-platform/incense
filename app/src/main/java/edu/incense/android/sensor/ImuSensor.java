package edu.incense.android.sensor;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.lang.Math;

import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.ImuData;
import edu.incense.android.datatask.data.OrientationData;

/**
 * Created by xilef on 10/28/2016.
 */
public class ImuSensor extends Sensor
    implements SensorEventListener
{
    private static final String TAG = "ImuSensor";
    private SensorManager sm; // This is used to retrieve instances of the sensors
    private android.hardware.Sensor magnetometer; // This is the variable that holds the sensor that's going to be used
    private android.hardware.Sensor accelerometer;
    private android.hardware.Sensor gyroscope;
    private android.hardware.Sensor orientation;
    private int sensorType; // Establishes the type of sensor that's going to be used.

    private int sampleRate; // The rate the sensor is going to offer data.
    private Queue<ImuData> sensedData;

    private float[] accelerometerReading = null;
    private float[] magnetometerReading = null;
    private float[] gyroscopeReading = null;
    private float[] orientationReading = null;
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];

    /**
     * Creates a new instance of the OrientationSensor class.
     * @param context: The context in which this class is being executed.
     */
    public ImuSensor(Context context){
        super(context);

        this.setName(TAG);
        sampleRate = SensorManager.SENSOR_DELAY_NORMAL;
        sensedData = new LinkedList<ImuData>();

        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sm.getDefaultSensor(android.hardware.Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sm.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        gyroscope = sm.getDefaultSensor(android.hardware.Sensor.TYPE_GYROSCOPE);
        //orientation = sm.getDefaultSensor(android.hardware.Sensor.TYPE_ORIENTATION);
        Log.d(TAG, "Sensor initialized: " + this.getName());
    }

    // Obtains the sample rate that is currently set. This uses values from the
    // android.hardware.SensorManager class.
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Sets the sample rate that the gyroscope is going to use to retrieve data from the sensor.
     * This have to be set before this class starts to sense.
     * @param sampleRate: One of the values that can be established through the SensorManager class.
     *                  SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI, SENSOR_DELAY_GAME, SENSOR_DELAY_FASTEST.
     */
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * Obtains the current sample rate used to retrieve data from the sensor
     * @return One of the values that can be established through the SensorManager class.
     *         SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI, SENSOR_DELAY_GAME, SENSOR_DELAY_FASTEST.
     */
    @Override
    public float getSampleFrequency() {
        return this.getSampleRate();
    }

    /**
     * Sets the sample rate used to obtain data from the sensor.
     * @param sampleFrequency One of the values that can be established through the SensorManager class.
     *           SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI, SENSOR_DELAY_GAME, SENSOR_DELAY_FASTEST.
     */
    @Override
    public void setSampleFrequency(float sampleFrequency){
        super.setSampleFrequency(sampleFrequency);
        this.setSampleRate((int)sampleFrequency);
    }

    /**
     * This function is used to register this class as the class that will receive all the sensor
     * notifications.
     * @return: returns false if the listener could not be registered successfully.
     */
    private boolean registerSensor(){
        boolean successMag = sm.registerListener(this, magnetometer, this.sampleRate);
        boolean successAccel = sm.registerListener(this, accelerometer, this.sampleRate);
        boolean successGyro = sm.registerListener(this, gyroscope, this.sampleRate);
        //boolean successOrient = sm.registerListener(this, orientation, this.sampleRate);
        if (successMag && successAccel && successGyro){
            sensingNotification.updateNotificationWith(getName());
            Log.d(TAG, "SensingNotification updated");
            super.setSensing(true);
            Log.d(TAG, "SensorEventLister registered!");
        }
        else{
            super.setSensing(false);
            Log.d(TAG, "SensorEventLister NOT registered!");
        }
        return successMag && successAccel && successGyro;
    }

    /**
     * This function is used to unregister the listener.
     */
    private void unregisterSensor(){
        sm.unregisterListener(this, magnetometer);
        sm.unregisterListener(this, accelerometer);
        sm.unregisterListener(this, gyroscope);
        //sm.unregisterListener(this, orientation);
        super.setSensing(false);
        Log.d(TAG, "SensorEventLister unregistered!");
    }

    /**
     * This function is called from the DataSource class which is called by the SessionController, so
     * this starts to retrieve data from the sensor.
     */
    @Override
    public synchronized void start() {
        registerSensor();
    }

    /**
     * This function is called from the DataSource class which is called by the SessionContoller, so
     * this class stops registering data from the sensor.
     */
    @Override
    public synchronized void stop() {
        unregisterSensor();
    }

    /**
     * This function overrides the getData function form the Sensor abstract class so the data
     * retrieved by this class is returned to the next filter in the order of a queue.
     * @return returns the data that is in the first place of the queue.
     */
    @Override
    public Data getData(){
        return sensedData.poll();
    }

    /**
     * This is not used. It was declared because it is required by the SensorEventListener interface.
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

    }

    /**
     * This function is in charge of receive all the notifications generated from the event.
     * @param event: The event parameter contains the values generated by the sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == android.hardware.Sensor.TYPE_MAGNETIC_FIELD){
            magnetometerReading = new float[3];
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
        }

        if (event.sensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER){
            accelerometerReading = new float[3];
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
        }

        if (event.sensor.getType() == android.hardware.Sensor.TYPE_GYROSCOPE){
            gyroscopeReading = new float[3];
            System.arraycopy(event.values, 0, gyroscopeReading, 0, gyroscopeReading.length);
        }

//        if (event.sensor.getType() == android.hardware.Sensor.TYPE_ORIENTATION){
//            orientationReading = new float[3];
//            System.arraycopy(event.values, 0, orientationReading, 0, orientationReading.length);
//        }

//        if (gyroscopeReading != null && accelerometerReading != null && orientationReading != null){
//            ImuData imu = new ImuData(accelerometerReading[0], accelerometerReading[1], accelerometerReading[2],
//                        gyroscopeReading[0], gyroscopeReading[1], gyroscopeReading[2],
//                        orientationReading[1], orientationReading[2], orientationReading[0]);
//
//                sensedData.add(imu);
//
//            magnetometerReading = null;
//            accelerometerReading = null;
//            orientationReading = null;
//
//        }

        if (magnetometerReading != null && accelerometerReading != null) {
            sm.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
            sm.getOrientation(rotationMatrix, orientationAngles);

            if (gyroscopeReading != null){
                ImuData imu = new ImuData(accelerometerReading[0], accelerometerReading[1], accelerometerReading[2],
                        gyroscopeReading[0], gyroscopeReading[1], gyroscopeReading[2],
                        (float)Math.toDegrees(orientationAngles[1]), (float)Math.toDegrees(orientationAngles[2]), (float)Math.toDegrees(orientationAngles[0]));

                sensedData.add(imu);

                magnetometerReading = null;
                accelerometerReading = null;
                gyroscopeReading = null;
            }

            rotationMatrix = new float[9];
            orientationAngles = new float[3];
        }
    }
}
