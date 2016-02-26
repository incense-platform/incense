package edu.incense.android.datatask;

import java.util.ArrayList;
import java.util.List;

import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.model.TaskType;

import android.os.Looper;
import android.util.Log;

public abstract class DataTask implements Runnable {
    private final static String TAG = "DataTask";
    private final static int DEFAULT_PERIOD_TIME = 1000;
    protected List<Input> inputs;
    protected List<Output> outputs;
    private float sampleFrequency; // Sample frequency
    protected long periodTime; // Sleep time for each cycle (period time in
                               // milliseconds)
    private TaskType taskType;
    private Thread thread = null;
    private boolean running = false;
    private String name = null;
    private boolean triggered;

    /**
     * @return the isRunning
     */
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * @param running
     *            the isRunning to set
     */
    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public DataTask() {
        // thread = new Thread(this);
        running = false;
        triggered = false;
        setPeriodTime(DEFAULT_PERIOD_TIME);
    }

    protected void clearInputs() {
        if (inputs != null)
            inputs.clear();
        inputs = new ArrayList<Input>();
    }

    protected void clearOutputs() {
        if (outputs != null)
            outputs.clear();
        outputs = new ArrayList<Output>();
    }

    public void clear() {
        clearInputs();
        clearOutputs();
    }

    protected abstract void compute();

    /*** Inputs & Outputs ***/

    protected void addOutput(Output o) {
        if (outputs != null)
            outputs.add(o);
    }

    protected void addInput(Input i) {
        if (inputs != null)
            inputs.add(i);
    }

    protected void pushToOutputs(Data data) {
        if (outputs != null) {
            for (Output o : outputs) {
                o.pushData(data);
            }
        }
    }

    /*** Threads & Runnable ***/

    public void run() {
        Looper.prepare();
        while (isRunning()) {
            compute();
            if (getPeriodTime() > 1) {
                try {
                    Thread.sleep(getPeriodTime());
                } catch (Exception e) {
                    Log.e(TAG, "Sleep: " + e);
                }
            }
        }
    }

    public void start() {
        thread = new Thread(this);
        setRunning(true);
        thread.start();
        Log.d(TAG, getName() + " started");
    }

    public void stop() {
        setRunning(false);
        // try {
        // thread.join();
        // } catch (InterruptedException e) {
        // Log.e(TAG, "Task thread join failed", e);
        // }
        if (thread != null) {
            thread = null;
        }
    }

    /**
     * Computes the period time (milliseconds) based on a sample frequency in Hz
     * 
     * @param sampleFrequency
     * @return
     */
    private long computePeriodTime(float sampleFrequency) {
        long periodTime = (long) ((1.0f / sampleFrequency) * 1000f);
        return periodTime;
    }

    /**
     * Computes the sample frequency (Hz) based on a period time in milliseconds
     * 
     * @param periodTime
     * @return
     */
    private float computeSampleFrequency(float periodTime) {
        float sampleFrequency = (float) ((1f / periodTime) * 1000f);
        return sampleFrequency;
    }

    /* SETS AND GETS */

    public void setSampleFrequency(float sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
        periodTime = computePeriodTime(sampleFrequency);
    }

    public float getSampleFrequency() {
        return sampleFrequency;
    }

    public void setPeriodTime(long periodTime) {
        this.periodTime = periodTime;
        sampleFrequency = computeSampleFrequency(periodTime);
    }

    protected long getPeriodTime() {
        return periodTime;
    }

    /**
     * @param taskType
     *            the taskType to set
     */
    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    /**
     * @return the taskType
     */
    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param triggered
     *            the triggered to set
     */
    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    /**
     * @return the triggered
     */
    public boolean isTriggered() {
        return triggered;
    }
}
