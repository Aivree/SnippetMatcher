package ca.skule.froshapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;




public class DaysEntryAdapter extends ArrayAdapter<Day> {
	
	private Context context;
	
	public DaysEntryAdapter (final Context context){
		super (context, 0);
		this.context = context;
}
	
	@Override
	public View getView (final int position, final View convertView, final ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		
		View dayView = inflater.inflate(R.layout.day_list_item,parent, false);
		TextView title = (TextView)dayView.findViewById(R.id.dayTitle);
		TextView subTitle = (TextView)dayView.findViewById(R.id.daySubTitle);
		
		title.setText(getItem(position).getDay());
		subTitle.setText(getItem(position).getDate());
		
		return dayView;
	}
}
