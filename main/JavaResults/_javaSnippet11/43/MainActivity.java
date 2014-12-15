package edu.msu.comfortablynumb.project1;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    /*
     * DELETE THIS LATER!!!!!!
     */
	static final String PLAYER_NAME = "PLAYER_NAME";
	static final String SECOND_PLAYER_NAME = "SECOND_PLAYER_NAME";
    static final String ENTER_PLAYER_NAME = "Please Enter Names";
    static final String NAMES = "NAMES";
    //////////////////////////////////////

    private static final String PREFERENCES = "preferences";

	public static final String USERNAME = "USERNAME";
    public static final String USER_ID = "USER_ID";
    public static final String FIRST_PLAYER = "FIRST_PLAYER";
    public static final String SECOND_PLAYER = "SECOND_PLAYER";
	static final String PASSWORD = "PASSWORD";
    static final String REMEMBER = "REMEMBER";
    static final String FILL_INFO = "Please fill out the forms";
    static final String INVALID_USER_PASS = "Invalid Username or Password";
    public static final String USERNAME_2 = "USERNAME_2";
    public static final String USER_ID_2 = "USER_ID_2";
    EditText usernameText;
    EditText passwordText;
    CheckBox checkbox;
    boolean remember = false;
    SharedPreferences settings;
    private Handler toastHandler;
    private Runnable toastRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        setTitle("Comfortably Numb");

        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        checkbox = (CheckBox) findViewById(R.id.checkBox);

        toastHandler = new Handler();
        toastRunnable = new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, INVALID_USER_PASS, Toast.LENGTH_SHORT).show();
            }
        };

        settings = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        readPreferences();
        checkbox.setChecked(remember);

        if ( savedInstanceState != null ) {
            loadInstanceState(savedInstanceState);
        }

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Called when options button selected
	 * @param item a menu item to use
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.game_help:
                // The puzzle is done
                // Instantiate a dialog box builder
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // Parameterize the builder
                builder.setTitle(R.string.howtoplay);
                builder.setMessage(R.string.howtotext);
                builder.setPositiveButton(android.R.string.ok, null);

                // Create the dialog box and show it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

            case R.id.exit_game:
                //Exit the game
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
	}

    public void onLogin(View view) {
        Log.i("Clicked login button", "A1234");

        if ( !usernameText.getText().toString().equals("") && !passwordText.getText().toString().equals("") ) {

            if(checkbox.isChecked()) {
                writePreferences();
            } else {
                clearPreferences();
            }

            // Create a thread to create a new login
            new Thread(new Runnable() {

                /**
                 * Save the hatting in the background
                 */
                @Override
                public void run() {
                    Cloud cloud = new Cloud();
                    InputStream stream = cloud.createOnCloud(usernameText.getText().toString(), passwordText.getText().toString(), false);

                    // Test for an error
                    boolean fail = stream == null;
                    if(!fail) {
                        try {
                            XmlPullParser xml = Xml.newPullParser();
                            xml.setInput(stream, "UTF-8");


                            xml.nextTag();      // Advance to first tag
                            xml.require(XmlPullParser.START_TAG, null, "brick");
                            String status = xml.getAttributeValue(null, "status");
                            String id = xml.getAttributeValue(null, "id");
                            if(id != null)
                            	Log.i("ID", id);

                            if(status.equalsIgnoreCase("yes")){
                                //Close MainActivity and open WaitingActivity
                                Intent intent = new Intent(MainActivity.this, WaitingActivity.class);
                                intent.putExtra(USERNAME, usernameText.getText().toString());
                                intent.putExtra(USER_ID, id);
                                startActivity(intent);
                            	finish();
                            }
                            else{
                                toastHandler.post(toastRunnable);
                            }

                        } catch(IOException ex) {
                            fail = true;
                        } catch(XmlPullParserException ex) {
                        	Log.i("ex:", ex.getMessage());
                            fail = true;
                        } finally {
                            try {
                                stream.close();
                            } catch(IOException ex) {
                            	Log.i("Message",ex.getMessage());
                            }
                        }
                    }
                }
            }).start();


        } else {
            Toast.makeText(this, FILL_INFO, Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreateAccount(View view) {
        Log.i("Clicked create account button", "A1234");

        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    /**
     * Save the names to a bundle
     * @param bundle The bundle we save to
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        if (usernameText.getText() != null && passwordText.getText() != null ) {
            String username = usernameText.getText().toString();
            String password =  passwordText.getText().toString();

            bundle.putString(USERNAME, username);
            bundle.putString(PASSWORD, password);
            bundle.putBoolean(REMEMBER, remember);
        }

    }

    /**
     * Read the names from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle) {
        String username = bundle.getString(USERNAME);
        String password = bundle.getString(PASSWORD);
        remember = bundle.getBoolean(REMEMBER);
        checkbox.setChecked(remember);

        if ( username != null && password != null ) {
            usernameText.setText(username);
            passwordText.setText(password);
        }
    }

    public void onRemember(View view) {
        if (checkbox.isChecked()) {
            Log.i("Checked","checked");
            remember = true;
        } else {
            Log.i("Unchecked","unchecked");
            remember = false;
        }
    }

    private void readPreferences() {
        usernameText.setText(settings.getString(USERNAME, ""));
        passwordText.setText(settings.getString(PASSWORD, ""));
        remember = settings.getBoolean(REMEMBER, false);
    }

    private void writePreferences() {
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(USERNAME, usernameText.getText().toString());
        editor.putString(PASSWORD, passwordText.getText().toString());
        editor.putBoolean(REMEMBER, remember);

        editor.commit();
    }

    private void clearPreferences() {
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(USERNAME, "");
        editor.putString(PASSWORD, "");
        editor.putBoolean(REMEMBER, false);

        editor.commit();
    }


}
