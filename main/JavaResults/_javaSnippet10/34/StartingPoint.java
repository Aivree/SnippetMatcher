package com.sandmarq.smreaderrss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;


import java.util.ArrayList;
import java.util.List;

public class StartingPoint extends Activity implements View.OnClickListener, OnItemSelectedListener {

    SharedPreferences getPrefs;

    int count = 0, numAId, numBId, numCId, numDId, aId, bId, cId, dId, fontSize = 10, prefFontSizeI;
    float checkboxScale;
    String forgroundColor, backgroundColor, prefFontSize;
    Boolean prefTheme;
    char ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_point);
        initialise();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupTopLayout();
        fillBotLayout(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillBotLayout(0);
    }

    private void initialise() {
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefFontSize = getPrefs.getString("fonts", "10");
        prefTheme = getPrefs.getBoolean("theme", false);

        prefFontSizeI = Integer.parseInt(prefFontSize);

        if (prefTheme) {
            forgroundColor = "BLACK";
            backgroundColor = "WHITE";

        } else {
            forgroundColor = "WHITE";
            backgroundColor = "BLACK";
        }

        numAId = 100000;
        numBId = 200000;
        numCId = 300000;
        numDId = 400000;


        switch (getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) {
            case (Configuration.SCREENLAYOUT_SIZE_UNDEFINED):
                fontSize = prefFontSizeI + 0;
                checkboxScale = 1.0F;
                ss = 'U';
                break;
            case (Configuration.SCREENLAYOUT_SIZE_SMALL):
                fontSize = prefFontSizeI + 0;
                checkboxScale = 1.0F;
                ss = 'S';
                break;
            case (Configuration.SCREENLAYOUT_SIZE_NORMAL):
                fontSize = prefFontSizeI + 0;
                checkboxScale = 1.0F;
                ss = 'N';
                break;
            case (Configuration.SCREENLAYOUT_SIZE_LARGE):
                fontSize = prefFontSizeI + 5;
                checkboxScale = 1.0F;
                ss = 'L';
                break;
            case (Configuration.SCREENLAYOUT_SIZE_XLARGE):
                fontSize = prefFontSizeI + 10;
                checkboxScale = 1.0F;
                ss = 'X';
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.starting_point, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent p = new Intent("com.sandmarq.smreaderrss.PREFS");
                startActivity(p);
                break;
            case R.id.about:
                Intent a = new Intent("com.sandmarq.smreaderrss.ABOUT");
                startActivity(a);
                break;
            case R.id.exit:
                finish();
                break;
        }
        return false;
    }

    private void setupTopLayout() {
        final Spinner spinner1 = (Spinner) findViewById(R.id.s1);

        spinner1.setBackgroundColor(Color.GRAY);
        List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        spinner1.setOnItemSelectedListener(this);

        final ImageButton iButton1 = (ImageButton) findViewById(R.id.b1);
        iButton1.setOnClickListener(this);

        final ImageButton iButton2 = (ImageButton) findViewById(R.id.b2);
        iButton2.setOnClickListener(this);
    }

    private void fillBotLayout(int num) {
        // Initialisation des divers variables.
        String statusC[] = {"Aucune", "WIFI", "3G", "WIFI + 3G"};
        String statusCTxt;
        count = num;
        final RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.rl2);
        final TextView newTVTitle[] = new TextView[count];
        final TextView newTVResume[] = new TextView[count];
        final TextView newLink[] = new TextView[count];
        final CheckBox checkBox[] = new CheckBox[count];
        final View newLine1[] = new View[count];

        statusCTxt = statusC[checkConnectionType()];

        // retrouve le bon layout sur lequel travailler.
        relativeLayout2.removeAllViews();

        // Creations des divers texteviews pour resumer des news.
        for (int i = 0; i < count; i++) {
            newLine1[i] = new View(this);
            newTVTitle[i] = new TextView(this);
            newTVResume[i] = new TextView(this);
            newLink[i] = new TextView(this);
            checkBox[i] = new CheckBox(this);

            aId = numAId + i;
            bId = numBId + i;
            cId = numCId + i;
            dId = numDId + i;

            // Dessine le premier separateur.
            newLine1[i].setId(i);
            newLine1[i].setBackgroundColor(Color.GRAY);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2);
            if (i == 0) {
                params1.addRule(RelativeLayout.BELOW, R.id.rl2);
            } else {
                params1.addRule(RelativeLayout.BELOW, newLink[i - 1].getId());

            }
            newLine1[i].setLayoutParams(params1);
            relativeLayout2.addView(newLine1[i]);

            checkBox[i].setId(dId);
            checkBox[i].setText("");
            checkBox[i].setChecked(false);
            checkBox[i].setTextColor(Color.WHITE);
            checkBox[i].setBackgroundColor(Color.GRAY);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                //checkBox[i].setScaleX(checkboxScale);
                //checkBox[i].setScaleY(checkboxScale);
            }
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.BELOW, newLine1[i].getId());
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            checkBox[i].setLayoutParams(params2);
            relativeLayout2.addView(checkBox[i]);

            // Titres.
            newTVTitle[i].setId(aId);
            newTVTitle[i].setGravity(Gravity.CENTER);
            newTVTitle[i].setTextSize(fontSize + 5);
            newTVTitle[i].setText("(Categorie) Titre " + aId);
            newTVTitle[i].setTextColor(Color.BLACK);
            newTVTitle[i].setBackgroundColor(Color.GRAY);
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params3.addRule(RelativeLayout.BELOW, newLine1[i].getId());
            params3.addRule(RelativeLayout.LEFT_OF, checkBox[i].getId());
            newTVTitle[i].setLayoutParams(params3);
            newTVTitle[i].setOnClickListener(this);
            relativeLayout2.addView(newTVTitle[i]);

            // Resumes.
            newTVResume[i].setId(bId);
            newTVResume[i].setTextSize(fontSize);
            newTVResume[i].setText(bId + " " + statusCTxt + " " + ss + " Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume Resume");
            newTVResume[i].setTextColor(Color.WHITE);
            newTVResume[i].setBackgroundColor(Color.BLACK);
            RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params4.addRule(RelativeLayout.BELOW, newTVTitle[i].getId());
            params4.addRule(RelativeLayout.LEFT_OF, checkBox[i].getId());
            newTVResume[i].setLayoutParams(params4);
            newTVResume[i].setOnClickListener(this);
            relativeLayout2.addView(newTVResume[i]);

            // Link.
            newLink[i].setId(cId);
            newLink[i].setTextSize(fontSize - 2);
            newLink[i].setText("Source : http://www.google.com/ " + cId);
            newLink[i].setTextColor(Color.WHITE);
            newLink[i].setBackgroundColor(Color.BLACK);
            RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params5.addRule(RelativeLayout.BELOW, newTVResume[i].getId());
            newLink[i].setLayoutParams(params5);
            newLink[i].setOnClickListener(this);
            relativeLayout2.addView(newLink[i]);

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        /* TODO Auto-generated method stub */
        switch (arg2) {
            case 0:
                fillBotLayout(10);
                break;
            case 1:
                fillBotLayout(20);
                break;
            case 2:
                fillBotLayout(30);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            // Si clique refresh
            case R.id.b1:
                fillBotLayout(5);
                break;
            // Si clique all-read
            case R.id.b2:
                fillBotLayout(0);
                break;
        }

    }

    // Return connection status
    public boolean checkConnectionStatus() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // Status return connection type
    // 0 = none
    // 1 = WIFI
    // 2 = 3G
    // 3 = WIFI,3G
    public int checkConnectionType() {
        int status = 0;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (checkConnectionStatus()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isWifiConn = false;
            if (networkInfo != null) {
                isWifiConn = networkInfo.isConnected();
            }
            if (isWifiConn) {
                status = 1;
            }

            boolean isMobileConn = false;

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                isMobileConn = networkInfo.isConnected();
            }
            if (isMobileConn) {
                switch (status) {
                    case 0:
                        status = 2;
                        break;
                    case 1:
                        status = 3;
                        break;
                }
            }
            return status;
        } else {
            return status;
        }
    }

/*    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
        double myDouble = savedInstanceState.getDouble("myDouble");
        int myInt = savedInstanceState.getInt("MyInt");
        String myString = savedInstanceState.getString("MyString");
    }
    */

}
