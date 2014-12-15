package AndroidOS;

import android.os.StrictMode;

public class ThreadPolicy {
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
}
