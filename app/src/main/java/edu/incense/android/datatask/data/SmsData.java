package edu.incense.android.datatask.data;

public class SmsData extends Data{
private String message;

	public SmsData(String message) {
		super(DataType.SMS);
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
