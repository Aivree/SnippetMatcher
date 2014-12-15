package rest;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ToJSPOutputter{
	
	
	public long time(){
		return System.currentTimeMillis();
	}
	
	public String getTime(){
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(cal.getTime());
	}
	public void addToDb(String login){
		
	}
}