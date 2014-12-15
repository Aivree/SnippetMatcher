package sshopbd.com;

import android.os.StrictMode;

public class ThreadPolicyHandler {
	private ThreadPolicyHandler() {
	}
	
	public static void doSetThreadPolicy() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build() ;
	    StrictMode.setThreadPolicy( policy ) ;
	}
}
