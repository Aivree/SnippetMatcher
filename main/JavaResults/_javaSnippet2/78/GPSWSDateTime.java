package nl.globalorange.samples.testGit;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GPSWSDateTime {
	
	public static String getGPSWSDate() {
		
		return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	}

	public static String getGPSWSTime() {
		
		return new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());
	}
	
}
