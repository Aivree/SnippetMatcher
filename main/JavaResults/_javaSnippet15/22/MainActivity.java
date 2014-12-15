package com.atos.android.p11;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void cargarVista(View v) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.panel);
		
		//LayoutInflater li = getLayoutInflater();
		Context ctx = this;
		LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View vista;
		if (Math.random() < 0.5) 
			vista = li.inflate(R.layout.confirmacion, null);
		else
			vista  = li.inflate(R.layout.credenciales, null);
		
		ll.addView(vista);
	}

}
