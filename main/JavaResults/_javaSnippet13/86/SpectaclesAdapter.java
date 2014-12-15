package com.exia.puydufou.helper.business;

import com.exia.android.puyfou.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class SpectaclesAdapter extends ArrayAdapter{
	
	
	private Context context;


	@SuppressWarnings("unchecked")
	public SpectaclesAdapter(Context context, int textViewResourceId,Object[] nom) {
		super(context, textViewResourceId, R.id.textview_nomSpectacle, nom);
		this.context = context;

	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View res = convertView;

		if (res == null) {
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			res = li.inflate(R.layout.item_spectacle, null);
		} else {
			res = convertView;
		}

		TextView userName = (TextView) res.findViewById(R.id.textview_nomSpectacle);
		userName.setText((String) getItem(position));
		
		
	


		return res;
	}


}
