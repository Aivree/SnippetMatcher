package com.example.takahiro_tsuno.rssreader;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContentAdapter extends ArrayAdapter<Content> {

    private final Context context;
    public List<Content> contentList;

    public ContentAdapter(Context context, List<Content> contentList){
        super(context, 0, contentList);
        this.context = context;
        this.contentList = contentList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Content content = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.main_content_row, parent, false);
        TextView textView = (TextView) contentView.findViewById(R.id.main_content_row_string);
        textView.setText(content.title);

        return contentView;
    }
}
