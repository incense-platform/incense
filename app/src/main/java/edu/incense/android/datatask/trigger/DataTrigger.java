package edu.incense.android.datatask.trigger;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import edu.incense.android.datatask.DataTask;
import edu.incense.android.datatask.Input;
import edu.incense.android.datatask.InputEnabledTask;
import edu.incense.android.datatask.data.Data;

public abstract class DataTrigger extends DataTask implements InputEnabledTask {
    protected List<Data> previousData;
    protected Context context;

    protected DataTrigger(Context context) {
        this.context = context;
        clear();
        // inputs = new ArrayList<Input>();
    }

    @Override
    protected void clearOutputs() {
        // No outputs for DataSink
        // outputs.removeAll(outputs);
        outputs = null;
    }

    protected abstract void trigger();

    public boolean hasMemory() {
        if (previousData == null) {
            return false;
        } else {
            return true;
        }
    }

    public void setMemory(boolean enable) {
        if (enable) {
            if (previousData == null) {
                previousData = new ArrayList<Data>();
            }
        } else {
            previousData = null;
        }

    }

    @Override
    protected void compute() {
        Data tempData;
        for (Input i : inputs) {
            //Log.i(getClass().getName(), "Asking for new data");
            tempData = i.pullData();
            if (tempData != null) {
                computeSingleData(tempData);
                //Log.i(getClass().getName(), "GOOD");
            } else {
                //Log.i(getClass().getName(), "BAD");
            }
        }
    }

    protected abstract void computeSingleData(Data data);

    @Override
    public void addInput(Input i) {
        super.addInput(i);
    }

}
