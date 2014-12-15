package Miscelanea;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;

public class DateTime {
	@SuppressLint("SimpleDateFormat")
	public String GetFecha(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = df1.format(c.getTime());
		return formattedDate;
	}
	
	@SuppressLint("SimpleDateFormat")
	public String GetHora(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss a");
		String formattedDate = df1.format(c.getTime());
		return formattedDate;
	}

}
