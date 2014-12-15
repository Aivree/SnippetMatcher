package org.MaMaxi.Screenshot.util;

import java.text.*;
import java.util.*;

public class DateUtils
{
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH_mm_ss";
	public static final String DATE_FORMAT_TIME = "HH_mm_ss";
	public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";
	
	public static String now()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
		
	}
	
	public static String year()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return sdf.format(cal.getTime());
		
	}
	
	public static String month()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		return sdf.format(cal.getTime());
	}
	
	public static String day()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		return sdf.format(cal.getTime());
	}
	
	public static String hour()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		return sdf.format(cal.getTime());
	}
	
	public static String minute()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		return sdf.format(cal.getTime());
	}
	
	public static String second()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("ss");
		return sdf.format(cal.getTime());
	}
	
	public static String time()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_FORMAT_TIME);
		return sdf.format(cal.getTime());
		
	}
	
	public static String date()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE);
		return sdf.format(cal.getTime());
		
	}
}