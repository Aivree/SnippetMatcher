package com.example.memory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	boolean isTemp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        isTemp=intent.getBooleanExtra("temp", false);
        if(isTemp){
        	setContentView(R.layout.activity_main_temp);
        }else{
        	setContentView(R.layout.activity_main);
        }
    }
    
    public void onBackPressed(){
    	finish();
    	if(isTemp){
    		Intent intent = new Intent(this, GameActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    		intent.putExtra("EXIT", true);
    		startActivity(intent);
    	}
    }
    
    public void onResume(){
    	super.onResume();
    	Intent intent=getIntent();
        isTemp=intent.getBooleanExtra("temp", false);
        if(isTemp){
        	setContentView(R.layout.activity_main_temp);
        }else{
        	setContentView(R.layout.activity_main);
        }
    }
    
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    
    public void resumeClick(View view){
    	Intent intent = new Intent(this, GameActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
    }
    public void newGameClick(View view){
    	Intent intent = new Intent(this, Newgame.class);
    	startActivity(intent);
    }
    public void hiscoresClick(View view){
    	Intent intent = new Intent(this, Hiscores.class);
    	startActivity(intent);
    }
    public void soundsClick(View view){
    	Intent intent = new Intent(this, Sounds.class);
    	startActivity(intent);

    }
    public void aboutClick(View view){
    	Intent intent = new Intent(this, About.class);
    	startActivity(intent);
    	
    }
    public void exitClick(View view){
    	finish();
    	if(isTemp){
    		Intent intent = new Intent(this, GameActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    		intent.putExtra("EXIT", true);
    		startActivity(intent);
    	}
    }
}
