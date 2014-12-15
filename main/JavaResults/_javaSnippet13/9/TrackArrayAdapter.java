package com.mattwaqar.audioguide;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mattwaqar.audioguide.models.Track;

public class TrackArrayAdapter extends ArrayAdapter<Track> {
	public TrackArrayAdapter(Context context, List<Track> tracks) {
		super(context, R.layout.item_track, tracks);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(null == view) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_track, null);
		}
		
		Track track = getItem(position);
		
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvTitle.setText(track.getTitle());
		TextView tvSubtitle = (TextView)view.findViewById(R.id.tvDescription);
		tvSubtitle.setText(track.getDescription());
		return view;
	}
}
