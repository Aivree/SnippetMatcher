package models;

import java.util.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import play.data.validation.*;
import play.db.ebean.*;

public class time extends Model  {


    public static String getTime() {
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(cal.getTime()).toString();
    }
    
 
}
