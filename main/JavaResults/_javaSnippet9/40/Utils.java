package githubuserslist.troshchiy.com.githubuserslist.util;

import android.os.StrictMode;

public class Utils {

    /** Avoid creating an instance of this class */
    private Utils() { throw new AssertionError(); }

    public static void enableStrictMode() {
        StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog();

        StrictMode.VmPolicy.Builder vmPolicyBuilder =
                new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog();

        threadPolicyBuilder.penaltyFlashScreen();
//        vmPolicyBuilder
//                .setClassInstanceLimit(ImageGridActivity.class, 1)
//                .setClassInstanceLimit(ImageDetailActivity.class, 1);

        StrictMode.setThreadPolicy(threadPolicyBuilder.build());
        StrictMode.setVmPolicy(vmPolicyBuilder.build());
    }
}