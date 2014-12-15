package joshy2205.walkerchat.server.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommandLine {

	public static void toConsole(String message, MessagePriority priority) {

		String output = getTime() + " " + priority.getDisplayFormat() + ": " + message;
		System.out.println(output);

	}

	private static String getTime() {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

		return "(" + simpleDateFormat.format(calendar.getTime()) + ")";

	}

}
