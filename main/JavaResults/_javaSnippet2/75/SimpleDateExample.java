package examples.practise;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SimpleDateExample {

	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar.getTime());
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyy");
		sdf.format(new GregorianCalendar().getTime());
		
		System.out.println(sdf);
	}
}
