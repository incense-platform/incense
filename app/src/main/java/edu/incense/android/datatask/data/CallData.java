package edu.incense.android.datatask.data;

public class CallData extends Data{
	private String message;
	
	public CallData(String message) {
	super(DataType.CALLS_STATE);
	this.message=message;
}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
