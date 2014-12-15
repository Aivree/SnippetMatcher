package com.greylocku.lazer;

import com.greylocku.lazer.models.LazerUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlayerArrayAdapter extends ArrayAdapter<LazerUser> {

	private final Context context;

	  public PlayerArrayAdapter(Context context) {
	    super(context, R.layout.player_layout);
	    this.context = context;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.player_layout, parent, false);
		
	    
		View colorBox = rowView.findViewById(R.id.player_color);
		colorBox.setBackgroundColor(getItem(position).getColor());
	    
		TextView textView = (TextView) rowView.findViewById(R.id.player_name_thing);
	    textView.setText(getItem(position).getName());

		
	    return rowView;
	  }
}
