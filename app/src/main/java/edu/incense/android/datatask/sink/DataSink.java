package edu.incense.android.datatask.sink;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import edu.incense.android.datatask.DataTask;
import edu.incense.android.datatask.Input;
import edu.incense.android.datatask.InputEnabledTask;
import edu.incense.android.datatask.data.Data;

/**
 * Implements DataTask, receives data from outputs (filters, sensors, surveys)
 * and saves them with a SinkWritter.
 * 
 * The SinkWritter defines the method to save the received data to a specific
 * format. (Ex. JsonSinkWritter to write data to a JSON file)
 * 
 * NOTE: For audio files/data the AudioSink is used.
 * 
 * Update: June 12th/2012
 * Added attribute bufferSize, DEFAULT_MAX_SINK_SIZE is not longer needed.
 * With this change, one could add sinks with different buffer size in a project.
 * 
 * @author mxpxgx
 *
 */

public class DataSink extends DataTask implements InputEnabledTask {
    private final static String TAG = "DataSink";
    //private final static int DEFAULT_MAX_SINK_SIZE = 1;
    public static final String ATT_BUFFER_SIZE = "bufferSize";
    private final static long DEFAULT_DRAIN_TIME = 10000;//1L * 60L * 60L * 1000L; // each hour
    private long lastDrainTime;
    private int bufferSize;
    protected String name;
    protected List<Data> sink = null;

    protected SinkWritter sinkWritter;

    public DataSink(SinkWritter sinkWritter, int bufferSize) {
        this.sinkWritter = sinkWritter;
        inputs = new ArrayList<Input>();
        lastDrainTime = System.currentTimeMillis();
        this.setBufferSize(bufferSize);
        
        clear();
        initSinkList();
        setPeriodTime(5);
    }

    public void start() {
        super.start();
        initSinkList();
    }
    
    public void stop() {
        super.stop();
        clearOutputs();
        sinkWritter.writeSink(name, removeSink());
        Log.d(TAG, "Sink sent to writter with size: "+sink.size());
    }

    protected void clearOutputs() {
        // No outputs for DataSink
        // outputs.removeAll(outputs);
        outputs = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.incense.datatask.DataTask#compute() Collects the most
     * recent data from all inputs in a "sink" (List)
     */
    @Override
    protected void compute() {
        Data latestData = null;
        for (Input i : inputs) {
            do {
                long timeSinceLastDrain = System.currentTimeMillis() - lastDrainTime;
                latestData = i.pullData();
                if (latestData != null) {
                    sink.add(latestData);
                    //Log.d(TAG, "Data added to sink!");
                    if(sink.size() >= getBufferSize() || 
                            timeSinceLastDrain >= DEFAULT_DRAIN_TIME){
                        drain();
                    }
                } else {
//                    Log.d(TAG, "Data NOT added to sink!");
                }
            } while (latestData != null);
        }
    }

    public List<Data> getSink() {
        return sink;
    }
    
    private void drain(){
        //Log.d(TAG, "Trying to write data...");
        sinkWritter.writeSink(name, removeSink());
        lastDrainTime = System.currentTimeMillis();
    }
    
    /**
     * @param sink the sink to set
     */
    public void setSink(List<Data> sink) {
        this.sink = sink;
    }

    public List<Data> removeSink() {
        List<Data> temp = getSink();
        initSinkList();
        return temp;
    }

    private void initSinkList() {
        sink = new ArrayList<Data>();
    }

    @Override
    public void addInput(Input i) {
        super.addInput(i);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * The minimum value for bufferSize is 1.
     * @param bufferSize the bufferSize to set
     */
    public void setBufferSize(int bufferSize) {
        if(bufferSize<=0)
            bufferSize = 1;
        this.bufferSize = bufferSize;
    }

    /**
     * @return the bufferSize
     */
    public int getBufferSize() {
        return bufferSize;
    }

}
