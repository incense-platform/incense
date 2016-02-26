package edu.incense.android.datatask;

import java.util.LinkedList;
import java.util.Queue;

import edu.incense.android.datatask.data.Data;

/**
 * This represents a pipe in the Pipes & Filters pattern. PipeBuffers are
 * connections between DataTask. This is a passive pipe, where DataTasks
 * push/pull to/from it.
 * 
 * NOTES:
 * 
 * -It may need a maximum size to prevent memory leaking.
 * 
 * -LinkeList implementation of Queue allows null elements to be added. This
 * should be prevented.
 * 
 * @author mxpxgx
 * 
 */

public class PipeBuffer implements Input, Output {
    private Queue<Data> dataBuffer = null;

    public PipeBuffer() {
        dataBuffer = new LinkedList<Data>();
    }

    public Data pullData() {
        // Added peek() to check for null, because LinkeList implementation of
        // Queue allows null elements to be added
        // Update: changed remove() to poll(), as it doesn't throws exception
        if (!dataBuffer.isEmpty() && dataBuffer.peek() != null)
            return dataBuffer.poll();
        else
            return null;
    }

    public void pushData(Data data) {
        // Added data != null to prevent null data to be added.
        // LinkeList implementation of Queue allows null elements to be added
        if (dataBuffer != null && data != null)
            dataBuffer.offer(data);
    }

}
