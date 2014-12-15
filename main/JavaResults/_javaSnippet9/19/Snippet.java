package snippet;

import android.annotation.SuppressLint;
import android.os.StrictMode;

@SuppressLint("NewApi")
public class Snippet {
	public static void main(String[] args) {
		  if (android.os.Build.VERSION.SDK_INT > 9) {
		        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		                .permitAll().build();
		        StrictMode.setThreadPolicy(policy);
		    }
	}
}

