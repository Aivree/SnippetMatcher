package com.example.planmytrip;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private  static String[] titles={"MY TRIPS","MORE"};
	private  static String[] descs={"past and planned trips","settings and manage accounts"};
	private LinearLayout option1;
	private LinearLayout option2;
	private LinearLayout option3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planner);
		LinearLayout lo=(LinearLayout) findViewById(R.id.main);
		LayoutInflater li=getLayoutInflater();
		
		
		for(int i=0;i<titles.length;i++){
			View view = li.inflate(R.layout.planner, null, false);
	        TextView title = (TextView) view.findViewById(R.id.title);
	        TextView desc = (TextView) view.findViewById(R.id.desc);
	        title.setText(titles[i]);
	        desc.setText(descs[i]);
	      
	        
	        lo.addView(view);
		}   
		
	
	}


}
