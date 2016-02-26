package edu.incense.android.results;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Queue of ResultFiles yet to be sent to the remote repository.
 * This Queue is managed by the ResultsUploader.
 * 
 * NOTES: 
 * -LinkeList implementation of Queue allows null elements to be added.
 * This should be prevented.
 * 
 * @author mxpxgx
 * 
 */

public class FileQueue {
    private Queue<ResultFile> fileQueue;
    private int maxFiles;

    public FileQueue() {
        setFileQueue(new LinkedList<ResultFile>());
        maxFiles = 0;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    public FileQueue(int maxFiles) {
        this();
        this.maxFiles = maxFiles;
    }

    public void setFileQueue(Queue<ResultFile> fileQueue) {
        this.fileQueue = fileQueue;
    }

    public Queue<ResultFile> getFileQueue() {
        return fileQueue;
    }

    public boolean isEmpty() {
        return fileQueue.isEmpty() && fileQueue.peek() == null;
    }

    public ResultFile peek() {
        return fileQueue.peek();
    }

    public ResultFile poll() {
        return fileQueue.poll();
    }

    public boolean offer(ResultFile resultFile) {
        //Prevent LinkeList to add null elements
        if (resultFile != null) {
            if (maxFiles > 0 && fileQueue.size() >= maxFiles) {
                poll();
            }
            return fileQueue.offer(resultFile);
        } else
            return false;
    }
}
