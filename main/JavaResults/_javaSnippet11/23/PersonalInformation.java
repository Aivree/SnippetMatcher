package local.martin.personalinformation;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class PersonalInformation extends Activity {
	/*
	 * @author Eric Martin 
	 * @version 20100206
	 * PersonalInformation
	 * CS498
	 * Called when the activity is first created.
	 */
	static final private int mode = Activity.MODE_PRIVATE;
	static final private int MENU_LOAD = Menu.FIRST;
	static final private int MENU_SAVE = Menu.FIRST + 1;
	static final private int MENU_OPTIONS = Menu.FIRST + 2;
	static final private int MENU_ABOUT = Menu.FIRST + 3;
	static private CheckBox noSpam;
	static private EditText emailAddress;
	static private EditText personName;
	static private RadioButton radioFemale;
	static private RadioButton radioMale;
	static private RadioButton radioOther;
	private TextView textSaved;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// FIXME create context menu with about menu

		final Button saveMe = (Button) findViewById(R.id.buttonSave);
		final TextView nameLabel = (TextView) findViewById(R.id.nameLabel);
		final TextView emailLabel = (TextView) findViewById(R.id.emailLabel);
		final TextView genderLabel = (TextView) findViewById(R.id.genderLabel);
		
		noSpam = (CheckBox) findViewById(R.id.checkSpam);
		emailAddress = (EditText) findViewById(R.id.textEmail);
		personName = (EditText) findViewById(R.id.textPersonName);
		radioFemale = (RadioButton) findViewById(R.id.radio_female);
		radioMale = (RadioButton) findViewById(R.id.radio_male);
		radioOther = (RadioButton) findViewById(R.id.radio_other);
		textSaved = (TextView) findViewById(R.id.textSaved);

		// Setup key/click listeners
		saveMe.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				savePreferences();
			}
		});
		noSpam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				textSaved.setText("Not Saved");
			}
		});
		emailAddress.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					switch (keyCode){
						case (KeyEvent.KEYCODE_DPAD_CENTER):
						case (KeyEvent.KEYCODE_DPAD_DOWN):
						case (KeyEvent.KEYCODE_DPAD_LEFT):
						case (KeyEvent.KEYCODE_DPAD_RIGHT):
						case (KeyEvent.KEYCODE_DPAD_UP):
						case (KeyEvent.KEYCODE_MENU):
						case (KeyEvent.KEYCODE_BACK):
						case (KeyEvent.KEYCODE_HOME):
						case (KeyEvent.KEYCODE_CALL):
						case (KeyEvent.KEYCODE_ENDCALL):
						{
							return false;
						}
						default:
						{
							textSaved.setText("Not Saved");
							return false;
						}
				}
				return false;
			}
		});
		personName.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					switch (keyCode){
					case (KeyEvent.KEYCODE_DPAD_CENTER):
					case (KeyEvent.KEYCODE_DPAD_DOWN):
					case (KeyEvent.KEYCODE_DPAD_LEFT):
					case (KeyEvent.KEYCODE_DPAD_RIGHT):
					case (KeyEvent.KEYCODE_DPAD_UP):
					case (KeyEvent.KEYCODE_MENU):
					case (KeyEvent.KEYCODE_BACK):
					case (KeyEvent.KEYCODE_HOME):
					case (KeyEvent.KEYCODE_CALL):
					case (KeyEvent.KEYCODE_ENDCALL):
					{
						// Capture keys that might be pressed normally but don't change any data.
						// return false to pass them through to the OS
						return false;
					}
					default:
					{
						textSaved.setText("Not Saved");
						return false;
					}
			}
			return false;
		}
		});
		radioFemale.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				textSaved.setText("Not Saved");
			}
		});
		radioMale.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				textSaved.setText("Not Saved");
			}
		});
		radioOther.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				textSaved.setText("Not Saved");
			}
		});
		
		registerForContextMenu(textSaved);
		registerForContextMenu(nameLabel);
		registerForContextMenu(emailLabel);
		registerForContextMenu(genderLabel);
	}

	protected void savePreferences() { //TODO make boolean and use try catch
		SharedPreferences activityPreferences = getPreferences(mode);
		SharedPreferences.Editor editor = activityPreferences.edit();
		//Grab the state of the our class variables that we need
		editor.putBoolean("noSpam", noSpam.isChecked());
		editor.putString("emailAddress", emailAddress.getText().toString());
		editor.putString("personName", personName.getText().toString());
		editor.putBoolean("radioFemale",radioFemale.isChecked());
		editor.putBoolean("radioMale",radioMale.isChecked());
		editor.putBoolean("radioOther", radioOther.isChecked());
		
		editor.commit(); //Commit the changes and save
		textSaved.setText("Saved");
	}
	protected void loadPreferences() {
		SharedPreferences mySharedPreferences = getPreferences(mode);
		//Load state back to class variables
		//Set everything to false / NULL except for radioOther if we don't know what the state was
		noSpam.setChecked(mySharedPreferences.getBoolean("noSpam", false));
		emailAddress.setText(mySharedPreferences.getString("emailAddress",""));
		personName.setText(mySharedPreferences.getString("personName",""));
		radioFemale.setChecked(mySharedPreferences.getBoolean("radioFemale", false));
		radioMale.setChecked(mySharedPreferences.getBoolean("radioMale", false));
		radioOther.setChecked(mySharedPreferences.getBoolean("radioOther", true));
		textSaved.setText("Saved");
	}
	
	@Override
	public void onStop() {
		/*
		 * Suspend remaining UI updates and threads, and anything that isn't
		 * necessary Persist all edits / state changes, process might be killed
		 */
		super.onStop();
	}
	@Override
	public void onDestroy() {
		// We're getting killed, be nice and clean up after yourself
		super.onDestroy();
	}

	@Override
	@SuppressWarnings("unused")
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem menuLoad = menu.add(0, MENU_LOAD, Menu.NONE, R.string.menu_Load);
		MenuItem menuSave = menu.add(0, MENU_SAVE, Menu.NONE, R.string.menu_Save);
		MenuItem menuOptions = menu.add(0, MENU_OPTIONS, Menu.NONE,	R.string.menu_Options);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId() ){
			case (MENU_SAVE): {
				savePreferences();
				return true;
			}
			case (MENU_OPTIONS): {
				Intent intent = new Intent(this, Options.class);
				startActivity(intent);
				return true;
			}
			case (MENU_LOAD): {
				loadPreferences();
				return true;
			}
			default:
				return false;
			}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.setHeaderTitle("Personal Information");
		menu.add(0,MENU_ABOUT,Menu.NONE,R.string.menu_About);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		
		switch(item.getItemId()){
			case (MENU_ABOUT): {
				Intent intent = new Intent(this, About.class);
				startActivity(intent);
				return true;
			}
			default:
				return false;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  // Save UI state changes to the savedInstanceState.
	  // This bundle will be passed to onCreate if the process is
	  // killed and restarted.
	  savedInstanceState.putBoolean("noSpam", noSpam.isChecked());
	  savedInstanceState.putString("emailAddress",emailAddress.getText().toString());
	  savedInstanceState.putString("personName",personName.getText().toString());
	  savedInstanceState.putBoolean("radioFemale", radioFemale.isChecked());
	  savedInstanceState.putBoolean("radioMale", radioMale.isChecked());
	  savedInstanceState.putBoolean("radioOther", radioOther.isChecked());
	  //commit changes
	  super.onSaveInstanceState(savedInstanceState);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  /* Restore UI state from the savedInstanceState.
	   * This bundle has also been passed to onCreate.
	   * All of the work was done in onSaveInstanceState, so this is it
	   */
	}
}
