package gui1_3;

import java.text.SimpleDateFormat;
import java.util.*;

public class ClockModel {
	
	public String getNowTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
		return sdf.format(new Date().getTime());
	}
}
