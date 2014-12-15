package com.example.weborganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.list2.R;
import com.example.weborganizer.Containers.Filter;

import java.util.ArrayList;

/**
 * Created by Sasha on 10.11.13.
 */
public class SpinnerAdapter extends ArrayAdapter<Filter> {
Context context;

    public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<Filter> items) {
        super(context, textViewResourceId, items);
        this.context = context;

    }



    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_item, null);
        }

        Filter item = getItem(position);
        if (item!= null) {

            TextView itemView = (TextView) view.findViewById(R.id.textView4);
            if (itemView != null) {

                itemView.setText(item.filterName);
            }
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_item, null);
        }

        Filter item = getItem(position);
        if (item!= null) {

            TextView itemView = (TextView) view.findViewById(R.id.textView4);
            if (itemView != null) {

                itemView.setText(item.filterName+" ");
            }
        }

        return view;
    }

}
