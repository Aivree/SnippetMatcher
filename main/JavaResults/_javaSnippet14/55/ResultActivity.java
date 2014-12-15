package com.xsp.themajority;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class ResultActivity extends Activity {

	private int max = 1000;
	private int totalLength = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup layout = (ViewGroup) inflater.inflate(
				R.layout.activity_result, null);
				
				// Get the message from the intent
    //Intent intent = getIntent();
    // String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		String questionContent = "dddd";
		List<String> answers = new ArrayList<String>();
		answers.add("ϲ");
		answers.add("bac");
		List<Integer> votes = new ArrayList<Integer>();// TODO get the votes
		votes.add(600);
		votes.add(400);
		TextView tv = (TextView) layout.findViewById(R.id.question_content);
		tv.setText(questionContent);

		for (int i = 0; i < answers.size(); i++) {
			String answer = answers.get(i);
			final int vote = votes.get(i);
			TextView ansTv = (TextView) View.inflate(getBaseContext(),
					android.R.layout.simple_expandable_list_item_1, null);
			ansTv.setText(answer);
			layout.addView(ansTv);
			View view = new View(this) {
				Paint p = new Paint();

				@Override
				protected void onDraw(Canvas canvas) {
					p.setColor(Color.RED);// TODO
					super.onDraw(canvas);
					int position = (int) (vote * 1.0 / max * totalLength);
					canvas.drawRect(0, 0, position, 80, p);
					p.setColor(Color.BLACK);
					p.setTextSize(50);
					canvas.drawText("" + (vote * 100. / max) + "%",
							position + 10, 80, p);

				}

			};
			LayoutParams params = new LayoutParams(600, 100);
			view.setLayoutParams(params);
			layout.addView(view);
		}
		setContentView(layout);
	}
}
