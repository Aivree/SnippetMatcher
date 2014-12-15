package git.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	final String LOG_TAG = "myLogs";

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LayoutInflater ltInflater = getLayoutInflater();
		View view = ltInflater.inflate(R.layout.view, null, false);
		LayoutParams lp = view.getLayoutParams();

		Log.d(LOG_TAG, "Class of view: " + view.getClass().toString());
		Log.d(LOG_TAG, "LayoutParams of view is null: " + (lp == null));
		Log.d(LOG_TAG, "Text of view: " + ((TextView) view).getText());
	}
}