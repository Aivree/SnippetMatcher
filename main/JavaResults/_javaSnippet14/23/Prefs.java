package com.jcpp.droidbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Prefs extends Activity {

	 
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.activity_prefs);
	 }

	 private TextView createNewTextView(String text) {
	     final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	     final TextView textView = new TextView(this);
	     textView.setLayoutParams(lparams);
	     textView.setText(text);
	     return textView;
	 }
	 
	 public void onClick(View v){
		 LinearLayout hiddenLayout = (LinearLayout)findViewById(R.id.linearLayoutX1);
		 final LinearLayout myLayout = (LinearLayout)findViewById(R.id.linear);
		 View hiddenInfo = getLayoutInflater().inflate(R.layout.prefs_single_book, myLayout, false);
		 AlertDialog.Builder alert = new AlertDialog.Builder(this);

		 alert.setTitle("Insert title...");

		 // Set an EditText view to get user input 
		 final EditText input = new EditText(this);
		 alert.setView(input);

		 alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int whichButton) {
		   String value = input.getText().toString();
		   myLayout.addView(createNewTextView(value));
		   }
		 });

		 alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int whichButton) {
		     // Canceled.
		   }
		 });

		 alert.show();
		 
		 myLayout.addView(hiddenInfo);
		 
	 }
	 
	 public void onAdd(View v){
		 LinearLayout wrapper = (LinearLayout) findViewById(R.id.linearLayoutX1);
		 
		 View inflatedView = LayoutInflater.from(this).inflate(R.id.textView1, wrapper, false);
		 wrapper.addView(inflatedView);
		    
	 }

}
