package com.topsun.posclient.common.ui.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {
	public static String getCurrentDate(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(Calendar.getInstance().getTime());
	}
	
	public static String getCurrentDateTime(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(Calendar.getInstance().getTime());
	}
}
