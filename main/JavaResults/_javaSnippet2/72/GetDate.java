package com.example.geoloc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;

public class GetDate {
	
	@SuppressLint("SimpleDateFormat")
	public static String getDate(){
		Calendar date = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy-HH:mm");
		String strDate = sdf.format(date.getTime());
		return strDate;
	}

}
