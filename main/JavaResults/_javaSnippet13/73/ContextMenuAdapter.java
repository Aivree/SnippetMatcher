package com.Flanaria;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContextMenuAdapter extends ArrayAdapter<ContextMenuItem>
{
	private Context _context;

	public ContextMenuAdapter(Context context, int textViewResourceId, List<ContextMenuItem> list)
	{
		super(context, textViewResourceId, list);
		_context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(view == null)
		{
			view = inflater.inflate(R.layout.menuitem, null);
		}
		ContextMenuItem item = getItem(position);
		if(item != null)
		{
			((TextView)view.findViewById(R.id.OptionMenu_Text)).setText(item.getDisplayName());
			((ImageView)view.findViewById(R.id.OptionMenu_Image)).setImageResource(item.getIconResource());
		}
		return view;
	}
}
