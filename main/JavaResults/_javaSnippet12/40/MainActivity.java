package com.sample.iBeacon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends MyActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadApp();
	}
	
	private void loadApp(){
		Handler x = new Handler();  
		x.postDelayed(new splashhandler(), 2000);
	}
	
	private class splashhandler implements Runnable {
		public void run() {
			Intent intent = new Intent(MainActivity.this,
					ActivityHome.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			MainActivity.this.finish();
		}
	}
}
