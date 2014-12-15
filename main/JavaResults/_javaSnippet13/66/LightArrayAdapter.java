package au.com.microcomaustralia.lifxswitch;

import java.util.ArrayList;

import lifx.java.android.light.LFXLight;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LightArrayAdapter extends ArrayAdapter<LFXLight> {
	private final Context context;
	
	public LightArrayAdapter(Context context, ArrayList<LFXLight> lights) {
	    super(
	    	context,
	    	R.layout.activity_light_list_item, R.id.label,
	    	lights);
	    this.context = context;
	  }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LFXLight light = getItem(position);
		LayoutInflater inflater = (LayoutInflater) context
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(
	    	R.layout.activity_light_list_item, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label);
	    textView.setText(light.getLabel());
	    return rowView;
	}

}
