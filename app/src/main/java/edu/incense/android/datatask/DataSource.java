package edu.incense.android.datatask;

import java.util.ArrayList;

import edu.incense.android.datatask.data.Data;
import edu.incense.android.sensor.Sensor;

public class DataSource extends DataTask implements OutputEnabledTask {
//    private final static String TAG = "DataSource";
    Sensor sensor;

    public DataSource(Sensor sensor) {
        super();
        this.sensor = sensor;
        outputs = new ArrayList<Output>();
        clear();
    }

    @Override
    public void start() {
        sensor.start();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        sensor.stop();
        compute();
    }

    protected void clearInputs() {
        // No inputs for DataSource
        inputs = null;
    }

    @Override
    protected void compute() {
        Data newData;// = sensor.getData();
        // do{
        // Log.i(getClass().getName(), "Asking for new data");
        newData = sensor.getData();
        if (newData != null) {
            this.pushToOutputs(newData);
            //Log.i(TAG, "New data pushed");
        } else {
            // Log.i(getClass().getName(), "NO DATA");
        }

        // }while(newData != null);
    }

    @Override
    public void addOutput(Output o) {
        super.addOutput(o);
    }

}
