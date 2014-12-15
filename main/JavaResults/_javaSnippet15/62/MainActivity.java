package ayrat.salavatovich.gmail.com.day_12.customtoastsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClick(View btn) {
		switch (btn.getId()) {
		case R.id.button:
			LayoutInflater layoutInflater = getLayoutInflater();

			View rootView = layoutInflater.inflate(R.layout.custom_layout,
					(ViewGroup) findViewById(R.id.textViewToast));

			final ImageView imageViewToast = (ImageView) rootView
					.findViewById(R.id.imageViewToast);
			imageViewToast.setImageResource(R.drawable.ic_launcher);
			final TextView textViewToast = (TextView) rootView
					.findViewById(R.id.textViewToast);
			textViewToast.setText(R.string.notification);

			Toast customToast = new Toast(getApplicationContext());
			customToast.setGravity(Gravity.CENTER, 0, 0);
			customToast.setDuration(Toast.LENGTH_LONG);
			customToast.setView(rootView);

			customToast.show();

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
