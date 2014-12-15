package com.mick88.superbrain.quiz;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mick88.superbrain.R;
import com.mick88.superbrain.quizzes.Answer;

public class QuizAnswerAdapter extends ArrayAdapter<Answer>
{

	public QuizAnswerAdapter(Context context, List<Answer> objects)
	{
		super(context, 0, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item_answer, parent, false);
		}
		
		TextView tvAnswer = (TextView) view.findViewById(android.R.id.text1),
				tvNumber = (TextView) view.findViewById(android.R.id.text2);
		
		tvAnswer.setText(getItem(position).getText());
		tvNumber.setText(String.valueOf((char)('A'+position))+".");
		return view;
	}
	
}
