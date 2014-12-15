package com.placella.bmi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

/**
 * This class represents the activity that displays
 * the result of the calculation to the user
 *
 * @author Rouslan Placella
 */
public class Output extends Activity {
    private Output self = this;
    private double height = 0;
    private double weight = 0;
    /**
     * Whether we are using imperial measurements
     */
    private boolean imperial = false;
    /**
     * Holds the calculated BMI value
     */
    private double bmi = 0;

    /**
     * Called when the activity is starting
     * Calculates the BMI and displays the value to the user
     *
     * @param savedInstanceState This bundle may contain saved data
     * @override
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Draw the layout elements on screen
        setContentView(R.layout.activity_output);
        height = getIntent().getDoubleExtra("height", 0);
        weight = (int) getIntent().getIntExtra("weight", 0);
        imperial = (boolean) getIntent().getBooleanExtra("imperial", false);
        bmi = (weight / (height*height));
        if (imperial) {
            bmi *= 703;
        }
        TextView result = (TextView) findViewById(R.id.result);
        result.setText(
            String.format(
                result.getText().toString(),
                bmi
            )
        );
        // Bind click events
        Button button = (Button) findViewById(R.id.more_info);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(self, LinkList.class));
            }
        });
    }

    /**
     * This function creates the "chart" that show the user's
     * BMI value on a scale. The onCreate event triggers too early
     * to get the width of the images, hence this is done here.
     *
     * @param hasFocus Whether the window of this activity has focus
     * @override
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ImageView marker = (ImageView) findViewById(R.id.marker);
        ImageView bar = (ImageView) findViewById(R.id.bar);
        int padding = marker.getWidth() / 2;
        bar.setPadding(padding, 0, padding, 0);
        int width = bar.getWidth();
        int offset = 0;
        if (bmi > 32.5) {
            offset = width - marker.getWidth();
        } else if (bmi > 17.5) {
            offset = (int)((bmi - 17.5) / (32.5 - 17.5) * (width - marker.getWidth()));
        }
        marker.setPadding(offset, 0, 0, 0);
    }

    /**
     * Displays the options menu
     *
     * @param menu The options menu in which to place the items
     * @override
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Called whenever an item in the options
     * menu is selected. Here we leave the dedicated
     * MenuHandler class take care of the request
     *
     * @param item The menu item that was selected
     * @override
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuHandler.onOptionsItemSelected(item, this);
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the state of the activity
     *
     * @param outState Bundle in which to save data
     * @override
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("height", height);
        outState.putDouble("weight", weight);
        outState.putBoolean("imperial", imperial);
        outState.putDouble("bmi", bmi);
    }

    /**
     * Restores the state of the activity
     *
     * @param outState The data supplied in onSaveInstanceState()
     * @override
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        height = savedInstanceState.getDouble("height");
        weight = savedInstanceState.getDouble("weight");
        imperial = savedInstanceState.getBoolean("imperial");
        bmi = savedInstanceState.getDouble("bmi");
    }
}
