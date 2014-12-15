package gf.universal.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyTime {
	
	public static String myGetTime() {
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	//System.out.println( sdf.format(cal.getTime()) );
    	return sdf.format(cal.getTime());
    }
    

}
