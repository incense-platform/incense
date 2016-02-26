/**
 * 
 */
package edu.incense.android.datatask.filter;

//import android.util.Log;
import android.util.Log;
import edu.incense.android.datatask.data.Data;

/**
 * @author mxpxgx
 * 
 */
public class FalseTimerFilter extends DataFilter {
    private static final String TAG = "FalseTimerFilter";
    private static final String ATT_FALSEEVENT = "falseEvent";
    private long timeLength;
    private long startTime;
    private String attributeName;
    private boolean started;
    private long lastCheck;

    public FalseTimerFilter() {
        super();
        setFilterName("FalseTimerFilter");
        setTimeLength(5000L);
        startTime = System.currentTimeMillis();
        started = false;
        long lastCheck = System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.incense.android.datatask.DataTask#start()
     */
    @Override
    public void start() {
        super.start();
        startTime = System.currentTimeMillis();
        lastCheck = System.currentTimeMillis();
        Log.d(TAG, TAG + " started");
    }

    @Override
    protected void computeSingleData(Data data) {
        Data newData = seekForFalse(data);
        pushToOutputs(newData);
    }

    
    private Data seekForFalse(Data data) {
        boolean value = data.getExtras().getBoolean(attributeName);
        long sinceLastCheck = System.currentTimeMillis() - lastCheck;
        if((sinceLastCheck) > (this.getPeriodTime() * 2L)){
            started = false;
        }
        
        // Starting to notice "no movement"
        if (!value && !started) {
            startTime = System.currentTimeMillis();
            started = true;
        // Keep getting "no movement".
        } else if (!value && started) {
            long temp = System.currentTimeMillis() - startTime;
            if (temp > timeLength) {
                startTime = System.currentTimeMillis();
                started = false;
                // return true
                lastCheck = System.currentTimeMillis();
                data.getExtras().putBoolean(ATT_FALSEEVENT, true);
                return data;
            }
        // Movement detected
        } else {
            started = false;
        }
        lastCheck = System.currentTimeMillis();
        data.getExtras().putBoolean(ATT_FALSEEVENT, false);
        return data;
    }

    /**
     * @param timeLength
     *            the timeLength to set
     */
    public void setTimeLength(long timeLength) {
        this.timeLength = timeLength;
    }

    /**
     * @return the timeLength
     */
    public long getTimeLength() {
        return timeLength;
    }

    /**
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param attributeName
     *            the attributeName to set
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

}
