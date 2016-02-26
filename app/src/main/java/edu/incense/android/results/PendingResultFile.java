package edu.incense.android.results;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import edu.incense.android.R;
import edu.incense.android.datatask.data.DataType;

/**
 * Created by xilef on 2/11/2016.
 * This class will help to obtain all the files that exists in the memory phone that have not been
 * uploaded to the server.
 */
public class PendingResultFile {
    // Obtain all the files that are in all the directories in the InCense directory.
    public static FileQueue getPendingResultFiles(Context context){
        String parentDirectory = context.getResources().getString(R.string.application_root_directory);
        int maxFiles = Integer.parseInt(context.getResources().getString(R.string.filequeue_max_files));
        File parent = new File(Environment.getExternalStorageDirectory(), parentDirectory);
        FileQueue fq = new FileQueue();
        Queue<ResultFile> q = new LinkedList<ResultFile>();

        fq.setMaxFiles(maxFiles);
        File[] pendingDirectories = parent.listFiles();

        for (File f : pendingDirectories) {
            for (File innerFile : f.listFiles()){
                ResultFile rf = null;
                String fileName = innerFile.getName();
                String[] values = fileName.split("_");

                // Survey files don't use sink, so file name style is: survey_timestamp.json
                if (values.length == 2) {
                    String dataType = values[0];
                    String timeStamp = values[1].substring(0, values[1].lastIndexOf("."));
                    rf = ResultFile.createInstanceForExistingFile(innerFile.getAbsolutePath(), FileType.valueOf(dataType.toUpperCase()), timeStamp);
                }

                // Data and audio files do use sinks, so file name style is: data_sinkName_timeStamp.json, audio_sinkName_timeStamp.raw respectively.
                if (values.length == 3){
                    String dataType = values[0];
                    String timeStamp = values[2].substring(0, values[2].lastIndexOf("."));
                    rf = ResultFile.createInstanceForExistingFile(innerFile.getAbsolutePath(), FileType.valueOf(dataType.toUpperCase()), timeStamp);
                }
                if (rf != null)
                    q.offer(rf);
            }
        }
        fq.setFileQueue(q);

        return fq;
    }
}
