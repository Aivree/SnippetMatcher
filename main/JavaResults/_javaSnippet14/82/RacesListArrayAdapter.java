package com.duvallsoftware.greyhound.adapters;

import com.duvallsoftware.greyhound.R;
import com.duvallsoftware.greyhound.R.id;
import com.duvallsoftware.greyhound.R.layout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RacesListArrayAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] names;
	private final String[] descriptions;
	private final Integer[] imageId;
	private final Integer[] ids;

	public RacesListArrayAdapter(Activity context, Integer[] ids, String[] names, String[] descriptions, Integer[] imageId) {
		super(context, R.layout.races_list_layout, names);
		this.context = context;
		this.names = names;
		this.descriptions = descriptions;
		this.imageId = imageId;
		this.ids = ids;

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.races_list_layout, null, true);
		
		TextView txtTitle = (TextView) rowView.findViewById(R.id.races_datetime);
		txtTitle.setText(names[position]);
		
		TextView txtDescription = (TextView) rowView.findViewById(R.id.races_description);
		txtDescription.setText(descriptions[position]);
				
//		AnalogCustomClock clock = new AnalogCustomClock(context);
//		clock.setTime(18, 25, 45);
//		LinearLayout l = ((LinearLayout)(rowView.findViewById(R.id.race_clock)));
//		l.addView(clock);
				
//		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//		imageView.setImageResource(imageId[position]);
		
		return rowView;
	}
	
	@Override
	public long getItemId(int position) {
		if( ids.length > position)
			return ids[position];
		
		return super.getItemId(position);
	}
}
