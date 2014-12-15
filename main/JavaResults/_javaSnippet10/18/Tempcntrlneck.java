package team.Spinalireland;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class Tempcntrlneck extends Activity implements OnClickListener {
	int tempvalue=0;
	Switch neckswitch;
	Button uparrow;
	Button downarrow;
	boolean temperature=false;// signifies off
	Toast toast;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
      
		super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_cntrl_neck);
        
        // set listeners to the buttons
        neckswitch=(Switch)findViewById(R.id.neck_switch);
        downarrow=(Button)findViewById(R.id.neck_downarrow);
        uparrow=(Button)findViewById(R.id.neck_uparrow);
       
        neckswitch.setOnClickListener(this);
        uparrow.setOnClickListener(this);
        downarrow.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
switch(v.getId()){
		
		
		case R.id.neck_switch:
			ImageView img= (ImageView) findViewById(R.id.neck_imageView);
			
			if(neckswitch.isChecked())
				img.setImageResource(R.drawable.on);
			else
				img.setImageResource(R.drawable.off);
		
		
			
			//need to display the appropriate message
		case R.id.neck_uparrow:
			ImageView imgss= (ImageView) findViewById(R.id.neck_imageView);
			if(neckswitch.isChecked()){
			 if(tempvalue<5 && tempvalue>=0){
				 tempvalue++;
				 displayAprropriateImage(tempvalue,imgss);
				 
				 
			 }
			 else{
				  toast =Toast.makeText(getApplicationContext(), 
	                     "The maximum temperature you can increment is 5", Toast.LENGTH_LONG);
				 toast.setGravity(Gravity.TOP|Gravity.LEFT, 0,380);
				 toast.show();

			 }
			 
			
			}
		
				else
				 {
					  toast =Toast.makeText(getApplicationContext(), 
			                     "You can't increment when the switch button is off", Toast.LENGTH_LONG);
						 toast.setGravity(Gravity.TOP|Gravity.LEFT, 0,380);
						 toast.show();

				 }
			break;
			
		case R.id.neck_downarrow:
	
			
			ImageView imgs= (ImageView) findViewById(R.id.neck_imageView);
			if(neckswitch.isChecked()){
			 if(tempvalue>1){
				 tempvalue--;
			 displayAprropriateImage(tempvalue,imgs);
			 }
			 else{
				  toast =Toast.makeText(getApplicationContext(), 
		                     "You can't decrement any further", Toast.LENGTH_LONG);
					 toast.setGravity(Gravity.TOP|Gravity.LEFT, 0,380);
					 toast.show();
			 }
			}
			else
			 {
				  toast =Toast.makeText(getApplicationContext(), 
		                     "You can't decrement when the switch button is off", Toast.LENGTH_LONG);
					 toast.setGravity(Gravity.TOP|Gravity.LEFT, 0,380);
					 toast.show();

			 }
			break;
		}
	}
	
	@Override
	public void onBackPressed()
	{
	     // code here to show dialog
	     super.onBackPressed();  // optional depending on your needs
	     Intent intent = new Intent(this, MainActivity.class);
	     intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	     startActivity(intent);
	     
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.v("Hi", " I am on Paused to commit neckvalue " + tempvalue);
		 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	      Editor editor = sharedPreferences.edit();
				         editor.putInt("Tempneck",tempvalue);
				         editor.commit();

	}
	
	
	@Override
	public void onResume(){
		super.onResume();
		 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	     tempvalue = sharedPreferences.getInt("Tempneck", 0);
	     Log.v("Hi", "Resume in neck  is " +tempvalue );
	     
	     
	     
	     if(tempvalue==1){
		        ImageView img= (ImageView) findViewById(R.id.neck_imageView);
		    	img.setImageResource(R.drawable.on_one);
		    	neckswitch.setChecked(true);
		       }
		       if(tempvalue==2){
			        ImageView img= (ImageView) findViewById(R.id.neck_imageView);
			    	img.setImageResource(R.drawable.on_two);
			    	neckswitch.setChecked(true);
			       }
		       if(tempvalue==3){
			        ImageView img= (ImageView) findViewById(R.id.neck_imageView);
			    	img.setImageResource(R.drawable.on_three);
			    	neckswitch.setChecked(true);
			       }
		       if(tempvalue==4){
			        ImageView img= (ImageView) findViewById(R.id.neck_imageView);
			    	img.setImageResource(R.drawable.on_four);
			    	neckswitch.setChecked(true);
			       }
		       if(tempvalue==5){
			        ImageView img= (ImageView) findViewById(R.id.neck_imageView);
			    	img.setImageResource(R.drawable.on_five);
			    	neckswitch.setChecked(true);
			       }

	}
	
	@Override
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
	
	
	private void displayAprropriateImage(int value,ImageView img){
		
		switch(value){
	
		case 1:
			
			img.setImageResource(R.drawable.on_one);
			break;
		
		case 2:
			img.setImageResource(R.drawable.on_two);
			break;
		
		case 3:
			img.setImageResource(R.drawable.on_three);
			break;
		
		case 4:
			img.setImageResource(R.drawable.on_four);
			break;
		case 5:
			img.setImageResource(R.drawable.on_five);
			break;
		}
		
		
		
	}
}
