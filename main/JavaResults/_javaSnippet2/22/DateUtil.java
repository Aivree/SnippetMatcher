package cn.com.camel.jiang.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static String getSystemTime(){
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat(Constants.DATE_FORMAT);
		Date date=Calendar.getInstance().getTime();
		return simpleDateFormat.format(date);
	}
}
