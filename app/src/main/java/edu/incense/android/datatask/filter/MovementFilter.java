/**
 * 
 */
package edu.incense.android.datatask.filter;

import android.util.Log;
import edu.incense.android.datatask.data.AccelerometerFrameData;
import edu.incense.android.datatask.data.Data;

/**
 * @author mxpxgx
 * 
 */
public class MovementFilter extends DataFilter {
    private static final String TAG = "MovementFilter";
    public static final String ATT_ISMOVEMENT = "isMovement";
    private float movementThreshold;

    public MovementFilter() {
        super();
        setFilterName(this.getClass().getName());
        movementThreshold = 0.3f;
    }

    /**
     * @return the movementThreshold
     */
    public float getMovementThreshold() {
        return movementThreshold;
    }

    /**
     * @param movementThreshold the movementThreshold to set
     */
    public void setMovementThreshold(float movementThreshold) {
        this.movementThreshold = movementThreshold;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void computeSingleData(Data data) {
        Data newData = seekForMovement(data);
        pushToOutputs(newData);
    }

    private Data seekForMovement(Data data) {
        AccelerometerFrameData accData = (AccelerometerFrameData) data;
        double[][] frame = accData.getFrame();
        accel[0]=frame[0][0];
        accel[1]=frame[0][1];
        accel[2]=frame[0][2];
        boolean movement = false;
        Log.i(TAG, "Frame size: " + accData.getFrame().length);
        for (int i = 0; i < accData.getFrame().length; i++) {
            double[] result = filterSample(frame[i]);
            movement = seekForMovement(result);
            if (movement) {
                data.getExtras().putBoolean(ATT_ISMOVEMENT, true);
                return data;
            }
        }
        data.getExtras().putBoolean(ATT_ISMOVEMENT, movement);
        return data;
    }

    private double[] accel = { 0, 0, 0 };

    private double[] filterSample(double[] data) {
//        Log.i(TAG, "accelx: "+accel[0]+", accely: "+accel[1]+", accelz: "+accel[2]);
        // ramp-speed - play with this value until satisfied
        final float kFilteringFactor = 0.1f;

        // last result storage - keep definition outside of this function, eg.
        // in wrapping object

        // acceleration.x,.y,.z is the input from the sensor

        // result.x,.y,.z is the filtered result

        // high-pass filter to eleminate gravity
        accel[0] = data[0] * kFilteringFactor + accel[0]
                * (1.0f - kFilteringFactor);
        accel[1] = data[1] * kFilteringFactor + accel[1]
                * (1.0f - kFilteringFactor);
        accel[2] = data[2] * kFilteringFactor + accel[2]
                * (1.0f - kFilteringFactor);
        double[] result = new double[3];
        result[0] = data[0] - accel[0];
        result[1] = data[1] - accel[1];
        result[2] = data[2] - accel[2];
        return result;
    }

    private boolean seekForMovement(double[] data) {
//        Log.i(TAG, "x: "+data[0]+", y: "+data[1]+", z: "+data[2]);
//        Log.i(TAG, "Threshold: " + movementThreshold);
        if (Math.abs(data[0]) > movementThreshold || Math.abs(data[1]) > movementThreshold
                || Math.abs(data[2]) > movementThreshold) {
//            Log.i(TAG, "It's true");
            return true;
        } else {
//            Log.i(TAG, "It's false");
            return false;
        }
    }
}
