package com.fundacao.testemaps.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

/**
 * Created by leandro on 10/03/14.
 */
public class LoginActive extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

}
