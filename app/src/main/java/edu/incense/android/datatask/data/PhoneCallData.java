package edu.incense.android.datatask.data;

public class PhoneCallData extends Data {
    private String number;
    private boolean received;

    public PhoneCallData(String number, boolean received) {
        super(DataType.CALLS);
        setNumber(number);
        setReceived(received);
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public boolean isReceived() {
        return received;
    }
}
