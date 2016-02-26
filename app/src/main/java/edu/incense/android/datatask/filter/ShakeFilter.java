package edu.incense.android.datatask.filter;

import android.util.Log;
import edu.incense.android.datatask.data.AccelerometerFrameData;
import edu.incense.android.datatask.data.Data;

public class ShakeFilter extends DataFilter {
    private static final String ATT_ISSHAKE = "isShake";
    private static final int SHAKE_THRESHOLD = 500; //900;
    private double last_x, last_y, last_z;
    private double lastUpdate;
    private boolean last;
    private int counter;

    public ShakeFilter() {
        super();
        setFilterName(this.getClass().getName());
        last_x = 0;
        last_y = 0;
        last_z = 0;
        lastUpdate = 0;
        last = false;
        counter = 0;
    }

    @Override
    public void start() {
        super.start();
        last = false;
        counter = 0;
    }

    @Override
    protected void computeSingleData(Data data) {
        Data newData = seekForShake(data);
        pushToOutputs(newData);
    }

    private Data seekForShake(Data data) {
        AccelerometerFrameData accData = (AccelerometerFrameData) data;
        double[][] frame = accData.getFrame();
        boolean shake = false;
        for(int i=0; i<accData.getFrame().length; i++){
            shake = seekForShake(frame[i]);
            if(shake){
                data.getExtras().putBoolean(ATT_ISSHAKE, true);
                return data;
            }
        }
        data.getExtras().putBoolean(ATT_ISSHAKE, shake);
        return data;
    }
    
    private boolean seekForShake(double[] data) {
        double x = data[0];
        double y = data[1];
        double z = data[2];
        double curTime = data[3];
        double diffTime = (curTime - lastUpdate);

        if (!last) {
            setLast(x, y, z, curTime);
            last = true;
            //return new BooleanData(false);
            return false;
        } else {
            double velocity = (double) (Math.abs(x + y + z - last_x - last_y
                    - last_z)
                    / diffTime * 10000000000.0f);
            setLast(x, y, z, curTime);
            if (velocity > SHAKE_THRESHOLD) {
                Log.v(getClass().getName(), "SHAKE detected with speed: "
                        + velocity);
                counter++;
                if (counter > 0) {
                    Log.v(getClass().getName(),
                            "DOUBLE SHAKE detected with speed: " + velocity);
//                    return new BooleanData(true);
                    return true;
                }
//                return new BooleanData(false);
                return false;
            } else {
//                Log.i(getClass().getName(), "SHAKE NOT detected with velocity: "
//                        + velocity);
//                return new BooleanData(false);
                return false;
            }
        }

    }

    public void setLast(double x, double y, double z, double lastUpdate) {
        last_x = x;
        last_y = y;
        last_z = z;
        this.lastUpdate = lastUpdate;
    }

}
