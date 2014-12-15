package com.example.airqualitymonitor;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeStamp {

	public static String get_time(){
		Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
    	return(sdf.format(cal.getTime()));		
	}
	
	public static String get_date(){
		Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
    	return(sdf.format(cal.getTime()));		
	}
	
}


