package com.example.venturemeet.venturemeet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mTextView;
    GridViewPager gridViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Commented Line

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final LayoutInflater inflater = getLayoutInflater();
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                gridViewPager = (GridViewPager) findViewById(R.id.pager);
                gridViewPager.setAdapter(new MyGridViewPagerAdapter());
                //gridViewPager.addView(inflater.inflate(R.layout.info, stub));
            }
        });
    }

    private class MyGridViewPagerAdapter extends GridPagerAdapter {
        int rows = 1;
        int columns = 1;
        boolean x = true;

        @Override
        public int getColumnCount(int arg0) {
            return columns;
        }

        @Override
        public int getRowCount() {
            return rows;
        }

        @Override
        protected Object instantiateItem(final ViewGroup container, int row, int col) {
            View view = null;
            if(row == 0 && col == 0 && x){
//                final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_lunch_nearby, container, false);

                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_lunch_nearby, container, false);

                final Button lunchButton = (Button) view.findViewById(R.id.btn_lunch);
                lunchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        x = false;
                        columns = 3;
                        MyGridViewPagerAdapter.this.destroyItem(container,0,0,v);
//                        v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_match, container, false);
                        notifyDataSetChanged();
                    }
                });

//                return view;
            }
            else if(row == 0 && col == 0 && !x){
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_match, container, false);
//                return view;
            }
            else if(row == 0 && col == 1 && !x){
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_more_info, container, false);
            }
            else if(row == 0 && col == 2 && !x){
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_location, container, false);
            }
            else{
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_match, container, false);
//                final TextView textView = (TextView) view.findViewById(R.id.textView);
//                textView.setText(String.format("Page:\n%1$s, %2$s", row, col));

            }

            container.addView(view);
            return view;

        }

        @Override
        protected void destroyItem(ViewGroup container, int row, int col, Object view) {
            container.removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }
}
