package at.cube.app.dashboard;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.cube.app.R;

public class DeviceAdapter extends ArrayAdapter<ListItem> {
	private Context context;
	private List<ListItem> devices;

	public DeviceAdapter(Context context, List<ListItem> objects) {
		super(context, 0, objects);
		this.context = context;
		this.devices = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		if (devices.get(position).isSection()) {
			Location location = (Location) devices.get(position);
			row = inflater.inflate(R.layout.dashboard_list_item_section,
					parent, false);
			TextView tv = (TextView) row
					.findViewById(R.id.dashboard_section_title);
			tv.setText(location.getName());
		} else {
			Device device = (Device) devices.get(position);
			row = inflater.inflate(R.layout.dashboard_list_item, parent, false);
			TextView title = (TextView) row.findViewById(R.id.deviceTitle);
			//FrameLayout controlWrapper = (FrameLayout) row.findViewById(R.id.deviceControl);
			title.setText(device.getName());
			// controlWrapper.addView(device.getPlugin().getView(context));
		}
		return row;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}
}
