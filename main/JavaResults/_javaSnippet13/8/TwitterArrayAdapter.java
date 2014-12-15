package com.realdream.activity.main.TwitterActivity01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TwitterArrayAdapter extends ArrayAdapter<twitter4j.Status> {

	/** */
	private Context				context	= null;

	public TwitterArrayAdapter(Context context) {
		super(context, R.layout.activity_main);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = null;
		View rowView = null;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.layout_twitter_item, parent, false);

		TextView date = (TextView) rowView.findViewById(R.id.text_view_twit_date);
		TextView message = (TextView) rowView.findViewById(R.id.text_view_twit_message);

		twitter4j.Status status = getItem(position);

		date.setText(status.getCreatedAt().toString());
		message.setText(status.getText());

		return rowView;
	}
}
