package edu.incense.android.sensor;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.AccelerometerData;

/**
 * Created by xilef on 10/28/2016.
 */
public class BareAccelerometerSensor extends Sensor
    implements SensorEventListener
{
    private static final String TAG = "AccelerometerSensor";
    private SensorManager sm; // This is used to retrieve instances of the sensors
    private android.hardware.Sensor accelerometer; // This is the variable that holds the sensor that's going to be used
    private int sensorType; // Establishes the type of sensor that's going to be used.

    private int sampleRate; // The rate the sensor is going to offer data.
    private Queue<AccelerometerData> sensedData;

    // This is the constructor of this class. Creates an instance of this class.
    public BareAccelerometerSensor(Context context){
        super(context);
        this.setName("BareAccelerometer");
        this.sensorType = android.hardware.Sensor.TYPE_ACCELEROMETER;
        this.sampleRate = SensorManager.SENSOR_DELAY_NORMAL;
        this.sensedData = new LinkedList<AccelerometerData>();

        // Accelerometer initialization.
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(sensorType);
        Log.d(TAG, "Sensor initialized: " + accelerometer.getName());
    }

    // Obtains the sample rate that is currently set. This uses values from the
    // android.hardware.SensorManager class.
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Sets the sample rate that the accelerometer is going to use to retrieve data from the sensor.
     * This have to be set before this class starts to sense.
     * @param sampleRate: One of the values that can be established through the SensorManager class.
     *                  SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI, SENSOR_DELAY_GAME, SENSOR_DELAY_FASTEST.
     */
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * This function is used to register this class as the class that will receive all the sensor
     * notifications.
     * @return returns false if the listener could not be registered successfully.
     */
    private boolean registerSensor(){
        boolean success = sm.registerListener(this, accelerometer, this.sampleRate);
        if (success){
            sensingNotification.updateNotificationWith(getName());
            Log.d(TAG, "SensingNotification updated");
            super.setSensing(true);
            Log.d(TAG, "SensorEventLister registered!");
        }
        else{
            super.setSensing(false);
            Log.d(TAG, "SensorEventLister NOT registered!");
        }
        return success;
    }

    /**
     * This function is used to unregister the listener.
     */
    private void unregisterSensor(){
        sm.unregisterListener(this, accelerometer);
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
     * This function is in charge of receive all the notifications generated from the event.
     * @param event: The event parameter contains the values generated by the sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()== this.sensorType) {
            AccelerometerData data = new AccelerometerData(event.values[0], event.values[1], event.values[2]);
            sensedData.add(data);
        }
    }

    /**
     * This is not used. It was declared because it is required by the SensorEventListener interface.
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

    }

}
