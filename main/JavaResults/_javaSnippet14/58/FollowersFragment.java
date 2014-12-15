/****
 * This is the Followers Fragment. It manages the database for adding and deleting the followers.
 * */
package com.sigtech.panicbutton.frags;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.sigtech.panicbutton.R;
import com.sigtech.panicbutton.manager.DataManager;
import com.sigtech.panicbutton.manager.DatabaseManager;

public class FollowersFragment extends SherlockFragment implements
		OnClickListener {
	Activity act;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		act = this.getActivity();
		View v = inflater.inflate(R.layout.followers, null);

		addfollower = (Button) v.findViewById(R.id.addfollower);

		parent = (LinearLayout) v.findViewById(R.id.parent);

		addfollower.setOnClickListener(this);

		populateView();

		setupAlertDialog();

		return v;
	}

	EditText et_name, et_phonenum;
	Button addfollower, submit;
	ArrayList<NameValuePair> followers = null;
	AlertDialog ad;
	Builder builder;
	LinearLayout parentLayout, parent;

	private void setupAlertDialog() {
		builder = new Builder(act);

		View v = getLayoutInflater(null).inflate(R.layout.addviewdialog, null);

		et_name = (EditText) v.findViewById(R.id.name);
		et_phonenum = (EditText) v.findViewById(R.id.phonenum);
		submit = (Button) v.findViewById(R.id.submit);

		submit.setOnClickListener(this);

		ad = builder.setTitle("Add a follower...").setView(v).create();
	}

	private void populateView() {
		String[] panicNums = new DatabaseManager(act).getPanicNums();
		for (int i = 0; i < panicNums.length; i++) {
			parent.addView(getFollowerLayout("User" + (i + 1), (DataManager
					.check_valid_number(panicNums[i]) ? panicNums[i]
					: "Invalid number:" + panicNums[i])));
		}
	}

	private View getFollowerLayout(String name, String number) {
		LinearLayout followerLayout = (LinearLayout) getLayoutInflater(null)
				.inflate(R.layout.follower, null);
		((TextView) followerLayout.findViewById(R.id.name)).setText(name);
		((TextView) followerLayout.findViewById(R.id.number)).setText(number);
		return followerLayout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addfollower:
			ad.show();
			break;
		case R.id.submit:
			String name = et_name.getText().toString();
			et_name.setText("");
			String num = et_phonenum.getText().toString();
			et_phonenum.setText("");
			parent.addView(getFollowerLayout(name, num));
			ad.dismiss();
			new DatabaseManager(act).addPanicNum(num);
			new DatabaseManager(act).testSavedDetails();
			break;
		}
	}

}
