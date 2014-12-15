package com.pkuhci.hilog.action;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTime {
public static String getTime(){
	SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd   HH:mm:ss");
	Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间   
	String now=   formatter.format(curDate);
return now;
}


}
