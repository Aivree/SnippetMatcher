package com.timkranen.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.timkranen.domain.Message;
import com.timkranen.extra.RoundedTransformation;
import com.timkranen.playpalproject.R;

public class MessageListAdapter extends ArrayAdapter<Message> {

	private Context context;
	private List<Message> messages;

	private ImageView image;
	private TextView messageText;
	private TextView senderName;
	private TextView dateText;
	private LinearLayout mainLayout;
	private LinearLayout messageLayout;

	private SimpleDateFormat dateFormat;

	public MessageListAdapter(Context context, int resource,
			List<Message> objects) {
		super(context, resource, objects);
		this.context = context;
		this.messages = objects;
		this.dateFormat = new SimpleDateFormat("dd-MM-yyyy kk:mm");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) ((Activity) context)
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.message_view_row, null, false);
		if (rowView == null) {
			// LayoutInflater inflater = ((Activity)
			// context).getLayoutInflater();
			// rowView = inflater.inflate(R.layout.message_view_row, null);
			image = (ImageView) rowView.findViewById(R.id.message_row_image);
			messageText = (TextView) rowView
					.findViewById(R.id.message_row_message);
			senderName = (TextView) rowView
					.findViewById(R.id.message_row_senderName);
			dateText = (TextView) rowView.findViewById(R.id.message_row_date);
			mainLayout = (LinearLayout) rowView
					.findViewById(R.id.messaging_view_mainLayout);
			messageLayout = (LinearLayout) rowView
					.findViewById(R.id.message_row_messageLayout);
		} else {
			image = (ImageView) rowView.findViewById(R.id.message_row_image);
			messageText = (TextView) rowView
					.findViewById(R.id.message_row_message);
			senderName = (TextView) rowView
					.findViewById(R.id.message_row_senderName);
			dateText = (TextView) rowView.findViewById(R.id.message_row_date);
			mainLayout = (LinearLayout) rowView
					.findViewById(R.id.messaging_view_mainLayout);
			messageLayout = (LinearLayout) rowView
					.findViewById(R.id.message_row_messageLayout);
		}

		Message msg = messages.get(position);
		View imageFromMain = mainLayout.getChildAt(0);
		View messageLayoutFromMain = mainLayout.getChildAt(1);
		mainLayout.removeAllViews();
		// handle the view stance
		if (!msg.isOwnMessage()) {
			// switch the views
			mainLayout.addView(messageLayoutFromMain);
			mainLayout.addView(imageFromMain);
			// set the color for the message box
			messageLayout.setBackgroundColor(context.getResources().getColor(
					R.color.friend_message_color));
		} else {
			// switch the views
			mainLayout.addView(imageFromMain);
			mainLayout.addView(messageLayoutFromMain);
			messageLayout.setBackgroundColor(context.getResources().getColor(
					R.color.clanster_color));
		}

		messageText.setText(msg.getMessage());
		senderName.setText(msg.getSenderName());
		Date date = msg.getCreatedAt();
		dateText.setText(dateFormat.format(msg.getCreatedAt()));

		if (msg.getSenderImageUrl() != null
				&& !msg.getSenderImageUrl().isEmpty()) {
			Picasso.with(context).load(msg.getSenderImageUrl())
					.transform(new RoundedTransformation()).into(image);
		} else {
			Picasso.with(context).load(R.drawable.defaultimage)
					.transform(new RoundedTransformation()).into(image);
		}

		return rowView;
	}

}
