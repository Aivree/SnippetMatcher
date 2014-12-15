package sample.actionscontentview.application;

import android.app.Application;
import android.os.StrictMode;

public class KostaLMSApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyDeath()
        .build());
    }
}
