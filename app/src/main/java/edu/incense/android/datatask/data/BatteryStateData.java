package edu.incense.android.datatask.data;

public class BatteryStateData extends Data{
	private String message;
	
	public BatteryStateData(String message) {
		super(DataType.BATTERY_STATE);
		this.message=message;
		// TODO Auto-generated constructor stub
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	

}
