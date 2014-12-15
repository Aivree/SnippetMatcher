package com.mjconsulting.creeper;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by MChan on 3/19/14.
 */
public class CreeperUserInfo extends ActionBarActivity
{
    private String TAG = "MerceLog";
    private EditText txtName;
    private EditText txtemail;
    private EditText txtPhone;
    private CheckBox ckAnonymous;
    private CheckBox ckCanContact;
    private Button btnUserOK;
    private Button btnUserCancel;
    private long recordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_userinfo_layout);

        Bundle b = getIntent().getExtras();
        if(b==null){
            CreeperConstants.buildToast(getString(R.string.unknowsourcetovoiceinput),this);
            finish();
            return;
        }

        recordId = b.getLong(CreeperConstants.RECORDINDEX, -1);
        if(recordId==-1)
        {
            CreeperConstants.buildToast(getString(R.string.unknowsourcetovoiceinput),this);
            finish();
            return;
        }

        addWidgets();
        addListeners();
        setupUserInfoControl();
    }

    private void addWidgets()
    {
        txtName = (EditText) findViewById(R.id.txtName);
        txtemail = (EditText) findViewById(R.id.txtemail);
        ckAnonymous = (CheckBox) findViewById(R.id.ckAnonymous);
        ckCanContact = (CheckBox) findViewById(R.id.ckCanContact);
        btnUserOK = (Button) findViewById(R.id.btnUserOK);
        btnUserCancel = (Button) findViewById(R.id.btnUserCancel);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        // ischecked should be false
        setTextViewBackgroundGrey(ckAnonymous.isChecked());
    }

    private void setupUserInfoControl()
    {
        Incident incident = IncidentsDataHelper.getInstance(this).getIncidentForId(recordId);
        if(incident==null){
            CreeperConstants.buildToast(getString(R.string.invalidincidentid),this);
            finish();
            return;
        }

        txtName.setText(incident.username);
        txtemail.setText(incident.useremail);
        txtPhone.setText(incident.userphone);
        if(incident.username!=null &&
                incident.username.equalsIgnoreCase(CreeperConstants.ANONYMOUS)){
            ckAnonymous.setChecked(true);
        }else{
            ckAnonymous.setChecked(false);
        }

        setTextViewBackgroundGrey(ckAnonymous.isChecked());

        if(incident.allowcontact==1){
            ckCanContact.setChecked(true);
        }else{
            ckCanContact.setChecked(false);
        }
    }

    private void setTextViewBackgroundGrey(boolean enable)
    {
        if(!enable)
        {
            txtName.setEnabled(true);
            txtemail.setEnabled(true);
            txtPhone.setEnabled(true);
            Log.e(TAG,"Set textviews to true");
            txtName.setHint(R.string.namehint);
            txtemail.setHint(R.string.emailhint);
            txtPhone.setHint(R.string.phonehint);
        }
        else
        {
            Log.e(TAG,"Set textviews to false");
            txtName.setEnabled(false);
            txtemail.setEnabled(false);
            txtPhone.setEnabled(false);
            txtName.setHint(R.string.fielddisabled);
            txtemail.setHint(R.string.fielddisabled);
            txtPhone.setHint(R.string.fielddisabled);
        }
    }

    private void addListeners()
    {
        ckAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextViewBackgroundGrey(ckAnonymous.isChecked());
            }
        });

        btnUserOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((new EmailValidator().validate(TextUtils.isEmpty(txtemail.getText().toString()) ? "" :
                        txtemail.getText().toString())==false) &&
                        (!ckAnonymous.isChecked()))
                {
                    CreeperConstants.buildToast(getString(R.string.invalidemail),CreeperUserInfo.this);
                    return;
                }else
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            CreeperUserInfo.this);
                    // set title
                    alertDialogBuilder.setTitle("Store Personal Info");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Confirm update!")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    ContentValues values = new ContentValues();
                                    if (ckAnonymous.isChecked()){
                                        values.put(CreeperConstants.USERNAME,CreeperConstants.ANONYMOUS);
                                        values.put(CreeperConstants.USEREMAIL,"");
                                        values.put(CreeperConstants.USERPHONE,"");
                                    }
                                    else{
                                        values.put(CreeperConstants.USERNAME,txtName.getText().toString());
                                        values.put(CreeperConstants.USEREMAIL,txtemail.getText().toString());
                                        values.put(CreeperConstants.USERPHONE,txtPhone.getText().toString());
                                    }
                                    values.put(CreeperConstants.ALLOWCONTACT,ckCanContact.isChecked()?1:0);
                                    int rc = IncidentsDataHelper.getInstance(CreeperUserInfo.this).updateIncident(values,recordId);
                                    if(rc > 0){
                                        CreeperConstants.buildToast(String.format("%s information updated !",rc),CreeperUserInfo.this);
                                    }else{
                                        CreeperConstants.buildToast(String.format("%s info not updated, something is wrong !",rc),CreeperUserInfo.this);
                                    }
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            }
        });

        btnUserCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ckAnonymous",ckAnonymous.isChecked());
        outState.putBoolean("ckCanContact",ckCanContact.isChecked());
        outState.putString("txtName",txtName.getText().toString());
        outState.putString("txtemail",txtemail.getText().toString());
        outState.putString("txtPhone",txtPhone.getText().toString());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String strName = savedInstanceState.getString("txtName");
        String strEmail = savedInstanceState.getString("txtemail");
        String strPhone = savedInstanceState.getString("txtPhone");

        boolean bAnonymous = savedInstanceState.getBoolean("ckAnonymous");
        boolean bCanContact = savedInstanceState.getBoolean("ckCanContact");

        if(bAnonymous)
        {
            txtName.setEnabled(true);
            txtemail.setEnabled(true);
            txtPhone.setEnabled(true);
        }
    }
}
