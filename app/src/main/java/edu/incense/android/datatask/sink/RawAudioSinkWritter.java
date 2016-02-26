package edu.incense.android.datatask.sink;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import edu.incense.android.datatask.data.AudioData;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.DataType;
import edu.incense.android.results.QueueFileTask;
import edu.incense.android.results.ResultFile;
import edu.incense.android.results.ResultsUploader;

public class RawAudioSinkWritter implements SinkWritter {
    private static final String TAG = "RawAudioSinkWritter";
    private Context context;
    private ResultFile resultFile;
//    private BufferedOutputStream bos;
    private DataOutputStream dos;

    public RawAudioSinkWritter(Context context) {
        this.context = context;
    }

    public void writeSink(String name, List<Data> sink) {
//        ResultFile resultFile = ResultFile.createAudioInstance(context, name);
//        try {
//            // Create a new output file stream that's private to this
//            // application.
//            Log.d(TAG, "Saving to file: " + resultFile.getFileName());
//            File file = new File(resultFile.getFileName());
//            // FileOutputStream fos = context.openFileOutput(
//            // resultFile.getFileName(), Context.MODE_PRIVATE);
//            BufferedOutputStream bos = new BufferedOutputStream(
//                    new FileOutputStream(file));
//            DataOutputStream dos = new DataOutputStream(bos);
//
//            AudioData ad;
//            // StringBuilder sb;
//
//            for (Data d : sink) {
//                if (d.getDataType() == DataType.AUDIO) {
//                    ad = (AudioData) d;
//                    byte[] buffer = ad.getAudioFrame();
//                    // Write whole frame
//
//                    // sb = new StringBuilder();
//                    for (int i = 0; i < buffer.length; i++) {
//                        dos.writeByte(buffer[i]);
//                        // sb.append(buffer[i]+" ");
//                    }
//                    // Log.d(TAG, "["+sb.toString() +"]");
//                }
//            }
//
//            dos.flush();
//            dos.close();
//            bos.close();
//
//            // Toast.makeText(context, "Application saved: "+sink.size() +
//            // " stream",
//            // Toast.LENGTH_LONG).show();
//
//        } catch (IOException e) {
//            Log.e(TAG, "Writing RAW audio file failed", e);
//        }
//        // (new QueueFileTask(context)).execute(resultFile);
//        queueFileTask(resultFile);
//        System.gc();
//        // System.runFinalization();
//        // System.gc();
    }

    public void start(String name) {
        resultFile = ResultFile.createAudioInstance(context, name);
        // Create a new output file stream that's private to this
        // application.
        Log.d(TAG, "Saving to file: " + resultFile.getFileName());
        try {
            File file = new File(resultFile.getFileName());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            dos = new DataOutputStream(bos);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Initializing writing streams failed", e);
        }
    }

    public void stop() {
        try {
            dos.flush();
            dos.close();
//            bos.close();
        } catch (IOException e) {
            Log.e(TAG, "Closing writing streams failed", e);
        }
        // (new QueueFileTask(context)).execute(resultFile);
        queueFileTask(resultFile);
//        System.gc();
//        System.runFinalization();
//        System.gc();
        Log.d(TAG, "File ["+ resultFile.getFileName()+"] closed.");
    }

    public void writeFrame(AudioData d) {
        AudioData ad;

        if (d.getDataType() == DataType.AUDIO) {
            ad = (AudioData) d;
            byte[] buffer = ad.getAudioFrame();
            // Write whole frame

            // sb = new StringBuilder();
            for (int i = 0; i < buffer.length; i++) {
                try {
                    dos.writeByte(buffer[i]);
                } catch (IOException e) {
                    Log.d(TAG, "Writing to file streams failed", e);
                }
                // sb.append(buffer[i]+" ");
            }
//            System.gc();
            // Log.d(TAG, "["+sb.toString() +"]");
        }
    }

    private void queueFileTask(ResultFile rf) {
        ResultsUploader resultsUploader = new ResultsUploader(context);
        resultsUploader.offerFile(rf);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.incense.android.datatask.sink.SinkWritter#writeSink(edu.incense.android
     * .datatask.sink.DataSink)
     */
    public void writeSink(DataSink dataSink) {
        // TODO Auto-generated method stub

    }
}
