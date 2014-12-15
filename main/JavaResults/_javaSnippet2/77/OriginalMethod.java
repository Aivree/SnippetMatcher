package reflect;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OriginalMethod {
	private String getTime(){
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
		String time = sdf.format(date);
		return time;
	}
	
	public String getBjTime(){
		return "BJ time is : " + getTime();
	}
}
