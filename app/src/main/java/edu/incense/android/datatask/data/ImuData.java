package edu.incense.android.datatask.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xilef on 10/28/2016.
 */
public class ImuData extends Data {
    private static final String TAG = "ImuData";
    private float ax;
    private float ay;
    private float az;

    private float gx;
    private float gy;
    private float gz;

    private float ox;
    private float oy;
    private float oz;

    public ImuData(){
        super(DataType.IMUDATA);
        ax = 0;
        ay = 0;
        az = 0;

        gx = 0;
        gy = 0;
        gz = 0;

        ox = 0;
        oy = 0;
        oz = 0;
    }

    public ImuData(float ax, float ay, float az, float gx, float gy, float gz, float ox, float oy,
                   float oz){
        super(DataType.IMUDATA);
        setAX(ax);
        setAY(ay);
        setAZ(az);

        setGX(gx);
        setGY(gy);
        setGZ(gz);

        setOX(ox);
        setOY(oy);
        setOZ(oz);
    }

    public String getDataDateTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(super.getTimestamp()));
    }

    public float getAX() {
        return ax;
    }

    public void setAX(float ax) {
        this.ax = ax;
    }

    public float getAY() {
        return ay;
    }

    public void setAY(float ay) {
        this.ay = ay;
    }

    public float getAZ() {
        return az;
    }

    public void setAZ(float az) {
        this.az = az;
    }

    public float getGX() {
        return gx;
    }

    public void setGX(float gx) {
        this.gx = gx;
    }

    public float getGY() {
        return gy;
    }

    public void setGY(float gy) {
        this.gy = gy;
    }

    public float getGZ() {
        return gz;
    }

    public void setGZ(float gz) {
        this.gz = gz;
    }

    public float getOX() {
        return ox;
    }

    public void setOX(float ox) {
        this.ox = ox;
    }

    public float getOY() {
        return oy;
    }

    public void setOY(float oy) {
        this.oy = oy;
    }

    public float getOZ() {
        return oz;
    }

    public void setOZ(float oz) {
        this.oz = oz;
    }
}
