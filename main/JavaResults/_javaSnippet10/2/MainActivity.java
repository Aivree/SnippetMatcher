package com.example.teamslipnslide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
	static MediaPlayer mp;
	
	public static MusicState musicstate = new MusicState();
	Intent intent;
	Button play;
	Button options;
	Button credits;
	int counter = 0;
	TextView titleText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	//	Intent intent = new Intent(this, MainActivity.class);
		
		if(musicstate.verify()==0){
			mp= MediaPlayer.create(this, R.raw.cutsound);
			mp.start();
			mp.setLooping(true);
			musicstate.setMedia(mp);
			musicstate.addCounter();
		}
		
		
	}

	final Context context = this;

	public void onClickPlay(View v)
	{
		Intent intent = new Intent(context, PreGameMenu.class);
	    startActivity(intent);
	}
	public void onClickOptions(View v)
	{
		Intent intent = new Intent(context, OptionsActivity.class);
		//intent.putExtra("extra", mp);
		startActivity(intent);
	}
	public void onClickCredits(View v)
	{
		Intent intent = new Intent(context, CreditsActivity.class);
		
	    startActivity(intent);
	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
		  // Save UI state changes to the savedInstanceState.
		  // This bundle will be passed to onCreate if the process is
		  // killed and restarted.
		  // savedInstanceState.putBoolean("MyBoolean", true);
		  // savedInstanceState.putDouble("myDouble", 1.9);
		  savedInstanceState.putInt("MyInt", 1);
		  // savedInstanceState.putString("MyString", "Welcome back to Android");
		  // etc.
		}
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		  super.onRestoreInstanceState(savedInstanceState);
		  // Restore UI state from the savedInstanceState.
		  // This bundle has also been passed to onCreate.
		 // boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
		//  double myDouble = savedInstanceState.getDouble("myDouble");
		 counter = savedInstanceState.getInt("MyInt");
		  //String myString = savedInstanceState.getString("MyString");
		}
}
