package GUI1_4test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockModel {

	public String getNowTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
		return sdf.format(new Date().getTime());
	}
}
