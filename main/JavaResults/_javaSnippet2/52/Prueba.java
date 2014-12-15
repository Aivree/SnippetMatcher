package xg.generadorCFEs;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Prueba {
	
	public final static void main(String[] args){
		Calendar date = Calendar.getInstance();
		Calendar cldr;
		cldr = (Calendar) date.clone();		
		SimpleDateFormat dateformatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'000'");
		SimpleDateFormat dateformatter2 = new SimpleDateFormat(" Z ");
		String z= dateformatter2.format(cldr.getTime());
		System.out.println("Z antes: "+z);
		z=z.substring(1,4)+":"+z.substring(4,6);
		String a = dateformatter.format(cldr.getTime());
		String res= a+z;
	}

}
