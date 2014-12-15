package com.view.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ViewsActivity extends Activity {
	 private String lv_arr[] = { "Android", "iPhone", "BlackBerry", "AndroidPeople", "Symbian", "iPad","Windows Mobile", "Sony","HTC","Motorola" };
	  private String lv_arr2[] = { "Eric Taix", "eric.taix@gmail.com" };

	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    WorkspaceView work = new WorkspaceView(this, null);
	    // Car il y a toujours un petit décalage du doigt même lors d'un scrolling vertical
	    work.setTouchSlop(32);
	    // Chargement de l'image d fond (peut être enlevée)
	    Bitmap backGd = BitmapFactory.decodeResource(getResources(), R.drawable.background_black_1280x1024);
	    work.loadWallpaper(backGd);
	   
	   View v1= inflater.inflate(R.layout.relative_layout, null, false);
	   View v2= inflater.inflate(R.layout.relative_layout, null, false);
	   View v3= inflater.inflate(R.layout.relative_layout, null, false);
	   View child = getLayoutInflater().inflate(R.layout.list, work, false);
	    
	  
	    
	    // Add views to the workspace view
	    work.addView(v1);
	    work.addView(v2);
	    work.addView(v3);
	    work.addView(child);
	    setContentView(work);
	    //setContentView(child);
	  }
}