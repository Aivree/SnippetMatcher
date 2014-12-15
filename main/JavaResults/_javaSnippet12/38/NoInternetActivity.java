package com.pe.lima.museos;

import android.content.Intent;
import android.os.Bundle;

public class NoInternetActivity extends ParentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_no_internet);
		setSubTitle(getResources().getString(R.string.title_activity_no_internet));		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (isNetworkConnected()) {		
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(getApplicationContext(), MainActivity.class);
			startActivity(intent);			
		}
	}	

}
