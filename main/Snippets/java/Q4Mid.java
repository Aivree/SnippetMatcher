[Code sample – Store State in State Bundle]
@Override
public void onSaveInstanceState(Bundle savedInstanceState)
{
  // Store UI state to the savedInstanceState.
  // This bundle will be passed to onCreate on next call.  EditText txtName = (EditText)findViewById(R.id.txtName);
  String strName = txtName.getText().toString();

  EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
  String strEmail = txtEmail.getText().toString();

  CheckBox chkTandC = (CheckBox)findViewById(R.id.chkTandC);
  boolean blnTandC = chkTandC.isChecked();

  savedInstanceState.putString(“Name”, strName);
  savedInstanceState.putString(“Email”, strEmail);
  savedInstanceState.putBoolean(“TandC”, blnTandC);

  super.onSaveInstanceState(savedInstanceState);
}