package com.cmitchell687.books.app;

import android.app.Application;
import android.os.StrictMode;

public class BookSampleApplication extends Application {

    @Override
    public void onCreate() {
        enableStrictMode();
        super.onCreate();
    }


    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy.Builder builder = new StrictMode.ThreadPolicy.Builder();
            builder.detectAll()
                    .penaltyLog()
                    .penaltyFlashScreen();

            StrictMode.setThreadPolicy(builder.build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }
}
