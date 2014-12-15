package com.hawaiibarbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class HomePageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage_lo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_page, menu);
		return true;
	}
	
	public void handleButton(View v){
		
		Intent in;
		
		switch(v.getId()){
			case R.id.ButtonAroundME:
				in  = new Intent(this, BarListLocationActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(in);
				break;
				
			case R.id.ButtonByPrice:
				in = new Intent(this, BarsByPrice.class);
				in.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(in);
				break;
		}
		
		
		
	}

}
