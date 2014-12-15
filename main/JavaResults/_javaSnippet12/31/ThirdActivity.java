package com.example.mylifecycleexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class ThirdActivity extends LifeCycleBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
	}
	
	public void showFirst(View v) {
		Intent first = new Intent(this, FirstActivity.class);
//		first.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);	
		first.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);		
		startActivity(first);
	}
	


}
