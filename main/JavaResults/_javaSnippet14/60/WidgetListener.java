package com.gufy.widgets;

import com.gufy.R;
import com.gufy.util.AnimationUtil;
import com.gufy.util.RandomUtil;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class WidgetListener implements OnClickListener {
	protected String imageName;
	protected int imageId;
	protected ImageView imageResult;
	protected TextView textResult;
	protected MyWindow parent;
	
	public WidgetListener(MyWindow _parent) {
		parent = _parent;
	}
	
	public View loadData(String[] array){
		
		imageName = array[RandomUtil.randomXToY(0, array.length-1)];
		imageId = parent.getResources().getIdentifier("com.gufy:drawable/" + imageName, null, null);
		LayoutInflater inflater = parent.getWindow().getLayoutInflater();
		View layout = inflater.inflate(R.layout.results,
		                               (ViewGroup) parent.getWindow().findViewById(R.id.resultsLayout));
		imageResult = (ImageView) layout.findViewById(R.id.imageResult);		
		textResult = (TextView) layout.findViewById(R.id.textResult);
		imageResult.setImageDrawable(parent.getResources().getDrawable(imageId));
		return layout;
	}
	
	protected void showResults(View layout){
		LinearLayout containerLayout = new LinearLayout(parent.getWindow().getContext());
		containerLayout.addView(layout);
		containerLayout.setGravity(Gravity.CENTER);
		containerLayout.setId(R.id.resultsLayout);
		// add the view as gone because after that we do our fancy enter with the fadein
		//containerLayout.setAnimation(AnimationUtil.fadeout(1000));
		containerLayout.setVisibility(View.GONE);
		parent.getWindow().addContentView(
				containerLayout, new ViewGroup.LayoutParams(
										ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		containerLayout.setAnimation(AnimationUtil.fadeIn(1000, 500));
		containerLayout.setVisibility(View.VISIBLE);
	}

}
