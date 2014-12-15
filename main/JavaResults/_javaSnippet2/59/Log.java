package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {

	
	public static void print(String className, String message){
		System.out.println(getTime() + "\t" + className  + ":\t\t" + message);
	}
	
	public static void printErr(String className, String message){
		System.err.println(getTime() + "\t" + className  + ":\t\t" + message);
	}
	
	public static String getTime(){
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return "[" +sdf.format(cal.getTime()) +"]";
	}
	
}
