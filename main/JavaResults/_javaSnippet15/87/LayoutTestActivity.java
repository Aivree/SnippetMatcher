package com.example.test.image;

import hot.lib.trace.android.AndroidTraceTimes;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;

public class LayoutTestActivity extends FragmentActivity{

	private static final String TRACE_RELATIVE = "relative";
	private static final String TRACE_LINEAR = "linear";
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Log.e("onCreate", "start");
		
		//setContentView(R.layout.layout_v_test_relative);
//		setContentView(R.layout.layout_v_test_linear);
		LayoutInflater inflater = getLayoutInflater();
		
		


		inflater.inflate(R.layout.layout_v_test_relative, null, false);
		inflater.inflate(R.layout.layout_v_test_linear, null, false);

		
		
		AndroidTraceTimes.getInstance().startTrace(TRACE_RELATIVE);		
		for (int i = 0; i < 500; i++) {
			inflater.inflate(R.layout.layout_v_test_relative, null, false);
		}
		AndroidTraceTimes.getInstance().printTrace(TRACE_RELATIVE);
		
		AndroidTraceTimes.getInstance().startTrace(TRACE_LINEAR);		
		for (int i = 0; i < 500; i++) {
			inflater.inflate(R.layout.layout_v_test_linear, null, false);
		}
		AndroidTraceTimes.getInstance().printTrace(TRACE_LINEAR);		
		
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		
		Log.e("onCreate", "end");
	}


}
