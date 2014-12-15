import java.text.SimpleDateFormat;
import java.util.Calendar;

public class countdown {

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	System.out.println( sdf.format(cal.getTime()));
	}

}
