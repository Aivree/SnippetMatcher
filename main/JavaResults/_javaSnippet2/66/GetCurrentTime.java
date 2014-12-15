package com.henihouse.meteo.functions;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetCurrentTime {
	public static String getCurrentTime(boolean view) {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.mmm");
		if (view)
			System.out.println(sdf.format(cal.getTime()));
		return sdf.format(cal.getTime());
	}
}
