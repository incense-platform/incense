package edu.incense.android.datatask.filter;

import java.util.ArrayList;

import edu.incense.android.datatask.DataTask;
import edu.incense.android.datatask.Input;
import edu.incense.android.datatask.InputEnabledTask;
import edu.incense.android.datatask.Output;
import edu.incense.android.datatask.OutputEnabledTask;
import edu.incense.android.datatask.data.Data;

public abstract class DataFilter extends DataTask implements OutputEnabledTask,
        InputEnabledTask {
    private String filterName = "Unavailable";

    public DataFilter() {
        super();
        inputs = new ArrayList<Input>();
        outputs = new ArrayList<Output>();
    }

    /**
     * Sets the name of the filter, the subclass name is expected.
     * @param filterName
     */
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
    
    /**
     * Returns the name of the filter (subclass name).
     * @return
     */
    public String getFilterName() {
        return filterName;
    }

    /**
     * Pulls data from every pipe and send them to the computeSingleData() method, every cycle. 
     * The frequency is defined by setSampleFrequency.
     */
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
    
    /**
     * Computes data depending on the goal of the filter. 
     * It must be overridden by a DataFilter subclass.
     * @param data
     */
    protected abstract void computeSingleData(Data data);

    /**
     * Adds an output pipe
     */
    @Override
    public void addOutput(Output o) {
        super.addOutput(o);
    }

    /**
     * Adds an input pipe
     */
    @Override
    public void addInput(Input i) {
        super.addInput(i);
    }

}
