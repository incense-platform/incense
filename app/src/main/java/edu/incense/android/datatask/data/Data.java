package edu.incense.android.datatask.data;

import android.os.Bundle;
import org.codehaus.jackson.annotate.JsonIgnore;

public abstract class Data {// implements Comparable<Data> {
    @JsonIgnore
    private long timestamp;
    private DataType dataType = DataType.NULL;
    // This is used only when the data type managed by this class is defined outside this project.
    // Is set to null when data type is defined in this project.
    private String customDataName;
    //@JsonIgnore
    private Bundle extras;

    public Data(DataType dataType) {
        setTimestamp(System.currentTimeMillis());
        setDataType(dataType);
        setCustomDataName(null);
    }

    /**
     * This constructor is used by subclases of data that are defined outside this project, like
     * when components are defined and custom data is sent in and out of the component.
     * @param customDataName The name of the custom data.
     */
    public Data(String customDataName){
        setTimestamp(System.currentTimeMillis());
        setDataType(dataType.CUSTOM);
        setCustomDataName(customDataName);
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

    @JsonIgnore
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the custom name of this data type.
     * @return
     */
    public String getCustomDataName(){
        return this.customDataName;
    }

    /**
     * Sets the custom name for this data type. This must not be changed once set on the constructor.
     * @param customDataName
     */
    private void setCustomDataName(String customDataName){
        this.customDataName = customDataName;
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
    @JsonIgnore
    public Bundle getExtras() {
        if(extras == null){
            extras = new Bundle();
        }
        return extras;
    }

    // public abstract int compareTo(Data data);
}