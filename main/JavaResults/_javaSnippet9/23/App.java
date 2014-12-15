package com.drg.testretrofit;

import android.app.Application;
import android.os.StrictMode;

/**
 * App
 */
public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
	}
}