package com.cs2340.buzzfunds;

import android.os.StrictMode;

/**
 * Class which contains miscellaneous setup methods for Activities.
 * 
 * @author Sean Collins
 */
public class Setup {
	/**
	 * Causes this Activity to ignore Exceptions which would otherwise be thrown
	 * by running networking activities on the main thread.
	 */
	public static void ignoreMainNetworkException() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
}
