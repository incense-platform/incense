package edu.incense.android.datatask;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;
import edu.incense.android.datatask.data.AudioData;
import edu.incense.android.sensor.AudioSensor;

public class AudioDataSource extends DataTask implements OutputEnabledTask {
    private final static String TAG = "AudioDataSource";
    private AudioSensor sensor;
    private Object mutex = new Object();
    private long duration;
    private long startTime;

    public AudioDataSource(AudioSensor sensor, long duration) {
        super();
        this.sensor = sensor;
        this.duration = duration;
        outputs = new ArrayList<Output>();
        clear();
    }

    private ByteArrayOutputStream baos;
    // private BufferedOutputStream bos;
    private DataOutputStream dos;
    private int baosCurrentPosition = 0;
    private byte[] bufferArray = null;
    private byte[] tempArray;

    @Override
    public void start() {
        baosCurrentPosition = 0;
        Log.d(TAG, "Starting...");
        startTime = System.currentTimeMillis();
        // File file = new File("/sdcard/audio.raw");
        // if (file.exists()) {
        // file.delete();
        // }
        // file.createNewFile();
        // bos = new BufferedOutputStream(new FileOutputStream(file));

        sensor.start();
        baos = new ByteArrayOutputStream(sensor.getBufferSize());
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        dos = new DataOutputStream(bos);
        super.start();
    }

    @Override
    public void stop() {
        // Log.d(TAG, "Stoping AudioDataSource...");
        super.stop();
        // Log.d(TAG, "Stoping AudioSensor...");
        sensor.stop();
        // Log.d(TAG, "Stoped AudioSensor...");
        try {
            dos.flush();
            dos.close();
            // bos.close();
        } catch (IOException e) {
            Log.e(TAG, "Stoping AudioDataSource failed", e);
        }
    }

    public void setSampleFrequency(float sampleFrequency) {
        sensor.setSampleFrequency(sampleFrequency);
        super.setSampleFrequency(sampleFrequency * 2 * 2);
    }

    protected void clearInputs() {
        // No inputs for DataSource
        inputs = null;
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {

                compute();
                // if (getPeriodTime() > 1) {
                // Thread.sleep(getPeriodTime());
                // }
            } catch (Exception e) {
                Log.e(TAG, "Running failed: " + e);
            }
        }
    }

    @Override
    protected void compute() {
        try {
            Thread.sleep(1000);

            synchronized (mutex) {
                bufferArray = baos.toByteArray();

                // if (bufferArray.length > baosCurrentPosition) {
                if (bufferArray.length > 0) {
                    tempArray = new byte[bufferArray.length
                            - baosCurrentPosition];
                    System.arraycopy(bufferArray, 0, tempArray, 0,
                            tempArray.length);
                    // System.arraycopy(bufferArray, baosCurrentPosition,
                    // tempArray, 0,
                    // tempArray.length);
                    AudioData newData = new AudioData();
                    newData.setAudioFrame(tempArray);
                    // newData.setAudioFrame(bufferArray);
                    // baosCurrentPosition = bufferArray.length;
                    if (newData != null) {
                        this.pushToOutputs(newData);
                        // Log.d(TAG, "Pushed new audio data");
                    }
                    baos.reset();

                    // System.gc();
                    // System.runFinalization();
                    // System.gc();
                    // else {
                    // // Log.i(getClass().getName(), "NO DATA");
                    // }
                }
            }

            if (duration >= 0) {
                long timeRunning = System.currentTimeMillis() - startTime;
                // Log.d(TAG, "Comparing "+timeRunning+" >= "+duration);
                if (this.isRunning() && timeRunning >= duration) {
                    // Log.d(TAG, "Stoping "+timeRunning+" >= "+duration);
                    this.stop();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Buffered failed", e);
        }
    }

    public void pushDataToBuffer(short[] tempBuffer, int bufferSize) {
        if (!isRunning())
            return;
        synchronized (mutex) {
            try {
                for (int i = 0; i < bufferSize; i++) {
                    dos.writeShort(tempBuffer[i]);
                }
                // if(dos.size()%20 < 1 || dos.size()%20 == 0){
                // Log.e(TAG, "New buffered received");
                // }

            } catch (IOException e) {
                Log.e(TAG, "Writing RAW audio file failed", e);
            }

            // AudioData newData = new AudioData();
            // newData.setAudioFrame(tempBuffer);
            // pushToOutputs(newData);
        }
    }

    @Override
    public void addOutput(Output o) {
        super.addOutput(o);
    }

}
