package com.example.biadu_test01;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.SupportMapFragment;

public class MainActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String LTAG = MainActivity.class.getSimpleName();
	SupportMapFragment mf;
	MapView mMapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		LayoutInflater inflater = getLayoutInflater();
		View dialog_view = inflater.inflate(R.layout.activity_main, null);
		setContentView(dialog_view);
		mMapView = (MapView) dialog_view.findViewById(R.id.map);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
}
