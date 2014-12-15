package com.salyangoz.yourchoice;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class TagsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags);
	}

public void search(View v){
	Intent searchintent= new Intent(getApplicationContext(),TagsActivity.class);
	searchintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	startActivity(searchintent);
}
}
