package com.cannibal_photographer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		Boat boatobject = (Boat)findViewById(R.id.boatimageView);	
		Person personobject = (Person)findViewById(R.id.personView1);
		RelativeLayout main = (RelativeLayout)findViewById(R.layout.activity_main);
		LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup boatview = (ViewGroup) li.inflate(R.layout.boatlayout,null);
		View personview = li.inflate(R.layout.personlayout,null);
		//boatview = (ViewGroup) li.inflate(R.layout.personlayout, null);
		
		//((ViewGroup) main).addView(boatview);
		//personview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//boatview.removeView(boatview);
		((ViewGroup) main).addView(boatview);
		((ViewGroup) boatview).addView(personview);
        
		//ViewGroup boatview = (ViewGroup) getLayoutInflater().inflate(R.layout.boatlayout, main,false);
        //View personview = getLayoutInflater().inflate(R.layout.personlayout,  main, false);
        //personview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        //main.addView(boatview);
        //boatview.addView(personview);
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
