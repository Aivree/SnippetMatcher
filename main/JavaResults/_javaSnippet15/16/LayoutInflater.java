package com.example.test_layoutinflater;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		//定义一个布局文件
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		//获取布局片段
		LayoutInflater inflater = getLayoutInflater();
		//应该再判断inflater是不是空指针
		
		//重用布局片段
		View view1 = inflater.inflate(R.layout.layout_inflater, null);
		View view2 = inflater.inflate(R.layout.layout_inflater, null);
		
		//add
		layout.addView(view1);
		layout.addView(view2);
		
		setContentView(layout);
		
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
