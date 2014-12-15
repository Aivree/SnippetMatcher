package com.pe.lima.museos;

import android.os.Bundle;
import android.content.Intent;

public class OpinarActivity extends ParentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_opinar);
		setSubTitle(getResources().getString(R.string.title_activity_opinar));
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (!isNetworkConnected()) {		
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(getApplicationContext(), NoInternetActivity.class);
			startActivity(intent);			
		}
	}
}
