package com.example.myfirstapp;

import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity {

	static final String QQ = "12354";
	static final String PSW = "12354";
	private EditText et_qq = null;
	private EditText et_psw = null;
	private Button bt_login = null;
	private CheckBox cb_CheckBox = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginlayout);

		et_qq = (EditText) findViewById(R.id.et_qq);
		et_psw = (EditText) findViewById(R.id.et_psw);
		bt_login = (Button) findViewById(R.id.bt_login);
		cb_CheckBox = (CheckBox) findViewById(R.id.cb_repsw);

		if (savedInstanceState != null) {
			cb_CheckBox.setChecked(savedInstanceState.getBoolean("ifChecked"));
		}
		bt_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// ≈–∂œ’À∫≈√‹¬Î «∑Ò’˝»∑

				System.out.println("qq=" + et_qq.getText().toString());
				System.out.println("psw=" + et_psw.getText().toString());
				if (check(et_qq.getText().toString(), et_psw.getText()
						.toString())) {
					Intent intent = new Intent();
					// intent.setAction("qiu.jiang.b");
					intent.setClass(MainActivity.this, SecondActivity.class);
					startActivity(intent);

					// intent.putExtra("qqnumber", et_qq.getText().toString());
					// startActivityForResult(intent, 0);

				} else {
					showMessage("’À∫≈ªÚ√‹¬Î¥ÌŒÛ£°£°");
				}

			}
		});
	}

	private boolean check(String qq_str, String psw_str) {
		if (qq_str.equals(QQ) && psw_str.equals(PSW)) {
			return true;
		} else
			return false;

	}

	private void showMessage(String str) {

		Toast.makeText(MainActivity.this, str, 500).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0 && resultCode == 0) {
			showMessage("”√ªß" + data.getStringExtra("qqnumber") + "“—◊¢œ˙");
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putBoolean("ifChecked", cb_CheckBox.isChecked());
	}

}
