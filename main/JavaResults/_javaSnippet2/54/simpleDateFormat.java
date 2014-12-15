import java.text.SimpleDateFormat;

public class simpleDateFormat {
	static String getTimestamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		java.util.Date today = new java.util.Date();
		return "_" + formatter.format(new java.sql.Timestamp(today.getTime()));
	}

	public static void main(String[] args) {
		System.out.println(getTimestamp());
	}
}
