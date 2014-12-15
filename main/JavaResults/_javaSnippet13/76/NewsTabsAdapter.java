package com.armenia.news.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.armenia.news.activity.R;

public class NewsTabsAdapter extends ArrayAdapter<String>{

	public NewsTabsAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.news_tabs_item, null);
		Typeface font= Typeface.createFromAsset(getContext().getAssets(), "arlamu.ttf");
		TextView textView = (TextView)itemView.findViewById(R.id.news_tabs_text);
		textView.setTypeface(font);
		textView.setText(getItem(position));
		return itemView;
	}
	

}
