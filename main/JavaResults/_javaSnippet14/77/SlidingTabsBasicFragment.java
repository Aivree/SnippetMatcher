package com.android.slidingtabsbasic;

import com.android.common.view.SlidingTabLayout;
import com.example.android.slidingtabsbasic.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SlidingTabsBasicFragment extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);        
        mViewPager.setAdapter(new SamplePagerAdapter());
        
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);        
        
        ActionBar bar = getActivity().getActionBar();
		bar.setTitle("나만의 레시피");
		
        mSlidingTabLayout.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				
				if(position == 0 ){
	        		ActionBar bar = getActivity().getActionBar();
	        		bar.setTitle("나만의 레시피");
	        	}
	        	
	        	else if(position == 1 ){
	        		ActionBar bar = getActivity().getActionBar();
	        		bar.setTitle("완제품 레시피");
	        	}
	        	
	        	else if(position == 2 ){
	        		ActionBar bar = getActivity().getActionBar();
	        		bar.setTitle("검색하기");
	        	}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
    }
    
    // PageAdapter
    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

       
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }
        
        @Override
        public int getItemPosition(Object object) {
        	// TODO Auto-generated method stub
        	return super.getItemPosition(object);
        }
        
        
        @Override
        public CharSequence getPageTitle(int position) {
        	
            return "";
        }
      
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
        	
        	if( position == 0 )
        	{
        		View view = getActivity().getLayoutInflater().inflate(R.layout.myrcp_item,container, false);            
                container.addView(view);
                
                return view;
        	}
        	else if( position == 1 )
        	{
				View view = getActivity().getLayoutInflater().inflate(
						R.layout.pager_item, container, false);
				container.addView(view);
				TextView title = (TextView) view.findViewById(R.id.item_title);
				title.setText(String.valueOf(position + 1));
				return view;
			}
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            
        }

    }
}
