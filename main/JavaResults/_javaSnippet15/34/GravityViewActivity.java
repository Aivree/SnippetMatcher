package com.example.gravity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.view.GravityView;

public class GravityViewActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		LayoutInflater inflater = this.getLayoutInflater();
		View view = inflater.inflate(R.layout.activity_main, null);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.group);
		final GravityView g = new GravityView(this);
		layout.addView(g);
		setContentView(layout);
		
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				g.startMove();
			}
		});
		
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				g.stopMove();
			}
		});
	}
}
