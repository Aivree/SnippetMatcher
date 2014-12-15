package com.ekranac.root.layoutinflatertest;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayout inflateButton = (LinearLayout) findViewById(R.id.inflate);

        inflateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.relativeLayout);

                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.sublayout, null, false);
                linearLayout.addView(v);
            }
        });


    }
}
