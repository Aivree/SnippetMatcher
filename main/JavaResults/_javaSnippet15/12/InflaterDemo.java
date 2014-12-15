package com.example.myfirstandproj;

import android.support.v7.app.ActionBarActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.os.Bundle;

public class InflaterDemo extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inflater_demo);
		
		LinearLayout parentLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_InflaterDemo);
		LayoutInflater layoutInflater = getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.sublayout,parentLinearLayout);	
		
	}
	}
