package com.example.handlexception;

import android.annotation.SuppressLint;
import android.os.StrictMode;

public class HandleVersionException {
     @SuppressLint("NewApi")
	public static void handleVersionException()
     {
    	 StrictMode.setThreadPolicy(
         		new StrictMode.ThreadPolicy.Builder()
         			.detectDiskReads()
         			.detectDiskWrites()
         			.detectNetwork()
         			.penaltyLog()
         			.build()
         	);
         StrictMode.setVmPolicy(
         		new StrictMode.VmPolicy.Builder()
         			.detectLeakedSqlLiteObjects()
         			.detectLeakedClosableObjects()
         			.penaltyLog()
         			.penaltyDeath()
         			.build()
         	);
     }
     
}
