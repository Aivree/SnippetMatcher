package joshy2205.projectathena.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeDateUtil {

	private TimeDateUtil() {

	}

	public static String getTime() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar calendar = Calendar.getInstance();

		return dateFormat.format(calendar.getTime());

	}

	public static String getDate() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();

		return dateFormat.format(calendar.getTime());

	}

}
