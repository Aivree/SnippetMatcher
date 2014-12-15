package com.example.nextopic.topics;

import java.util.List;

import com.example.nextopic.R;
import com.example.nextopic.subjects.Subject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SubjectSpinnerAdapter extends ArrayAdapter<Subject>{
	
	private Context context;
	private Subject subject;
	
	public SubjectSpinnerAdapter(Context context, int textViewResourceId, List<Subject> sujectList) {
		super(context, textViewResourceId, sujectList);
		this.context = context;
	}
	
	/*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		subject = getItem(position);
    	LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View row = inflater.inflate(R.layout.row_spinner, parent, false);
    	TextView subjectTextView = (TextView)row.findViewById(R.id.textView1);
    	subjectTextView.setText(subject.getSubject());
    	subjectTextView.setTextColor(Color.BLUE);
    	return row;
    }*/

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
    	subject = getItem(position);
    	LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View row = inflater.inflate(R.layout.row_spinner, parent, false);
    	TextView subjectTextView = (TextView)row.findViewById(R.id.textViewEventHour);
    	subjectTextView.setText(subject.getSubject());
    	subjectTextView.setTextColor(Color.BLUE);
    	return row;
    }
    
    /*public View getCustomView(int position, View convertView, ViewGroup parent){
    	subject = getItem(position);
    	LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View row = inflater.inflate(R.layout.row_spinner, parent, false);
    	TextView subjectTextView = (TextView)row.findViewById(R.id.textView1);
    	subjectTextView.setText(subject.getSubject());
    	subjectTextView.setTextColor(Color.BLUE);
    	return row;
    }*/
    
}
