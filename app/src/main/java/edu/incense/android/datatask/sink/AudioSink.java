/**
 * 
 */
package edu.incense.android.datatask.sink;

import android.util.Log;
import edu.incense.android.datatask.Input;
import edu.incense.android.datatask.data.AudioData;
import edu.incense.android.datatask.data.Data;

/**
 * @author mxpxgx
 * 
 */
public class AudioSink extends DataSink {
    private final static String TAG = "AudioSink";
    private final static long MAX_TIME_WITHOUT_AUDIO = 2000;
    private long lastDataTime;
    private RawAudioSinkWritter sinkWritter;
    private boolean started;

    /**
     * @param sinkWritter
     */
    public AudioSink(RawAudioSinkWritter sinkWritter) {
        super(sinkWritter, 1);
        this.sinkWritter = sinkWritter;
        lastDataTime = System.currentTimeMillis() + MAX_TIME_WITHOUT_AUDIO; // TODO
                                                                            // not
                                                                            // sure
                                                                            // about
                                                                            // this
                                                                            // addition
        started = false;
    }

    // protected void compute2() {
    // Data latestData = null;
    // for (Input i : inputs) {
    // do {
    // latestData = i.pullData();
    // if (latestData != null) {
    // // Log.d(TAG, "Audio frame received");
    // sink.add(latestData);
    // lastDataTime = System.currentTimeMillis();
    // } else {
    // // Log.d(TAG, "Data NOT added to sink!");
    // }
    // } while (latestData != null);
    // }
    // long timeLength = System.currentTimeMillis() - lastDataTime;
    // // Log.d(TAG, "Comparing: "+timeLength+" >= "+MAX_TIME_WITHOUT_AUDIO);
    // if(sink !=null && sink.size() > 0 && timeLength >=
    // MAX_TIME_WITHOUT_AUDIO){
    // Log.d(TAG, "Sink sent to writter with size: "+sink.size());
    // sinkWritter.writeSink(name, removeSink());
    // }
    // }

    @Override
    protected void compute() {
        try {
            Data latestData = null;
            for (Input i : inputs) {
                latestData = i.pullData();
                if (latestData != null) {
                    if (!started) {
                        sinkWritter.start(name);
                        started = true;
                    }
                    if (started) {
                        // Log.d(TAG, "Audio frame received");
                        sinkWritter.writeFrame((AudioData) latestData);
                        lastDataTime = System.currentTimeMillis();
                    }
                }
            }
            long timeLength = System.currentTimeMillis() - lastDataTime;
            // Log.d(TAG,
            // "Comparing: "+timeLength+" >= "+MAX_TIME_WITHOUT_AUDIO);
            if (timeLength >= MAX_TIME_WITHOUT_AUDIO && started) {
                // Log.d(TAG, "Sink sent to writter with size: "+sink.size());
                started = false;
                sinkWritter.stop();
            }
        } catch (Exception e) {
            Log.e(TAG, "Sink problem: ", e);
        }
    }

}
