package com.tiankonguse.gameplatform.net;

import android.annotation.SuppressLint;
import android.os.StrictMode;

@SuppressLint("NewApi")
public class MyStrictMode {
	public static void strictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
				.penaltyLog() // 打印logcat
				.penaltyDeath().build());
	}
}
