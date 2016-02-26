package edu.incense.android.datatask.data;

public class PhoneStateData extends Data {
    // private int isCharging;
    private String dataState;
    private String extra;
    private boolean hasExtra;

    public PhoneStateData() {
        super(DataType.STATES);
        hasExtra = false;
    }

    public void setState(String state) {
        this.dataState = state;
    }

    public String getState() {
        return dataState;
    }

    /**
     * @param extra the extra to set
     */
    public void setExtra(String extra) {
        this.extra = extra;
        hasExtra = true;
    }

    /**
     * @return the extra
     */
    public String getExtra() {
        return extra;
    }
    
    public boolean hasExtra(){
        if(extra == null) return false;
        return hasExtra;
    }
}
