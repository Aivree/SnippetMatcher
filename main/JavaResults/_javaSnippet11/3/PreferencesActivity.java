package com.frisodenijs.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Warning: I'm not proud of this code!!
 */

public class PreferencesActivity extends ActionBarActivity {

    SharedPreferences sharedPreferences;

    private EditText playerOneName;
    private RadioButton playerOneIconX;
    private RadioButton playerOneIconO;

    private EditText playerTwoName;
    private RadioButton firstMoveX;
    private RadioButton firstMoveO;
    private RadioButton firstMoveXO;

    private RadioButton easyMode;
    private RadioButton hardMode;

    private CheckBox testingOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        playerOneName = (EditText) findViewById(R.id.setPlayerOneName);
        playerTwoName = (EditText) findViewById(R.id.setPlayerTwoName);

        playerOneIconX = (RadioButton) findViewById(R.id.playerOneIconX);
        playerOneIconO = (RadioButton) findViewById(R.id.playerOneIconO);

        firstMoveX = (RadioButton) findViewById(R.id.firstMoveX);
        firstMoveO = (RadioButton) findViewById(R.id.firstMoveO);
        firstMoveXO = (RadioButton) findViewById(R.id.firstMoveXO);

        easyMode = (RadioButton) findViewById(R.id.easyMode);
        hardMode = (RadioButton) findViewById(R.id.hardMode);

        testingOptions = (CheckBox) findViewById(R.id.testingCheckbox);

        if(savedInstanceState == null)
        {
            playerOneName.setText(sharedPreferences.getString("playerOneName", "Player 1"));
            playerTwoName.setText(sharedPreferences.getString("playerTwoName", "Player 2"));

            playerOneIconX.setChecked(sharedPreferences.getBoolean("playerOneIconX", true));
            playerOneIconO.setChecked(sharedPreferences.getBoolean("playerOneIconO", false));

            firstMoveX.setChecked(sharedPreferences.getBoolean("firstMoveX", true));
            firstMoveO.setChecked(sharedPreferences.getBoolean("firstMoveO", false));
            firstMoveXO.setChecked(sharedPreferences.getBoolean("firstMoveXO", false));

            easyMode.setChecked(sharedPreferences.getBoolean("easyMode", true));
            hardMode.setChecked(sharedPreferences.getBoolean("hardMode", false));

            testingOptions.setChecked(sharedPreferences.getBoolean("testingOptions", false));

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("playerOneName", playerOneName.getText().toString());
        outState.putString("playerTwoName", playerTwoName.getText().toString());

        outState.putBoolean("playerOneIconX", playerOneIconX.isChecked());
        outState.putBoolean("playerOneIconO", playerOneIconO.isChecked());

        outState.putBoolean("firstMoveX", firstMoveX.isChecked());
        outState.putBoolean("firstMoveO", firstMoveO.isChecked());
        outState.putBoolean("firstMoveXO", firstMoveXO.isChecked());

        outState.putBoolean("easyMode", easyMode.isChecked());
        outState.putBoolean("hardMode", hardMode.isChecked());

        outState.putBoolean("testingOptions", testingOptions.isChecked());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        playerOneName.setText(savedInstanceState.getString("playerOneName"));
        playerTwoName.setText(savedInstanceState.getString("playerTwoName"));

        playerOneIconX.setChecked(savedInstanceState.getBoolean("playerOneIconX"));
        playerOneIconO.setChecked(savedInstanceState.getBoolean("playerOneIconO"));

        firstMoveX.setChecked(savedInstanceState.getBoolean("firstMoveX"));
        firstMoveO.setChecked(savedInstanceState.getBoolean("firstMoveO"));
        firstMoveXO.setChecked(savedInstanceState.getBoolean("firstMoveXO"));

        easyMode.setChecked(savedInstanceState.getBoolean("easyMode"));
        hardMode.setChecked(savedInstanceState.getBoolean("hardMode"));

        testingOptions.setChecked(savedInstanceState.getBoolean("testingOptions"));
    }

    public void savePreferences(View view) {

        // Save things in Shared Preferences and go back to main menu
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("playerOneName", playerOneName.getText().toString());
        editor.putString("playerTwoName", playerTwoName.getText().toString());

        editor.putBoolean("playerOneIconX", playerOneIconX.isChecked());
        editor.putBoolean("playerOneIconO", playerOneIconO.isChecked());

        editor.putBoolean("firstMoveX", firstMoveX.isChecked());
        editor.putBoolean("firstMoveO", firstMoveO.isChecked());
        editor.putBoolean("firstMoveXO", firstMoveXO.isChecked());

        editor.putBoolean("easyMode", easyMode.isChecked());
        editor.putBoolean("hardMode", hardMode.isChecked());

        editor.putBoolean("testingOptions", testingOptions.isChecked());

        // Save all changes made to the preferences
        editor.commit();
        finish();
    }
}
