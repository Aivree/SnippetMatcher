package com.ff3d.fragmentlistexample;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac-max on 11/11/14.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {

    Context context;

    public CustomArrayAdapter(Context ctx, int resourceID, ArrayList<String> friends){
        super(ctx, resourceID, friends);
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        String friend = getItem(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.fragment_item, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.empty);

        textView.setText(friend);

        return  convertView;
    }

}
