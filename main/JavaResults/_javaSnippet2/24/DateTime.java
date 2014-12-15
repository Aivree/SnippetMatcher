package DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTime {

	public static String getDate(){
		Calendar cal = Calendar.getInstance();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
		return sdf.format(cal.getTime());
	}
	
	public static String getTime(){
		Calendar cal = Calendar.getInstance();
		
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		return sdf.format(cal.getTime());
	}
}
