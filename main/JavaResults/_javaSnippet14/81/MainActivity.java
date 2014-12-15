package com.example.axsys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Context context;
	String[][] exercises = {
			{"Bicep Curls","<b>90</b> kg","<b>3</b> sets","<b>10</b> reps"},
			{"Deltoid Lateral Raises","<b>80</b> kg","<b>3</b> sets","<b>10</b> reps"},
			{"Hammer Curls","<b>100</b> kg","<b>3</b> sets","<b>10</b> reps"},
			{"Squat","--kg","<b>3</b> sets","<b>10</b> reps"}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context =  this;
		LinearLayout layout = (LinearLayout)findViewById(R.id.main_layout);
		LayoutInflater inflater = getLayoutInflater();
		
		// Set welcome text
		TextView greet = (TextView)findViewById(R.id.txt_greet);
		String text = "Hi Adrian, today\'s workout is <font color=#5AC1BC>Arms/Legs</font>.";
		greet.setText(Html.fromHtml(text));
		
		// Set Button text
		Button start = (Button)findViewById(R.id.btn_lets_start);
		start.setText(Html.fromHtml(getString(R.string.lets_start)));
		
		for(int i=0;i<4;i++){
			View v = (View)inflater.inflate(R.layout.exercise_ist_item, null);
			TextView exerciseName = (TextView)v.findViewById(R.id.exercise_name);
			TextView exerciseWeight = (TextView)v.findViewById(R.id.exercise_weight);
			TextView exerciseSet = (TextView)v.findViewById(R.id.exercise_set);
			TextView exerciseRep = (TextView)v.findViewById(R.id.exercise_rep);
			exerciseName.setText(exercises[i][0]);
			exerciseWeight.setText(Html.fromHtml(exercises[i][1]));
			exerciseSet.setText(Html.fromHtml(exercises[i][2]));
			exerciseRep.setText(Html.fromHtml(exercises[i][3]));
			v.setOnClickListener(clickListener);
			layout.addView(v, i);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void showSomething(View v) {
		Toast.makeText(context, "Something....", Toast.LENGTH_SHORT).show();;
	}
	
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			Toast.makeText(MainActivity.this, "I am clicked", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(context, ExerciseActivity.class));
			
		}
	};

}
