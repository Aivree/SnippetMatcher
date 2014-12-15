package com.ganlu.tomoko;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class StatusActivity extends Activity {
    private StatusDataSource dtStatus;
    private ArrayList<Status> statusList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        dtStatus = new StatusDataSource(this);
        dtStatus.open();

        statusList = dtStatus.getAllStatus();

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.statusContainer);

        for (Status s : statusList){
            View temp = getLayoutInflater().inflate(R.layout.statistic_row , null);
            ((TextView)temp.findViewById(R.id.txtCharacter)).setText(s.getCharacter() + " (" + s.getRome() + ")");
            ((TextView)temp.findViewById(R.id.txtRatio)).setText(String.valueOf((int)(s.getRatio() * 100)) + "%" );

            int w1 = (int)(180 * s.getRatio());
            int w2 = 180 - w1;
            temp.findViewById(R.id.percentageRight).getLayoutParams().width = w1;
            temp.findViewById(R.id.percentageWrong).getLayoutParams().width = w2;
            linearLayout.addView(temp);
        }
    }
    @Override
    public void onBackPressed() {
        finish();//go back to the previous Activity
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
