package peyto.push.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PushDateUtils {
	
	public static String getCurrentYYYYMMDD() {
	        SimpleDateFormat        sdf     = new SimpleDateFormat( "yyyyMMdd" );
	        return sdf.format( Calendar.getInstance().getTime() );
	}
	
	public static String getCurrentYYYYMMDDHHMMDDSS() {
	        SimpleDateFormat        sdf     = new SimpleDateFormat( "yyyyMMddHHmmss" );
	        return sdf.format( Calendar.getInstance().getTime() );
	}
	
	public static String getCurrentYYYYMMDDHHMMDDSSSSS() {
	        SimpleDateFormat        sdf     = new SimpleDateFormat( "yyyyMMddHHmmssSSS" );
	        return sdf.format( Calendar.getInstance().getTime() );
	}
	
	public static String toYYYYMMDDHHMMDDSSSSS( Date date ) {
	        SimpleDateFormat        sdf     = new SimpleDateFormat( "yyyyMMddHHmmssSSS" );
	        return sdf.format( date );
	}
	
	public static String toYYYYMMDD( Date date ) {
	        SimpleDateFormat        sdf     = new SimpleDateFormat( "yyyyMMdd" );
	        return sdf.format( date );
	}

}