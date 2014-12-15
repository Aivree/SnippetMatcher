package com.intravita.android.rss;

import android.app.Application;
import android.os.StrictMode;

public class RSSApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		// Used to prevent Internet usage on main thread. Just in case Async library is not really async.
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()	
		.detectNetwork().build());
	}
}
