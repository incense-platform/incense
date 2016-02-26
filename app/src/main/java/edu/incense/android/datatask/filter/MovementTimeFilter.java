/**
 * 
 */
package edu.incense.android.datatask.filter;

//import android.util.Log;
import android.util.Log;
import edu.incense.android.datatask.Input;
import edu.incense.android.datatask.data.AccelerometerData;
import edu.incense.android.datatask.data.Data;

/**
 * 
 * This object does the following:
 * If more than X seconds without movement, output false (to stop Accelerometer)
 * If more than Y seconds without input, output true (to start Accelerometer)
 * 
 * @author mxpxgx
 * 
 */
public class MovementTimeFilter extends DataFilter {
    private static final String TAG = "MovementTimeFilter";
    private static final String ATT_MOVTIMEEVENT = "moveTimeEvent";
    private long lastMovement;
    private long lastInput;
    private long maxNoMovement;
    private long maxNoInput;

    public MovementTimeFilter() {
        super();
        setFilterName("FalseTimerFilter");
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.incense.android.datatask.DataTask#start()
     */
    @Override
    public void start() {
        super.start();
        long now = System.currentTimeMillis();
        lastMovement = now;
        lastInput = now;
        Log.d(TAG, TAG + " started");
    }

    @Override
    protected void compute() {
        Data tempData;
        for (Input i : inputs) {
            // Log.i(getClass().getName(), "Asking for new data");
            tempData = i.pullData();
            if (tempData != null) {
                computeSingleData(tempData);
                // Log.i(getClass().getName(), "GOOD");
            } else {
                checkTimeWithNoInput();
                // Log.i(getClass().getName(), "BAD");
            }
        }
    }

    /**
     * If more than Y (maxNoInput) seconds without input, output true (to start
     * Accelerometer)
     */
    private void checkTimeWithNoInput() {
        long now = System.currentTimeMillis();
        long sinceLastInput = now - lastInput;
        if (sinceLastInput >= maxNoInput) {
            // Not really but it's needed to restart the no movement time count
            // down.
            lastMovement = now;
            Data newData = new AccelerometerData(0, 0, 0);
            pushToOutputs(returnDataWith(newData, true));
        }
    }

    @Override
    protected void computeSingleData(Data data) {
        Data newData = processInput(data);
        pushToOutputs(newData);
    }

    private Data processInput(Data data) {
        boolean movement = data.getExtras().getBoolean(
                MovementFilter.ATT_ISMOVEMENT);
        long now = System.currentTimeMillis();
        long sinceLastMovement = now - lastMovement;
        lastInput = now;

        if (movement) {
            lastMovement = now;
            return returnDataWith(data, true);
        } else {
            if (sinceLastMovement >= maxNoMovement) {
                return returnDataWith(data, false);
            }
            return returnDataWith(data, true);
        }
    }

    public Data returnDataWith(Data data, boolean value) {
        data.getExtras().putBoolean(ATT_MOVTIMEEVENT, value);
        return data;
    }

    /**
     * @return the maxNoMovement
     */
    public long getMaxNoMovement() {
        return maxNoMovement;
    }

    /**
     * @param maxNoMovement
     *            the maxNoMovement to set
     */
    public void setMaxNoMovement(long maxNoMovement) {
        this.maxNoMovement = maxNoMovement;
    }

    /**
     * @return the maxNoInput
     */
    public long getMaxNoInput() {
        return maxNoInput;
    }

    /**
     * @param maxNoInput
     *            the maxNoInput to set
     */
    public void setMaxNoInput(long maxNoInput) {
        this.maxNoInput = maxNoInput;
    }

}
