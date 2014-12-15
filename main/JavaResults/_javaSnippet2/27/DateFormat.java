package com.niubaisui.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat {
	
	public static Date getDate(String time) throws ParseException{
		SimpleDateFormat format=new SimpleDateFormat();
		return format.parse(time);
	}
	
	public static Date getDate(String time,String pattern) throws ParseException{
		SimpleDateFormat format=new SimpleDateFormat(pattern);
		return format.parse(time);
	}
	
	public static String getTime(Date date){
		SimpleDateFormat format=new SimpleDateFormat();
		return format.format(date);
	}
	public static String getTime(Date date,String pattern){
		SimpleDateFormat format=new SimpleDateFormat(pattern);
		return format.format(date);
	}

}
