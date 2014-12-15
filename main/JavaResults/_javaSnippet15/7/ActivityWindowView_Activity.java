package com.myexample.android.apis.app;

import android.app.Activity;
import android.os.Bundle;

import com.myexample.android.apis.R;

public class ActivityWindowView_Activity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 第一种方式（常用）
        setContentView(R.layout.activity_window_view);
        
        // 第二种方式（先得到PhoneWindow对象，再调用setContentView()方法，参数是layout）
        // getWindow().setContentView(R.layout.main);
        
        // 第二种方式（先得到PhoneWindow对象，再调用setContentView()方法，参数是view）
        // getWindow().setContentView(this.getLayoutInflater().inflate(R.layout.main, null));
        // getWindow().setContentView(LayoutInflater.from(this).inflate(R.layout.main, null));
    }
}