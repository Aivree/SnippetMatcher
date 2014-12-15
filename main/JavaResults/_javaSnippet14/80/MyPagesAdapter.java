package com.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.activities.SwipePhotos;
import com.frab.R;
import com.objects.RowPhotoScore;
import com.objects.SwipePhotoScore;

public class MyPagesAdapter extends PagerAdapter {
	
	private ArrayList<SwipePhotoScore> photosList;
	private LayoutInflater inflater;
	private Activity activity;

	public MyPagesAdapter(ArrayList<SwipePhotoScore> photosList, LayoutInflater inflater, Activity activity){
		this.photosList = photosList;
		this.inflater = inflater;
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		return photosList.size();
	}
	
	@Override
	public Object instantiateItem (ViewGroup container, int position){
		View page = inflater.inflate(R.layout.swipe_photo_score, null);
		TextView mTvTitle = (TextView) page.findViewById(R.id.tv_title);
		mTvTitle.setText(photosList.get(position).getTitle());
		ImageView mIvPhoto = (ImageView) page.findViewById(R.id.iv_photo);
		mIvPhoto.setImageResource(photosList.get(position).getPhoto());
		TextView mTvUser = (TextView) page.findViewById(R.id.tv_user);
		mTvUser.setText(photosList.get(position).getUserName());
		final RatingBar mRbScore = (RatingBar) page.findViewById(R.id.rb_score);
		mRbScore.setRating(photosList.get(position).getScore());
		final LinearLayout mLlListOfVotes =  (LinearLayout) page.findViewById(R.id.ll_new_list_of_votes);
		View view = activity.getLayoutInflater().inflate(R.layout.vote_fragment, null);
		mLlListOfVotes.addView(view);
		final ArrayList<RowPhotoScore> rowItems = new ArrayList<RowPhotoScore>();
//		rowItems.add(new RowPhotoScore("Arturo", (float) 2.5, R.drawable.podium));
//		rowItems.add(new RowPhotoScore("Vicente", (float) 3.5, R.drawable.caribe));
//		rowItems.add(new RowPhotoScore("Dani", (float) 1.5, R.drawable.megan));
//		rowItems.add(new RowPhotoScore("Dori", (float) 0.5, R.drawable.eiffel));
		mRbScore.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				mLlListOfVotes.removeAllViews();
				for (int i = 0; i < rowItems.size(); i++){
//					View view = activity.getLayoutInflater().inflate(R.layout.user_vote, null);
//					TextView texto = (TextView) view.findViewById(R.id.tv_user_name);
//					texto.setText(rowItems.get(i).getUserName());
//					RatingBar rating1 = (RatingBar) view.findViewById(R.id.rb_user_score);
//					rating1.setRating(rowItems.get(i).getUserScore());
//					ImageView image = (ImageView) view.findViewById(R.id.iv_user_photo);
//					image.setImageResource(rowItems.get(i).getUserPhoto());
//					mLlListOfVotes.addView(view);
				}
				mRbScore.setIsIndicator(true);
			}
		});
		
		mIvPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, SwipePhotos.class);
				ArrayList<SwipePhotoScore> lista = new ArrayList<SwipePhotoScore>();
				lista.add(new SwipePhotoScore("Dani", "FRAB", (float) 3.5, R.drawable.podium));
				lista.add(new SwipePhotoScore("Dori", "FRAB", (float) 2.5, R.drawable.podium));
				Bundle bundle = new Bundle();
				bundle.putSerializable("PHOTOS", lista);
				intent.putExtras(bundle);
				activity.startActivity(intent);
			}
		});
		
		((ViewPager) container).addView(page, 0);
		return page;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (View)arg1;
	}

	@Override
	public void destroyItem	(ViewGroup container, int position, Object object){
		((ViewPager) container).removeView((View) object);
		object=null;
	}
	
}

