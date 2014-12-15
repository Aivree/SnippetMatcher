package com.salyangoz.yourchoice;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	public void profile(View v) {
		Intent profileintent = new Intent(getApplicationContext(), ProfileActivity.class);
		profileintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(profileintent);
	}

	public void feed(View v) {
		Intent feedintent = new Intent(getApplicationContext(), FeedActivity.class);
		feedintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(feedintent);
	}

	public void camera(View v) {
		Intent cameraintent = new Intent(getApplicationContext(), CameraActivity.class);
		cameraintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(cameraintent);
	}

	public void notification(View v) {
		Intent notificationintent = new Intent(getApplicationContext(), NotificationActivity.class);
		notificationintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(notificationintent);
	}
	public void random(View v) {
		Intent randomintent = new Intent(getApplicationContext(), RandomFeed.class);
		randomintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(randomintent);
	}


}
