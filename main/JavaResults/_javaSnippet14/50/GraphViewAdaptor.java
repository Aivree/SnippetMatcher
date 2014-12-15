package com.fyp.ratha;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GraphViewAdaptor extends ArrayAdapter<MultiLineGraph>{

	private ArrayList<MultiLineGraph> graphlist;
	int resourceId;
	Context context;
	
	public GraphViewAdaptor(Context context, int resource, ArrayList<MultiLineGraph> graphlist) {
		super(context, resource, graphlist);		
		this.graphlist = graphlist;
		this.resourceId=resource;
		this.context=context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

	    View row = convertView;
		
		if (row == null) {
			   LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			   row = inflater.inflate(resourceId, parent, false);					   
		}

		MultiLineGraph item = graphlist.get(position);		
		LinearLayout layout = (LinearLayout)row.findViewById(R.id.listitem);
		//TextView tt=(TextView)row.findViewById(R.id.textView1);
		//tt.setText("DDD");
		layout.addView(item.getView(context));
		item.getView(context).repaint();
		
		return row;		
	}
	

}
