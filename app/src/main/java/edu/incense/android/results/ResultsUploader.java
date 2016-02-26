package edu.incense.android.results;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.util.Log;
import edu.incense.android.R;
import edu.incense.android.comm.Uploader;

public class ResultsUploader {
    private final static String TAG = "ResultsUploader";
    private FileQueue fileQueue;
    private JsonResults jsonResults;
    private File queueFile;
    private String filename;
    private Context context;

    public ResultsUploader(Context context) {
        this.context = context;

        // Public file
        // String parentDirectory = context.getResources().getString(
        // R.string.application_root_directory);
        // File parent = new File(Environment.getExternalStorageDirectory(),
        // parentDirectory);
        // queueFile = new File(parent, context.getResources().getString(
        // R.string.filequeue_filename));

        // Private file
        filename = context.getResources()
                .getString(R.string.filequeue_filename);
        queueFile = context.getFileStreamPath(filename);

        jsonResults = new JsonResults();

        if (queueFile.exists()) {
            fileQueue = loadQueue();
        }
        if (fileQueue == null) {
            fileQueue = new FileQueue();
            int maxFiles = Integer.parseInt(context.getResources().getString(
                    R.string.filequeue_max_files));
            fileQueue.setMaxFiles(maxFiles);
            //There no Queue, a new one should be created
            Queue<ResultFile> queue = new LinkedList<ResultFile>();
            fileQueue.setFileQueue(queue);
            saveQueue();
        } else {
            // Check every file exist. If it doesn't exist, remove from queue.
            Queue<ResultFile> queue = fileQueue.getFileQueue();
            List<ResultFile> nonexistantFiles = new ArrayList<ResultFile>();
            for (ResultFile rf : queue) {
                File file = new File(rf.getFileName());
                if (!file.exists()) {
                    nonexistantFiles.add(rf);
                }
            }
            for (ResultFile rf : nonexistantFiles) {
                queue.remove(rf);
            }
            
        }
    }

    private FileQueue loadQueue() {
        // Public
        // return jsonResults.toFileQueue(queueFile);

        // Private
        Log.i(getClass().getName(),
                "Writting file to: " + queueFile.getAbsoluteFile());
        try {
            // Previous author used results.json to maintain the queue of files to upload to repository.
            // This had issues since queue was lost every time the application or the phone was restarted.
//            InputStream input = context.openFileInput(filename);
//            FileQueue fq = jsonResults.toFileQueue(input);

            // We now use existing files in the InCense folder to create the queue.
            // Once a file is uploaded to the repository, it is deleted.
            FileQueue fq = PendingResultFile.getPendingResultFiles(context);

            return fq;
        } catch (Exception e) {
            Log.e(TAG, "File [" + filename + "] not found", e);
        }
        return null;
    }

    private void saveQueue() {
        // Public
        // jsonResults.toJson(queueFile, fileQueue);
        
        // Private
        try {
            OutputStream output = context.openFileOutput(filename, 0);
            jsonResults.toJson(output, fileQueue);
        } catch (FileNotFoundException e) {
            Log.i(TAG, "File [" + filename + "] not found", e);
        }
    }

    public void offerFile(ResultFile resultFile) {
        fileQueue.offer(resultFile);
        saveQueue();
    }

    public void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                File parent = file.getParentFile();
                file.delete();
                if (parent.listFiles().length == 0)
                    parent.delete();
            } catch (Exception e) {
                Log.e(getClass().getName(), "Deleting file", e);
            }
        }
    }

    /***
     * sendFiles() tries to send everything in the the file queue.
     * 
     * @return int - number of files uploaded.
     */
    public int sendFiles() {
        Uploader uploader = new Uploader(context);
        ResultFile file;
        boolean sent = true;
        int count = 0;
        while (!fileQueue.isEmpty() && sent) {
            file = fileQueue.peek();
            sent = false;
            switch (file.getFileType()) {
            case DATA:
                sent = uploader.postSinkData(file.getFileName());
                break;
            case AUDIO:
                sent = uploader.postAudioData(file.getFileName());
                break;
            case SURVEY:
                sent = uploader.postSurveyData(file.getFileName());
                break;
            }
            if (sent) {
                file = fileQueue.poll();
                deleteFile(file.getFileName());
                count++;
            }
        }
        if (count > 0) {
            saveQueue();
        }
        return count;
    }

    public List<ResultFile> getQueueList() {
        List<ResultFile> list = new ArrayList<ResultFile>(
                fileQueue.getFileQueue());
        return Collections.unmodifiableList(list);
    }

}
