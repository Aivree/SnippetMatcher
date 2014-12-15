package com.android.main;


import com.android.main.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class eMOCA extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            Intent home = new Intent();
            home.setAction(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        setContentView(R.layout.main);
    }

    public void startTest(View v)
    {
        Intent next = new Intent(eMOCA.this, QTBridge.class);
        next.setAction(Intent.ACTION_MAIN);
        next.addCategory(Intent.CATEGORY_LAUNCHER);
        next.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(next);
    }

    public void startManager(View v)
    {
    	Intent manager = new Intent(eMOCA.this, Manager.class);
        manager.setAction(Intent.ACTION_MAIN);
        manager.addCategory(Intent.CATEGORY_LAUNCHER);
        manager.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    	startActivity(manager);
    }

    public void scoreTest(View v)
    {
        Intent next = new Intent(eMOCA.this, MarkSplashScreen.class);
        next.setAction(Intent.ACTION_MAIN);
        next.addCategory(Intent.CATEGORY_LAUNCHER);
        next.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(next);
    }

    public void about (View v)
    {
        Intent next = new Intent(eMOCA.this, About.class);
        next.setAction(Intent.ACTION_MAIN);
        next.addCategory(Intent.CATEGORY_LAUNCHER);
        next.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(next);
    }

    public void exitMoca(View v)
    {
        Intent home = new Intent();
        home.setAction(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    /**************************************************************************
     * ANDROID FUNCTIONALITY
     *************************************************************************/

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent home = new Intent();
            home.setAction(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
