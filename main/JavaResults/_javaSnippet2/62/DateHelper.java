package eu.derloki.util.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHelper {

	public static String getTime(){
		return getTime("dd.MM.yyyy HH:mm:ss");
	}
	
	public static String getTime(String format){
		DateFormat df = new SimpleDateFormat(format);
		return df.format(Calendar.getInstance().getTime());
	}
	
}
