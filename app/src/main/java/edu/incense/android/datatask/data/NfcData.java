/**
 * 
 */
package edu.incense.android.datatask.data;

/**
 * @author mxpxgx
 *
 */
public class NfcData extends Data {
    private String message=null;
    
    /**
     * @param dataType
     */
    public NfcData() {
        super(DataType.NFC);
    }
    
    public NfcData(String message) {
        this();
        setMessage(message);
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
