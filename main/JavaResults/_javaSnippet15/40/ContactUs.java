package com.example.autoapp;

import com.example.autoapp.R.*;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactUs extends Activity {
	public static String[] names={ "Call Us Today","Cantact Us  via Email","Like Us on  Facebook"};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_us);
		
		LinearLayout linearlayout=(LinearLayout) findViewById(R.id.contactusmain);
		LayoutInflater in=getLayoutInflater();
		linearlayout.setBackgroundResource(R.drawable.bg1);
		for(int i=0;i<names.length;i++)
		{
			View view=in.inflate(R.layout.contact_us, null, false);
			TextView tv=(TextView) view.findViewById(R.id.contacttitle);
			tv.setText(names[i]);
			linearlayout.addView(view);
		}
	}



}
