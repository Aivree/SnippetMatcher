package kr.ac.jejunu.capston.attendancecheck.threadpolicy;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ThreadPolicy {

	public ThreadPolicy() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
}
