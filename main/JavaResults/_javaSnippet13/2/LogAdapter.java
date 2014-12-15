package net.jlamothe.foodlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Vector;

public class LogAdapter extends ArrayAdapter<Log>
{
    public LogAdapter(Context context, Vector<Log> objects)
    {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view = (LinearLayout) convertView;
        if(view == null) {
            LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (LinearLayout) inflater.inflate(R.layout.log_cell, null);
        }
        TextView dateView = (TextView) view.findViewById(R.id.log_cell_date);
        dateView.setText(getItem(position).date.toString());
        TextView descriptionView = (TextView) view.findViewById(R.id.log_cell_description);
        descriptionView.setText(getItem(position).description);
        return view;
    }
}
