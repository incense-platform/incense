/**
 * 
 */
package edu.incense.android.sensor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;
import edu.incense.android.datatask.DataSource;
import edu.incense.android.datatask.data.others.BooleanData;

/**
 * @author mxpxgx
 * 
 */
public class TimerSensor extends Sensor {
    private final static String TAG = "TimerSensor";
    private ScheduledThreadPoolExecutor stpe;
    private long period;
    private boolean firstEventRan = false;
    private DataSource dataSource;
    
    /**
     * @param context
     */
    public TimerSensor(Context context, long period) {
        super(context);
        setName("Timer");
        this.period = period;
    }

    /**
     * @return the period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * @param period
     *            the period to set
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * @see edu.incense.android.sensor.Sensor#start()
     */
    @Override
    public synchronized void start() {
        super.start();
        this.currentData =  null;
        firstEventRan = false;
        stpe = new ScheduledThreadPoolExecutor(1);
//        stpe.scheduleAtFixedRate(timerRunnable, 0, period,
//                TimeUnit.MILLISECONDS);
        stpe.schedule(timerRunnable, period,
                TimeUnit.MILLISECONDS);
        Log.d(TAG, TAG + " started");
//        Log.d(TAG, "Period: "+period+" ms");
    }

    /**
     * @see edu.incense.android.sensor.Sensor#stop()
     */
    @Override
    public synchronized void stop() {
        super.stop();
        stpe.shutdown();
        Log.d(TAG, TAG + " stopped");
    }

    Runnable timerRunnable = new Runnable() {
        public void run() {
//            if (!firstEventRan) {
//                firstEventRan = true;
//                TimerSensor.this.currentData =  null;
//                Log.d(TAG, "First execution");
//            } else {
                TimerSensor.this.currentData = new BooleanData(true);
                Log.d(TAG, "Timer event");
                dataSource.stop();
//            }
        }
    };
    
    public void addSourceTask(DataSource ds){
        dataSource = ds;
    }

}
