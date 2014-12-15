package pl.edu.kosttek.adapters;

import java.util.List;

import pl.edu.kosttek.R;
import pl.edu.kosttek.entity.Schedule;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SchedulesArrayAdapter extends ArrayAdapter<Schedule>{
	private final Context context;
	private final List<Schedule> scheduleList;
	
	public SchedulesArrayAdapter(Context context,
			List<Schedule> objects) {
		super(context,R.layout.conference_row_layout , objects);
		scheduleList = objects;
		this.context = context;
		
		// TODO Auto-generated constructor stub
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.conference_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.conf_title);
		textView.setText(scheduleList.get(position).getPresentation().getTitle());
		textView = (TextView) rowView.findViewById(R.id.conf_desc);
		textView.setText(scheduleList.get(position).getPresentation().getDescription());
		textView = (TextView) rowView.findViewById(R.id.conf_start_date);
		textView.setText(scheduleList.get(position).getStartDate().toGMTString());
		textView = (TextView) rowView.findViewById(R.id.conf_end_date);
		textView.setText(scheduleList.get(position).getEndDate().toGMTString());
		return rowView;
	}
	@Override
	public Schedule getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
		
	}
	
}
