package com.quiz.tech;

import android.app.Application;
import android.os.StrictMode;

public class QuizApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads()
				.detectDiskWrites()
				.detectAll()   // or .detectAll() for all detectable problems
				.penaltyLog()
				.build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.detectLeakedClosableObjects()
				.penaltyLog()
				.penaltyDeath()
				.build());
	}
}