package com.luopeng.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtil {

	private static SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	
	public static String getTime(){
		Calendar c = Calendar.getInstance();
		return sd.format(c.getTime());
	}	
	
}
