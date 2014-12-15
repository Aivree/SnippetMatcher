package yz.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyTime {
	public static String getTime() {
		
		//获取当前时间
		Date currDate = Calendar.getInstance().getTime();
		
		//以固定格式产生字符
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//返回
		return sdf.format(currDate);

	}
}
