package thread;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CurrentTime {
	public static String getTime(){
		SimpleDateFormat forMat=new SimpleDateFormat("yyyy:MM:dd:HH-mm-ss");
		return forMat.format(Calendar.getInstance().getTime());
		
	}
}
