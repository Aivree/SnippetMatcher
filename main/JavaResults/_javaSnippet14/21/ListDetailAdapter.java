package com.guruslist;

import java.util.ArrayList;

import com.guruslist.MyListAdapter.MyListHolder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListDetailAdapter extends ArrayAdapter<MyConversation>{

	private String TAG = "ListDetailAdapter";

	Context context;
	int layoutResourceId;
	ArrayList<MyConversation> convs = null;
	ArrayList<MyListLine> lines = null;
	
	public ListDetailAdapter(Context context, int layoutResourceId, ArrayList<MyConversation> convs) {
		super(context, layoutResourceId, convs);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.convs = convs;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();

		LinearLayout row = (LinearLayout) convertView;

		row = (LinearLayout) inflater.inflate(layoutResourceId, parent, false);

		MyConversation conv = convs.get(position);
		
		Integer num_msgs = conv.lines.size();

		for (int m=0; m<num_msgs; m++) {
			MyListLine msg = conv.lines.get(m);
			View line = inflater
					.inflate(R.layout.list_detail_row, row, false);
			
			TextView line_who = (TextView) line
					.findViewById(R.id.list_detail_row_who);
			TextView line_ts = (TextView) line
					.findViewById(R.id.list_detail_row_ts);
			TextView line_data = (TextView) line
					.findViewById(R.id.list_detail_row_data);

			line_who.setText(getAuthorName(msg.who));
		
			line_data.setText(msg.data);
			line_ts.setText(msg.ts);
		
		((ViewGroup) row).addView(line);
		}

		return row;

	}

	static class MyLineHolder {
		TextView who;
		TextView ts;
		TextView data;
	}
	
	private String getAuthorName(String e) {
		if (e != null) {
			return e.replaceFirst("@.*", "");
		} else {
			return "<anonymous>";
		}
	}

}
