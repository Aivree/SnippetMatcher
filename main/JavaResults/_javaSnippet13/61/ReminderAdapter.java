package com.huertapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huertapp.model.Reminder;

public class ReminderAdapter extends ArrayAdapter<Reminder> {

	LayoutInflater inflater;
	
	public ReminderAdapter(Context context,
			List<Reminder> objects) {
		super(context, -1, objects);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent,
					false);
		}
		Reminder current = getItem(position);
		((TextView)convertView.findViewById(android.R.id.text1)).setText(current.getName());
		((TextView)convertView.findViewById(android.R.id.text2)).setText(current.getDescription());
	
		return convertView;
	}


	

}
