package com.utils;

import android.os.StrictMode;

public class ConfigAndroidUtils {
	
	public static void liberarProcessosBackground(){
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}
}
