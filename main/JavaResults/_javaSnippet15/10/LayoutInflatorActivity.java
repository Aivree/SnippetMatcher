package com.paradigmcreatives.layoutinflator;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class LayoutInflatorActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear_layout);
        LayoutInflater inflate = getLayoutInflater();
        View view = inflate.inflate(R.layout.second_main,linear,false);
        linear.addView(view);
    }
}