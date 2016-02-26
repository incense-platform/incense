package edu.incense.android.datatask.data;

public class ScreenData extends Data{
	private String message;
	
	public ScreenData(String message) {
		super(DataType.SCREEN);
		this.message= message;		
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
