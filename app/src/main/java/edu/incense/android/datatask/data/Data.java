package edu.incense.android.datatask.data;

import android.os.Bundle;

public abstract class Data {// implements Comparable<Data> {
    private long timestamp;
    private DataType dataType = DataType.NULL;
    private Bundle extras;

    public Data(DataType dataType) {
        setTimestamp(System.currentTimeMillis());
        setDataType(dataType);
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    protected void setTimestamp(long time) {
        timestamp = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param bundle the bundle to set
     */
    public void setExtras(Bundle extras) {
        this.extras = extras;
    }

    /**
     * @return the bundle
     */
    public Bundle getExtras() {
        if(extras == null){
            extras = new Bundle();
        }
        return extras;
    }

    // public abstract int compareTo(Data data);
}