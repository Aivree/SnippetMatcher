package poo.demos.androidio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class OtherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		LayoutInflater inflater = getLayoutInflater();
		View root = inflater.inflate(R.layout.activity_other, null);
		setContentView(root);
		*/
		setContentView(R.layout.activity_other);
		Button button = (Button) findViewById(R.id.button1);
	}
}
