package ie.cit.geoffwales.bmicalculator;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity {

	double weight = 0.0, height = 0.0, bmiValue = 0.0;
	boolean cm = true, kg = true;
	Button calculateBmi = null;
	TextView display = null;
	EditText weightText = null, heightText = null;
	RadioButton heightButton1 = null, heightButton0 = null;
	RadioButton weightButton1 = null, weightButton0 = null;
	SharedPreferences preferences;
	boolean ok = true;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        preferences = getSharedPreferences("BMICalcFile", 0);
        restoreSavedData();
        setContentView(R.layout.activity_main);
        calculateBmi = (Button) findViewById(R.id.calculateBMI);
        display = (TextView) findViewById(R.id.tvDisplay);
        
		weightText = (EditText) findViewById(R.id.weightText);
		if ((weight > 0.0) ) {
			weightText.setText(String.format("%.1f", weight) );
		}
		
		heightText = (EditText) findViewById(R.id.heightText);
		if ((height > 0.0) ) {
			heightText.setText(String.format("%.1f", height) );
		}
		
		heightButton0 = (RadioButton) findViewById(R.id.radioHeight0);
		weightButton0 = (RadioButton) findViewById(R.id.radioWeight0);

        calculateBmi.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				if (weightText.getText().length() > 0 ) {
					weight = Double.parseDouble(weightText.getText().toString());
				} else {
					weight = 0.0;
				}
				
				if (heightText.getText().length() > 0) {
					height = Double.parseDouble(heightText.getText().toString());
				} else {
					height = 0.0;
				}
				cm = heightButton0.isChecked();
				kg = weightButton0.isChecked();
				checkValues();
			}
        });
        
	}
    
    private void checkValues() { 
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	
	    if ( (weight == 0.0) ) {
	    	builder.setTitle("Warning").setMessage("Weight not set")
	    	.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
	 	       public void onClick(DialogInterface dialog, int id) {
	 	           }
	 	       });
	    	
	    	AlertDialog dialog = builder.create();
	    	dialog.show();
	    	
		} else if ( (height == 0.0) ) {
	    	builder.setTitle("Warning").setMessage("Height not set")
	    	.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
	 	       public void onClick(DialogInterface dialog, int id) {
	 	           }
	 	       });
	    	
	    	AlertDialog dialog = builder.create();
	    	dialog.show();
	    	
		} else {
			bmiValue = bmiCalculation();
			saveData();
			outputBmiIntent();
		}
	    
	}
    
    
    private void restoreSavedData() {
    	if (preferences.contains("weight") ) {
	    	bmiValue = (double) preferences.getFloat("bmi", 0);
	    	weight = (double) preferences.getFloat("weight", 0);
	    	height = (double) preferences.getFloat("height", 0);
	    	cm = (boolean) preferences.getBoolean("cm", true);
	    	kg = (boolean) preferences.getBoolean("kg", true);
    	}
    	
    }
    
    private void saveData() {
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putFloat("bmi", (float) bmiValue);
    	editor.putFloat("weight", (float) weight);
    	editor.putFloat("height", (float) height);
    	editor.putBoolean("cm", cm);
    	editor.putBoolean("kg", kg);
    	
    	editor.commit();
    }
    
    private double bmiCalculation() {
    	double weightCalc, heightCalc;
    	if (!cm) {
    		heightCalc = height * 2.54;
    	} else {
    		heightCalc = height;
    	}
    	if (!kg) {
    		weightCalc = weight / 2.2046;
    	} else {
    		weightCalc = weight;
    	}
    	return 10000 * (weightCalc/(heightCalc*heightCalc) );
    }
    
    private void outputBmiIntent() {
    	Intent displayBmi = new Intent(MainActivity.this, OutputBmi.class);
    	Bundle bundle = new Bundle();
    	bundle.putDouble("bmi", bmiValue );
    	bundle.putDouble("weight", weight);
    	bundle.putDouble("height", height);
    	bundle.putBoolean("cm", cm);    
    	bundle.putBoolean("kg", kg);    
    	
    	displayBmi.putExtras(bundle);
    	startActivity(displayBmi);
    }
    

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		bmiValue = savedInstanceState.getDouble("bmi");
		weight = savedInstanceState.getDouble("weight");
		height = savedInstanceState.getDouble("height");
		cm = savedInstanceState.getBoolean("cm");
		kg = savedInstanceState.getBoolean("kg");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putDouble("bmi", bmiValue);
		outState.putDouble("weight", weight);
		outState.putDouble("height", height);
		outState.putBoolean("cm", cm);
		outState.putBoolean("kg", kg);
	}

}
