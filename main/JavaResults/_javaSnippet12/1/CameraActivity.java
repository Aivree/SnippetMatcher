package com.salyangoz.yourchoice;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class CameraActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_camera);
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
