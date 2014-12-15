package com.exia.puydufou.helper.business;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.exia.android.puyfou.R;

public class ShopAdapter extends ArrayAdapter{
	
	private Context context;

	@SuppressWarnings("unchecked")
	public ShopAdapter(Context context, int textViewResourceId,Object[] nom) {
		super(context, textViewResourceId, R.id.textview_nomShop, nom);
		this.context = context;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View res = convertView;

		if (res == null) {
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			res = li.inflate(R.layout.item_shop, null);
		} else {
			res = convertView;
		}

		TextView userName = (TextView) res.findViewById(R.id.textview_nomShop);
		userName.setText((String) getItem(position));
		
	
 		


		return res;
	}
}
