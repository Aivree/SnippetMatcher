package com.example.layoutinflater_sample1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */


	//コメントテスト
	//コメントテスト2
	//コメントテスト3

        @Override
        public void onCreate(Bundle savedInstanceState) {
               super.onCreate(savedInstanceState);
               setContentView(R.layout.activity_main);

               final LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);

               findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

                     @Override
                     public void onClick(View v) {
                           layout.removeAllViews();
                           getLayoutInflater().inflate(R.layout.layout1, layout);
                     }
               });

               findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

                      @Override
                      public void onClick(View v) {
                            layout.removeAllViews();
                            getLayoutInflater().inflate(R.layout.layout2, layout);
                     }
                });
           }
 }
