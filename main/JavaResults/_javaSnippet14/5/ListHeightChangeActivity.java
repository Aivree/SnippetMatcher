package com.example.helloworld.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helloworld.R;

public class ListHeightChangeActivity extends FragmentActivity {

	private ViewGroup box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_height_change);

		// 枠組み
		box = (ViewGroup) findViewById(R.id.height_change_target);

		LayoutInflater inflater = getLayoutInflater();
		box.addView(getContent(inflater, 100));
		box.addView(getContent(inflater, 300));

	}

	private View getContent(LayoutInflater inflater, int height) {
		// 変更させる対象のコンテンツ
		View content = getLayoutInflater().inflate(R.layout.layout_height_change_contents, null,
				false);
		TextView text = (TextView) content.findViewById(R.id.change_target);
		Log.d("Viewの高さ", String.valueOf(text.getHeight()));
		text.setHeight(height);
		// 色も変えちゃう
		text.setBackgroundResource(R.color.blue);
		return content;
	}
}
