package com.kalidu.codeblue.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kalidu.codeblue.R;
import com.kalidu.codeblue.activities.listQuestionActivity.ListQuestionActivity;
import com.kalidu.codeblue.activities.listQuestionMapActivity.ListQuestionMapActivity;

public class NavBarBuilder {
	
	private Activity activity;

	public NavBarBuilder(Activity activity){
		this.activity = activity;
	}
	
	public void setListeners(){
		((Button) activity.findViewById(R.id.button_navbar_home)).setOnClickListener(
			new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(NavBarBuilder.this.activity, ListQuestionActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					activity.startActivity(intent);
				}
			}
		);
		
		((Button) activity.findViewById(R.id.button_navbar_map)).setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(NavBarBuilder.this.activity, ListQuestionMapActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						activity.startActivity(intent);
					}
				}
			);
		
		((Button) activity.findViewById(R.id.button_navbar_profile)).setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO
						Log.i("TODO", "Implement this!");
					}
				}
			);
	}	

}
