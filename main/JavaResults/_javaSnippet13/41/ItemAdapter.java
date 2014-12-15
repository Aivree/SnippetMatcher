package com.example.fragmentsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter<ItemInfo>{

	public ItemAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}

	@Override
	  public  View getView(int position,  View convertView,  ViewGroup parent){
		View contentView = null;
	
		LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.item_list_fragment, null);
		
		ItemInfo info = (ItemInfo)this.getItem(position);
		
		TextView tvTitle = (TextView)contentView.findViewById(R.id.tvTitle);
		tvTitle.setText(info.title);
		TextView tvCreateTime = (TextView)contentView.findViewById(R.id.tvTime);
		tvCreateTime.setText(info.getcreateDateString());
		
		return contentView;
	}
}
