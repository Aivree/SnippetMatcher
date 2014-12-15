import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TryTime {

    public static void main(String[] args) {
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	//SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	System.out.println( cal.getTime() );

    	//String str =cal.getTime();
    	System.out.println(cal.get(Calendar.HOUR_OF_DAY));
    }

}