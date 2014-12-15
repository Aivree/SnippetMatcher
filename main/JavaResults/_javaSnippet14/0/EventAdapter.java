package com.example.mitchtz.webview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Chris_000 on 12/5/2014.
 */
public class EventAdapter extends ArrayAdapter{
    private Context con;
    private Object[] objs;
    private int ViewRes;

    public EventAdapter(Context context,int resource, Object[] objects) {
        super(context,resource,objects);
        con=context;
        objs=objects;
        ViewRes=resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Assignment A;

        if(row==null){
            LayoutInflater inflater= ((Activity)con).getLayoutInflater();
            LinearLayout layout=(LinearLayout)inflater.inflate(ViewRes,null);
            A=(Assignment)objs[position];

            //Initiate text holders(Views) for event members
            TextView courseView= (TextView)layout.findViewById(R.id.eventCourse);
            TextView nameView=(TextView)layout.findViewById(R.id.eventName);
            TextView dateView=(TextView)layout.findViewById(R.id.eventDate);
            TextView descriptionView=(TextView)layout.findViewById(R.id.eventDescript);
            //TextView timeView=new TextView(getContext());

            //populate holders with member data
            courseView.setText(A.Class);
            dateView.setText(" Due: "+A.Date);
            nameView.setText(A.Name);
            descriptionView.setText(A.Description);

            //wrap together with "row" layout
           /* layout.addView(courseView);
            layout.addView(dateView);
            layout.addView(nameView);
            layout.addView(descriptionView);*/


            row=layout;

        }
        return row;
    }


}
