package com.salyangoz.yourchoice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.salyangoz.adapter.CustomNext10feedsAdapter;
import com.salyangoz.biz.CallServiceFunctions;
import com.salyangoz.features.Settings;
import com.salyangoz.model.Next10feeds;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class FeedActivity extends Activity {
	private CallServiceFunctions csf;
	private List<Next10feeds> nextfeeds;
	private ListView lyAnasayfaFeedlist;
	private ImageButton feedbutton;
	private Settings setting;
	private String userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_feed);
		createResource();
		init();
	}

	private void createResource() {
		feedbutton = (ImageButton) findViewById(R.id.btnAnasayfaFeed);
		feedbutton.setBackgroundResource(R.drawable.selected_menu);
		lyAnasayfaFeedlist = (ListView) findViewById(R.id.lyAnasayfaListview);
		setting = new Settings();
		userid = setting.load("userid", this);
	}

	private void init() {

		csf = new CallServiceFunctions();

		nextfeeds = new ArrayList<Next10feeds>();
		lyAnasayfaFeedlist.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						lyAnasayfaFeedlist.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						try {
							getfeed();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

	}

	public void getfeed() throws InterruptedException, ExecutionException {
		nextfeeds = csf.next10feeds(0, userid);
		lyAnasayfaFeedlist.setAdapter(new CustomNext10feedsAdapter(
				getApplicationContext(), nextfeeds));
	}

	public void profile(View v) {
		Intent profileintent = new Intent(getApplicationContext(),
				ProfileActivity.class);
		profileintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(profileintent);
	}

	public void feed(View v) {
		Intent feedintent = new Intent(getApplicationContext(),
				FeedActivity.class);
		feedintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(feedintent);
	}

	public void refresh(View v) throws InterruptedException, ExecutionException {
		// Refresh Activity
		getfeed();

	}

	public void notification(View v) {
		Intent notificationintent = new Intent(getApplicationContext(),
				NotificationActivity.class);
		notificationintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(notificationintent);
	}

	public void random(View v) {
		Intent randomintent = new Intent(getApplicationContext(),
				RandomFeed.class);
		randomintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(randomintent);
	}

	public void search(View v) {
		Intent searchintent = new Intent(getApplicationContext(),
				SearchActivity.class);
		searchintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(searchintent);
	}

	public void camera(View v) {
		Intent cameraintent = new Intent(getApplicationContext(),
				CameraActivity.class);
		cameraintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(cameraintent);
	}

}
