package com.example.venturemeet.venturemeet;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Sergio Esteves on 10/25/2014.
 */
public class InfoActivity extends Activity{
    /*
    private TextView mTextView;
    GridViewPager gridViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final LayoutInflater inflater = getLayoutInflater();
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                gridViewPager = (GridViewPager) findViewById(R.id.pager);
                gridViewPager.setAdapter(new MyGridViewPagerAdapter());
                gridViewPager.addView(inflater.inflate(R.layout.selector_generic, stub));;
            }
        });
    }

    private class MyGridViewPagerAdapter extends GridPagerAdapter {
        @Override
        protected void destroyItem(ViewGroup arg0, int arg1, int arg2, Object arg3) {
        }

        @Override
        public int getColumnCount(int arg0) {
            return 1;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        protected Object instantiateItem(ViewGroup arg0, int arg1, int arg2) {
            return null;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return false;
        }
    }
    */
}
