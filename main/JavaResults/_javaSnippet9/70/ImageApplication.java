package com.neevtech.imageprocessing;

import android.app.Application;
import android.os.StrictMode;

public class ImageApplication extends Application {
	@Override
	public void onCreate() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll() // or
				// .detectAll()
				// for
				// all
				// detectable
				// problems
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
				.penaltyLog().penaltyDeath().build());
		super.onCreate();
	}
}
