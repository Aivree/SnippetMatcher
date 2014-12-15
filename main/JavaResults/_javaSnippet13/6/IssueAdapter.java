package com.change.kranti;


import android.app.ListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import model.Issue;

import java.util.List;

public class IssueAdapter extends ArrayAdapter<Issue>{

    private LayoutInflater inflater;

    public IssueAdapter(Context context, int textViewResourceId, List<Issue> issues) {
        super(context, textViewResourceId,issues);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = convertView;
        if(row == null ){
            row = inflater.inflate(R.layout.issue_item,parent, false);
        }
        TextView title = (TextView) row.findViewById(R.id.title);
        title.setText(getItem(position).getTitle());
        TextView desc = (TextView) row.findViewById(R.id.description);
        desc.setText(getItem(position).getDescription());

        return row;
    }
}
