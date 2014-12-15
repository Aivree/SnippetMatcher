package Cal;

import java.text.SimpleDateFormat;

public class CalUtil {
	public CalUtil() {
	}

	public static String getTime() {
		SimpleDateFormat myformat = new SimpleDateFormat("yyyyMMdd");
		return myformat.format(new java.util.Date());
	}
}
