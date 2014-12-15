package com.androiddojo.secretmessage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SecretMessageReceiver extends ActionBarActivity {

    CheckBox check_box;
    Button push_me;
    EditText editText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_message_receiver);

        check_box = ((CheckBox)findViewById(R.id.checkbox));
        push_me = ((Button)findViewById(R.id.pushme));
        editText = ((EditText)findViewById(R.id.edit_text));
        textView = ((TextView)findViewById(R.id.secret_message));

        if (savedInstanceState != null) {
            check_box.setChecked(savedInstanceState.getBoolean("checkbox"));
        }
        if (getIntent() != null) {
            textView.setText(getIntent().getStringExtra("message"));
        }

        push_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SecretMessageReceiver.this, SecretMessageActivity.class);
                i.putExtra("message", editText.getText().toString());
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.secret_message_receiver, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("checkbox", check_box.isChecked());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
