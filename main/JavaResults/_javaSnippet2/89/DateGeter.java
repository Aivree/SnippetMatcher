package nit.wave.utils;

public class DateGeter {
	
	public static  String getDate(){
		java.util.Calendar c=java.util.Calendar.getInstance();    
        java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");    
        return f.format(c.getTime());  
	}

}
