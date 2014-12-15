package com.example.dddddd;

import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LayoutInflater infalter;
	private View mainView;
	private LinearLayout lly;
	private Button but;
	private TextView tv;
	private LinearLayout llly;
	private LayoutParams lp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 infalter = getLayoutInflater() ; 
		 mainView = infalter.inflate(R.layout.activity_main, null);
		 setContentView(mainView);
		 lly = (LinearLayout) findViewById(R.id.lly) ; 
		 but = (Button) findViewById(R.id.but) ; 
		 
		 llly =  (LinearLayout)infalter.inflate(R.layout.other, lly) ; 
		  tv = (TextView) findViewById(R.id.a) ;
		  lp = (LinearLayout.LayoutParams)tv.getLayoutParams() ; 
		  lp.gravity = Gravity.CENTER_HORIZONTAL ; 
		  tv.setLayoutParams(lp);
		  
		 but.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				infalter.inflate(R.layout.other, lly,true) ; 
				llly.addView(infalter.inflate(R.layout.other, null)) ;
				Toast.makeText(MainActivity.this, tv.getText().toString(), 1).show() ; 
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
