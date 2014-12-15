package team.Spinalireland;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
    
	Button neckbutton;
    Button handbutton;
    Button feetbutton;
    boolean neckheaton=false;
    boolean feetheaton=false;
    boolean handfeaton=false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.v("Hi", "I am in on create main");
        
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	      Editor editor = sharedPreferences.edit();
				         editor.putInt("Tempfeet",0); 
				         editor.commit();
				         editor.putInt("Temphand",0); 
				         editor.commit();
				         editor.putInt("Tempneck",0); 
				         editor.commit();
        // set listeners to the buttons
        neckbutton=(Button)findViewById(R.id.neck_button);
        handbutton=(Button)findViewById(R.id.hand_button);
        feetbutton=(Button)findViewById(R.id.feet_button);
        
        neckbutton.setOnClickListener(this);
        handbutton.setOnClickListener(this);
        feetbutton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		
		case R.id.neck_button:
			
			
			
			  Intent i = new Intent(this, Tempcntrlneck.class);
			 i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			  startActivity(i);
			break;
		
			
		case R.id.feet_button:
			  Intent j = new Intent(this, Tempcntrlfeet.class);
			  startActivity(j);
			break;
			
		case R.id.hand_button:
			  Intent k = new Intent(this, Tempcntrlhand.class);
			  startActivity(k);
			break;
		}
		// TODO Auto-generated method stub
		
	}
	
	protected void onStart(){
		super.onStart();
	}
	
	protected void onResume(){
		super.onResume();
	}
	
	protected void onPause(){
		super.onPause();
	}
    
	protected void onStop(){
		super.onStop();
		Log.v("Hi", " I am in on stop in main");
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
}
