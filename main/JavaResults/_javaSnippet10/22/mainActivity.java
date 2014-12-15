package com.example.shafiab.timecalculator;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;


public class mainActivity extends Activity implements timeKeyboardFragment.OnKeyPressListenerTime, factorKeyboardFragment.OnKeyPressListenerFactor, timeDisplay.TimeDisplayInterface, factorDisplay.FactorDisplayInterface {


    String LOG_TAG = "Shafi Main Debug";

    // fragment transaction variables
    FragmentManager fm;
    FragmentTransaction ft;

    // fragment variables, keyboard
    timeKeyboardFragment my_timeKeyboard;
    factorKeyboardFragment my_factorKeyboard;

    // fragment variables, display
    timeDisplay my_timeDisplay;
    factorDisplay my_factorDisplay;

    // operations variable
    timeUI my_timeUI;
    factorUI my_factorUI;
    time resultTime;
    double factor;
    int operation;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getFragmentManager();
        setContentView(R.layout.activity_main);

        my_timeDisplay = (timeDisplay) getFragmentManager().findFragmentByTag("time display");
        my_factorDisplay = (factorDisplay) getFragmentManager().findFragmentByTag("factor display");
        my_timeKeyboard = (timeKeyboardFragment) getFragmentManager().findFragmentByTag("time keyboard");
        my_factorKeyboard = (factorKeyboardFragment) getFragmentManager().findFragmentByTag("factor keyboard");

        // create time fragments
        if (my_timeKeyboard == null) my_timeKeyboard = new timeKeyboardFragment();
        if (my_timeDisplay == null) my_timeDisplay = new timeDisplay();

        // create factor fragments
        if (my_factorKeyboard == null) my_factorKeyboard = new factorKeyboardFragment();
        if (my_factorDisplay == null) my_factorDisplay = new factorDisplay();

        initializeUIs();

        if (savedInstanceState==null) {
            SharedPreferences previousSession = getSharedPreferences("MyPrefFile",MODE_PRIVATE);
            my_timeUI.restoreDataFutureSession(previousSession);
            my_factorUI.restoreDataFutureSession(previousSession);
            createTimeFragment();

        }
        else
            RestoreState(savedInstanceState); // restore states

    }

    void initializeUIs()
    {
        // initialize UI
        my_factorUI = new factorUI(this, my_factorDisplay);
        my_timeUI = new timeUI(this, my_timeDisplay);

        // initialize arithmetic
        resultTime = new time();
        factor = 0;
        operation = Utility.equal;
    }


    // interfacing timeKeyboard button function
    @Override
    public void OnKeyPressTime(int key) {
        time t1 = new time();
        boolean flag = my_timeUI.OnKeyPressTime(key, t1);

        if (flag) // if multiply or divide call factor functions
        {
            // save result and operator
            resultTime.setTime(t1);
            operation = Utility.KeyToOperation(key);

            // reset factorUI
            my_factorUI.resetValue();

            // set intermediate values for factor display
            my_factorDisplay.setIntermediateValue(resultTime.toStr(), Utility.convertToDisplayDigitOther(key));

            // create Factor Fragment
            createFactorFragment();
        }

    }




    // interfacing factorKeyboard button function
    @Override
    public void OnKeyPressFactor(int key) {
        boolean isNan = false;
        boolean isFinished = my_factorUI.OnKeyPressFactor(key);

        if (isFinished) // finished operation, return to time display
        {
            // save result and operator, perform arithmetic (multiplication, division)
            factor = my_factorUI.getFactor();
            isNan = (factor==0)&(operation==Utility.divide);
            if (key == R.id.buttonC) // if clear button is pressed
                resultTime.reset();
            else if (!isNan) {
                resultTime = time.performOperation(resultTime, factor, operation);
                if (time.isNaN(resultTime))
                    isNan = true;
            }

            operation = Utility.KeyToOperation(key);

            // set timeUI
            my_timeUI.setValue(resultTime, operation, isNan);

            // create Time Fragment
            createTimeFragment();
        }
    }


    void createTimeFragment()
    {
        ft = fm.beginTransaction();
        ft.replace(R.id.mainActivityBottomFrame, my_timeKeyboard, "time keyboard");
        ft.replace(R.id.mainActivityTopFrame, my_timeDisplay, "time display");
        ft.commit();
    }

    void createFactorFragment()
    {
        ft = fm.beginTransaction();
        ft.replace(R.id.mainActivityTopFrame, my_factorDisplay, "factor display");
        ft.replace(R.id.mainActivityBottomFrame, my_factorKeyboard, "factor keyboard");
        ft.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // main activity variables
        double[] time = resultTime.toArray();
        outState.putDoubleArray("time",time);
        outState.putBoolean("time isMinus",resultTime.isMinus);

        outState.putDouble("factor",factor);
        outState.putInt("operation",operation);


        // timeUI variables
        my_timeUI.save_timeUI(outState);

        // factorUI variables
        my_factorUI.save_factorUI(outState);
    }


    void RestoreState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
        {
            // main activity variables
            factor = savedInstanceState.getDouble("factor");
            operation = savedInstanceState.getInt("operation");
            double[] time = savedInstanceState.getDoubleArray("time");
            resultTime.setTime(time[0],time[1],time[2],savedInstanceState.getBoolean("time isMinus"));

            // timeUI variables
            my_timeUI.restore_timeUI(savedInstanceState);

            // factorUI variables
            my_factorUI.restore_factorUI(savedInstanceState);
        }
    }

    @Override
    public void setTimeDisplay() {
        my_timeUI.restore_timeDisplay();
    }

    @Override
    public void setFactorDisplay() {
        my_factorUI.restore_factorDisplay();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //getContentResolver().delete(TimeContract.TIME_URI, null, null);
        //getContentResolver().delete(TimeContract.FACTOR_URI, null, null);
        saveDataFutureSession();
    }

    void saveDataFutureSession()//
    {
        // save data for future use
        SharedPreferences previousSession = getSharedPreferences("MyPrefFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = previousSession.edit();
        my_timeUI.saveDataFutureSession(editor);
        my_factorUI.saveDataFutureSession(editor);
        editor.commit();
    }
}
