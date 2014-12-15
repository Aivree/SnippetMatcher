package com.example.phplogin;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class BookmarkAdapter extends ArrayAdapter<Bookmark> {
	private Context context;
	private Bookmark bookmark;
	private int resourceLayout;
	
	public BookmarkAdapter(Context context, int resource, List<Bookmark> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resourceLayout = resource;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		bookmark = getItem(position);
		//Calendar c = Calendar.getInstance();
		
		
		
		
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflate.inflate(resourceLayout,parent,false);
		TextView textupdate_at = (TextView) convertView.findViewById(R.id.update_at1);
		TextView textcommenter_name = (TextView) convertView.findViewById(R.id.commenter_name1);
		TextView textMessage = (TextView) convertView.findViewById(R.id.message1);
		TextView textExpired = (TextView) convertView.findViewById(R.id.expired1);
		
		textupdate_at.setText(bookmark.getupdate_at());
		textcommenter_name.setText(bookmark.getcommenter_name());
		textMessage.setText(bookmark.getmessage());
		String Expired = String.valueOf(bookmark.getExpired());
		textExpired.setText(Expired);
		
		
		return convertView;
		
	}

}