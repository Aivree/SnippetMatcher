package c301.w11.View;

import c301.w11.Model.Photo;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends Activity implements FView<Photo>
{
	
    /**
     * Keys: types in the SharedPreferences:
     * Enable: boolean if the user agree to use password 
     * Remember: boolean if the user agree to remember password 
     * Old: String Old password (last time)
     * New: Current password
     */

    public static final String PREFS_NAME = "LoginPrefs";
    private boolean pre_Enable;
    private boolean pre_Remember;
    private String pre_Old;
    private String pre_New;
    
    private CheckBox Enable;
    private CheckBox Reset;
    private CheckBox Remember;
    private EditText Old;
    private EditText New;
    private EditText Retype;
    private TextView label_Old;
    private TextView label_New;
    private TextView label_Retype;
    
    private SharedPreferences settings;

    /**
     * Called when the activity is first created.
     * SettingsActivity will get the values stored in th 
     * SharedPreferences and user can reset them
     * 
     * <p>
     * This method will initialize the content view, 
     * load values from the SharedPreferences as a backup
     * and set many Listeners
     * 
     * @param savedInstanceState
     * 			  
     *            If the activity is being re-initialized after previously being
     *            shut down then this Bundle contains the data it most recently
     *            supplied in onSaveInstanceState(Bundle). Note: Otherwise it is
     *            null.
     */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); 
        
        setContentView(R.layout.settings);
        
        /**
         * Get the SharedPreferences
         */

        settings = getSharedPreferences(PREFS_NAME, 0);
        Enable = (CheckBox) findViewById(R.id.Enable);
        Reset = (CheckBox) findViewById(R.id.Reset);
        Remember = (CheckBox) findViewById(R.id.Remember);
        Old = (EditText) findViewById(R.id.oldpass);
        New = (EditText) findViewById(R.id.newpass);
        Retype = (EditText) findViewById(R.id.retype);
        label_Old = (TextView) findViewById(R.id.label_Old);
        label_New = (TextView) findViewById(R.id.label_New);
        label_Retype = (TextView) findViewById(R.id.label_Retype);
        
        /**
         * Read keys from the SharedPreferences to set the checkboxes
         */
        
        
        if (!settings.contains("Enable")){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("Enable", false);
            editor.commit();
        }else{
            pre_Enable = settings.getBoolean("Enable", false);
        }
        
        if (!settings.contains("Remember")){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("Remember", false);
            editor.commit();
        }else{
            pre_Remember = settings.getBoolean("Remember", false);
        }
        
        if (!settings.contains("Old")){
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Old", "");
            editor.commit();
        }else{
            pre_Old = settings.getString("Old", "");
        }
        
        if (!settings.contains("New")){
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("New", "");
            editor.commit();
        }else{
            pre_New = settings.getString("New", "");
        }
        
        Enable.setChecked(pre_Enable);
        Reset.setChecked(false);
        Remember.setChecked(pre_Remember);
        
        /**
         * If user didn't use the password, hide all entries
         */
        
        if (!(Enable.isChecked())){
            Reset.setClickable(false);
            Reset.setVisibility(View.GONE);
            Remember.setClickable(false);
            Remember.setVisibility(View.GONE);
        }
                
        /**
         * Hide the EditTexts
         */
        
        Remember.setChecked(pre_Remember);
        Old.setClickable(false);
        Old.setCursorVisible(false);
        Old.setFocusable(false);
        Old.setFocusableInTouchMode(false);
        label_Old.setVisibility(View.GONE);
        Old.setVisibility(View.GONE);
        New.setClickable(false);
        New.setCursorVisible(false);
        New.setFocusable(false);
        New.setFocusableInTouchMode(false);
        label_New.setVisibility(View.GONE);
        New.setVisibility(View.GONE);
        Retype.setClickable(false);
        Retype.setCursorVisible(false);
        Retype.setFocusable(false);
        Retype.setFocusableInTouchMode(false);
        label_Retype.setVisibility(View.GONE);
        Retype.setVisibility(View.GONE);
                
        /**
         * Set the OnItemClickListener of the Enable check box
         * <p>
         * if it is checked, it will make 
         * reset check box and remember check box visible,
         * else make them invisible
         * @see OnItemClickListener
         */
        
        Enable.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                  
                  if (((CheckBox) v).isChecked()) {
                      Reset.setVisibility(View.VISIBLE);
                      Remember.setVisibility(View.VISIBLE);
                      Reset.setClickable(true);
                      Remember.setClickable(true);
                      Remember.setChecked(settings.getBoolean("Remember", false));
                      SharedPreferences.Editor editor = settings.edit();
                      editor.putBoolean("Enable", true);
                      editor.commit();
                  } else {
                      Reset.setClickable(false);
                      Reset.setVisibility(View.GONE);
                      Remember.setClickable(false);
                      Remember.setVisibility(View.GONE);
                      label_Old.setVisibility(View.GONE);
                      label_New.setVisibility(View.GONE);
                      label_Retype.setVisibility(View.GONE);
                      Old.setVisibility(View.GONE);
                      New.setVisibility(View.GONE);
                      Retype.setVisibility(View.GONE);
                      Reset.setChecked(false);
                      Remember.setChecked(false);
                      SharedPreferences.Editor editor = settings.edit();
                      editor.putBoolean("Enable", false);
                      editor.commit();
                  }
   
            }
          });
        
        /**
         * Set the OnItemClickListener of the reset check box
         * <p>
         * if it is checked, it will make 
         * the edit text visible for user to reset the password
         * else make them invisible
         * @see OnItemClickListener
         */
        
        Reset.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                  
                  if (((CheckBox) v).isChecked()) {
                      Old.setClickable(true);
                      Old.setCursorVisible(true);
                      Old.setFocusable(true);
                      Old.setFocusableInTouchMode(true);
                      label_Old.setVisibility(View.VISIBLE);
                      Old.setVisibility(View.VISIBLE);
                      New.setClickable(true);
                      New.setCursorVisible(true);
                      New.setFocusable(true);
                      New.setFocusableInTouchMode(true);
                      label_New.setVisibility(View.VISIBLE);
                      New.setVisibility(View.VISIBLE);
                      Retype.setClickable(true);
                      Retype.setCursorVisible(true);
                      Retype.setFocusable(true);
                      Retype.setFocusableInTouchMode(true);
                      label_Retype.setVisibility(View.VISIBLE);
                      Retype.setVisibility(View.VISIBLE);
                  } else {
                	  Old.setText("");
                      Old.setClickable(false);
                      Old.setCursorVisible(false);
                      Old.setFocusable(false);
                      Old.setFocusableInTouchMode(false);
                      label_Old.setVisibility(View.GONE);
                      Old.setVisibility(View.GONE);
                      New.setText("");
                      New.setClickable(false);
                      New.setCursorVisible(false);
                      New.setFocusable(false);
                      New.setFocusableInTouchMode(false);
                      label_New.setVisibility(View.GONE);
                      New.setVisibility(View.GONE);
                      Retype.setText("");
                      Retype.setClickable(false);
                      Retype.setCursorVisible(false);
                      Retype.setFocusable(false);
                      Retype.setFocusableInTouchMode(false);
                      label_Retype.setVisibility(View.GONE);
                      Retype.setVisibility(View.GONE);
                  }
   
            }
          });
        
        /**
         * Set the OnItemClickListener of the remember check box
         * <p>
         * if it is checked, it will set the value of key "Remember"
         * true
         * else set it false
         * 
         * @see OnItemClickListener
         */

        Remember.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                  
                  if (((CheckBox) v).isChecked()) {
                      SharedPreferences.Editor editor = settings.edit();
                      editor.putBoolean("Remember", true);
                      editor.commit();
                  } else {
                      SharedPreferences.Editor editor = settings.edit();
                      editor.putBoolean("Remember", false);
                      editor.commit();
                  }
   
            }
          });
                
    }
    
    /**
     * Method to create the menu
     * <p>
     * use the add method to add the Text and icon for 1 functionality 
     * Compare for save the settings.
     * @param menu @see menu
     */
    
    public boolean onCreateOptionsMenu(Menu menu){
        
        menu.add(0,Menu.FIRST,0,"Save Settings").setIcon(android.R.drawable.ic_menu_save);


        return true;
        }

    
        
        public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
        case Menu.FIRST:{
            
            /**
             * Before save, check if the password is longer than 4,
             * if the old password is correct and if the new password
             * is retyped correctly
             */
            
            if (Reset.isChecked()){
                
                if (!Old.getText().toString().equals(pre_New)){
                    Toast.makeText(SettingsActivity.this,"Please enter correct old password!",
                            Toast.LENGTH_SHORT).show();
                    break;
                }else if (!New.getText().toString().equals(Retype.getText().toString())){
                    Toast.makeText(SettingsActivity.this,"Please reenter the new password!",
                            Toast.LENGTH_SHORT).show();
                    break;
                }else if (New.getText().toString().length() < 4){
                    Toast.makeText(SettingsActivity.this,"Please make sure the length of password is greater than 4!",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                

                pre_Old = Old.getText().toString();
                pre_New = New.getText().toString();
                
                
            }
            
            pre_Enable = settings.getBoolean("Enable", false);
            pre_Remember = settings.getBoolean("Remember", false);
            finish();
        break;}

        }
        return false;
        }

        @Override
        public boolean onPrepareOptionsMenu(Menu menu){
        
        return true;

        }
      
        @Override
        public void onOptionsMenuClosed(Menu menu){

        }

        
        /**
         * Custom onStop()
         * <p>
         * if this activity is ended without using "Save Settings", 
         * the values will be returned to last time.
         */
    
    protected void onStop()
    {
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        super.onStop();
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Enable", pre_Enable);
        editor.putBoolean("Remember", pre_Remember);
        editor.putString("Old", pre_Old);
        editor.putString("New", pre_New);
        editor.commit();
    }

	public void update(Photo model) {
		// TODO Auto-generated method stub
		
	}
        
    
}
