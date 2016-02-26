/**
 * 
 */
package edu.incense.android.datatask.trigger;


/**
 * @author mxpxgx
 *
 */
public class Condition {
    private String data;
    private String operator;
    private String value1;
    private String value2;
    private String date;
    private String type;
    
    /**
     * @return the data
     */
    public String getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }
    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }
    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }
    /**
     * @return the value1
     */
    public String getValue1() {
        return value1;
    }
    /**
     * @param value1 the value1 to set
     */
    public void setValue1(String value1) {
        this.value1 = value1;
    }
    /**
     * @return the value2
     */
    public String getValue2() {
        return value2;
    }
    /**
     * @param value2 the value2 to set
     */
    public void setValue2(String value2) {
        this.value2 = value2;
    }
    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
}
