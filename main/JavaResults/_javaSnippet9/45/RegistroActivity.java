package com.infomovil.quiz1vs1.aplicacion;

import com.infomovil.quiz1vs1.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

public class RegistroActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registro1);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
	}
}
