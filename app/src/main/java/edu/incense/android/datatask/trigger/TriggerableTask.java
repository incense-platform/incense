package edu.incense.android.datatask.trigger;

import edu.incense.android.datatask.data.Data;

public interface TriggerableTask {
    public void start();
    public void start(Data data);
}
