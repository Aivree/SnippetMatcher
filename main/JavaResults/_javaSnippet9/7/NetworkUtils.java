package chalmers.eda397.team07.supremeagile.common;

import android.os.StrictMode;

public class NetworkUtils {
	public static void permitAllThreadPolicy() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
}
