package edu.rosehulman.listviewbaseadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RowView extends LinearLayout {
	private TextView mLeftTextView;
	private TextView mRightTextView;
	private Context mContext;

	public RowView(Context context) {
		super(context);

		LayoutInflater layoutInflater = ((Activity) context)
				.getLayoutInflater();
		layoutInflater.inflate(R.layout.row_view, this);
		this.mLeftTextView = (TextView) findViewById(R.id.left_text_view);
		this.mRightTextView = (TextView) findViewById(R.id.right_text_view);

		// this.setOrientation(HORIZONTAL);
		// // Create the views for the linear layout
		// this.mLeftTextView = new TextView(context);
		// this.mRightTextView = new TextView(context);
		//
		// // Modify properties
		// this.setBackgroundColor(Color.YELLOW);
		// this.mLeftTextView.setBackgroundColor(Color.CYAN);
		// this.mLeftTextView.setMinimumWidth(60);
		// this.mLeftTextView.setTextColor(Color.DKGRAY);
		// this.mLeftTextView.setTextSize(18);
		// this.mLeftTextView.setPadding(5, 5, 5, 5);
		// this.mRightTextView.setTextColor(Color.BLACK);
		// this.mRightTextView.setTextSize(18);
		// this.mRightTextView.setPadding(10, 5, 10, 5);
		//
		// // Add the views to 'this'
		// this.addView(this.mLeftTextView);
		// this.addView(this.mRightTextView);
	}

	public void setLeftText(String leftText) {
		this.mLeftTextView.setText(leftText);
	}

	public void setRightText(String rightText) {
		this.mRightTextView.setText(rightText);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RowView view;
		if (convertView == null) {
			view = new RowView(this.mContext);
		} else {
			view = (RowView) convertView;
		}
		view.setLeftText(" " + (position + 1) + ". ");
		view.setRightText("Dave Fisher");
		return view;
	}
}