package com.mc.test_action;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends FragmentActivity
{
	TextView text1, text2, text3;
	ViewPager pager;
	List<View> list;
	OnClickBar clickBar = new OnClickBar();

	public void widget_Init()
	{
		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);

		text1.setOnClickListener(clickBar);
		text2.setOnClickListener(clickBar);
		text3.setOnClickListener(clickBar);

		pager = (ViewPager) findViewById(R.id.pager);
	}

	public void pager_Init()
	{
		list = new ArrayList<View>();
		LayoutInflater layoutInflater = getLayoutInflater();
		list.add(layoutInflater.inflate(R.layout.view1, null));
		list.add(layoutInflater.inflate(R.layout.view2, null));
		list.add(layoutInflater.inflate(R.layout.view3, null));
		pager.setAdapter(new MyPagerAdapter(list));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int arg0)
			{

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{

			}
		});
	}

	class OnClickBar implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.text1:
				pager.setCurrentItem(0);
				break;

			case R.id.text2:
				pager.setCurrentItem(1);
				break;
				
			case R.id.text3:
				pager.setCurrentItem(2);
				break;
				
			default:
				break;
			}
			Log.d("MC",String.valueOf(((TextView)v).getText()));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		widget_Init();
		pager_Init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class MyPagerAdapter extends PagerAdapter
	{
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews)
		{
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object arg2)
		{
			Log.d("MC", "destroy");
			container.removeView(mListViews.get(position));
		}

		@Override
		public int getCount()
		{
			Log.d("MC", "getcount");
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			Log.d("MC", "instantiate");
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			Log.d("MC", "is?");
			return arg0 == (arg1);
		}
	}

	public static class DummySectionFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
