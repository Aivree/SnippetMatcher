package de.nkpmedia.jvoice.sysinterface;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time
{
	public String getTime()
	{	
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return "it's " + sdf.format(new Date());
		
	}
	public String getDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		SimpleDateFormat sdf2 = new SimpleDateFormat("d. MMMM.");
		return "today is " + sdf.format(new Date()) + " the " + sdf2.format(new Date());
		
	}
}
