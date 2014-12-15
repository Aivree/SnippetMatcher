package org.saneki.testapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity // ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Get message
		Intent intent = this.getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		
		// Create text view
		//TextView textView = new TextView(this);
		//textView.setTextSize(40);
		//textView.setText(message);
		//
		// Set as activity layout
		//this.setContentView(textView);

		//LayoutInflater inflater = this.getLayoutInflater(this.getContext());
		ThumbnailView thumbnailView = new ThumbnailView(this, null); //(ThumbnailView)inflater.inflate(R.layout.thumbnail, null);
		thumbnailView.setThumbnailSource(this.getResources().getDrawable(R.drawable.questionable_censor));
		this.setContentView(thumbnailView);

		/* This stuff not needed?
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_display_message);

		if(savedInstanceState == null)
		{
			getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.container, new PlaceholderFragment())
				.commit();
		}
		*/
	}

	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/

	/*
	public static class PlaceholderFragment extends Fragment
	{
		public PlaceholderFragment() { }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(
				R.layout.fragment_display_message,
				container,
				false);
			return rootView;
		}
	}
	*/
}
