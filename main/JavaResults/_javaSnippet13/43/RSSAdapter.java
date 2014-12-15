package se.aleros.bungalow;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RSSAdapter extends ArrayAdapter<RSSItem> {

	public RSSAdapter(Context context) {
		super(context, R.layout.rss_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.rss_item, null);
		}
		TextView tvTitle = (TextView)v.findViewById(R.id.tvTitle);
		TextView tvSummary = (TextView)v.findViewById(R.id.tvSummary);
		RSSItem item = this.getItem(position);
		tvTitle.setText(item.getTitle());
		tvSummary.setText(item.getDescription());
		return v;
	}

}
