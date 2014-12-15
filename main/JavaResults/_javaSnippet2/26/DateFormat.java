package cn.register;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat
{
	public static long getDate(String s)
	{
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition parseposition = new ParsePosition(0);
		Date date = simpledateformat.parse(s, parseposition);
		return date.getTime();
	}
}
