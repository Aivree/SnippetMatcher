package com.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTime {
	
	public String now_Time() {
		
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		return simpleDateFormat.format(calender.getTime());
	}
}
