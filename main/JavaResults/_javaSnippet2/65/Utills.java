package com.ankit.carorder.commons;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utills {
	public static String getSystemDate() {
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		return df.format(c.getTime());
	}
}
