package me.ghui.weco.app.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.ghui.weco.app.R;
import me.ghui.weco.app.ui.view.SlidingTabLayout;

/**
 * Created by vann on 8/14/14.
 */
public class HomePage extends WecoPage {
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new HomePageAdapter());
        mSlidingTabLayout = (SlidingTabLayout) view
                .findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.homepage_layout, container, false);
    }

    class HomePageAdapter extends PagerAdapter {

        @Override public int getCount() {
            return 7;
        }

        @Override public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override public CharSequence getPageTitle(int position) {
            return "Item" + position;
        }

        @Override public void destroyItem(ViewGroup container, int position,
                Object object) {
            container.removeView((View) object);
        }

        @Override public Object instantiateItem(ViewGroup container,
                int position) {
            View view = getActivity().getLayoutInflater()
                    .inflate(R.layout.pager_item,
                            container, false);
            container.addView(view);
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText(
                    String.valueOf(position + 1) + "\n111" + "\n222" + "\n222"
                            + "\n222"
                            + "\n222" + "\n222" + "\n222" + "\n222" + "\n222"
                            + "\n222"
                            + "\n222" + "\n222" + "\n222" +
                            "\n222" + "\n222" + "\n222" + "\n222" + "\n222"
                            + "\n222");
            return view;
        }
    }
}
