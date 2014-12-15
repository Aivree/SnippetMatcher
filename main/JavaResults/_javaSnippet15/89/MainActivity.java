package flash.android.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity
{
    private ViewPagerClip mClip;
    private List<View> mViews=new ArrayList<View>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mClip = (ViewPagerClip) findViewById(R.id.jazzy_pager);
        mClip.setAdapter(new MainAdapter());
        mClip.setPageMargin(0);
        getLayoutInflater();
        //
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.one,null);  
        View view2 = inflater.inflate(R.layout.two,null);  
        View view3 = inflater.inflate(R.layout.three,null);
//        mClip.addView(view1);
//        mClip.addView(view2);
//        mClip.addView(view3);
        mViews.add(view1);
        mViews.add(view2);
        mViews.add(view3);
        mClip.setViews(mViews);
    }

    private class MainAdapter extends PagerAdapter
    {
        @Override
        public Object instantiateItem(ViewGroup container, final int position)
        {
//            LayoutInflater inflater = getLayoutInflater();
//            View v = inflater.inflate(R.layout.one, null);
//            container.addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//            mJazzy.setObjectForPosition(v, position);
//            return v;
            container.addView(mViews.get(position));  
            return mViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj)
        {
            container.removeView(mClip.getView(position));
        }

        @Override
        public int getCount()
        {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj)
        {
            return view == obj;
        }
    }
}
