package com.feiyu.smarthome.connect;

import android.annotation.SuppressLint;
import android.os.StrictMode;

@SuppressLint("NewApi")
public class ConnectCheckThread {
public static void check()
{
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
    .detectDiskReads()  
    .detectDiskWrites()  
    .detectNetwork()   // or .detectAll() for all detectable problems  
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
