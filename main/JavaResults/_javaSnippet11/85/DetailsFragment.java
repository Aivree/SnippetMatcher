package com.lbconsulting.passwords4;

import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lbconsulting.passwords4.Utilities.MyLog;
import com.lbconsulting.passwords4.Utilities.PasswordsUtils;
import com.lbconsulting.passwords4.database.PasswordItemsTable;

public class DetailsFragment extends Fragment {
	private final static String TAG = PasswordsUtils.TAG;
	//private static Uri mPasswordUri;
	private int mItemType = 3; // website
	private static long mCurrentPasswordId = -1;

	private EditText mtxtItemName = null;
	private Spinner mspnItemType = null;
	private CheckBox mckHideItem = null;
	private EditText mtxtAccountNumber = null;
	private Spinner mspnSubGroupSize = null;
	private EditText mtxtCreditCardSecurityCode = null;
	private TextView mtvCreditCardType = null;
	private Button mbtnCopyAccountNumber = null;
	private LinearLayout mllExpirationDate = null;
	private TextView mtvExpirationDate = null;
	private Spinner mspnExpireMonth = null;
	private Spinner mspnExpireYear = null;
	private EditText mtxtPrimaryPhone = null;
	private EditText mtxtSecondaryPhone = null;
	private EditText mtxtWebSiteURL = null;
	private EditText mtxtWebSiteUserID = null;
	private EditText mtxtWebSitePassword = null;
	private CheckBox mckShowWebsitePassword = null;
	private Button mbtnCopyPassword = null;
	private Button mbtnCopyPasswordAndGoToSite = null;
	private EditText mtxtComments = null;
	private View mSeperatorLine = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DetailsFragment() {
	}

	public static DetailsFragment newInstance(long PasswordId) {
		MyLog.v(TAG, "DetailsFragment newInstance: PasswordId=" + PasswordId);
		mCurrentPasswordId = PasswordId;

		DetailsFragment fragment = null;
		if (PasswordId > 0) {
			fragment = new DetailsFragment();
			Bundle args = new Bundle();
			args.putLong("CurrentPasswordId", PasswordId);
			fragment.setArguments(args);
			return fragment;
		}
		return fragment;
	}

	public long getShownPasswordId() {
		long passwordId = -1;
		passwordId = mCurrentPasswordId;

		/*Bundle args = getArguments();
		if (args != null) {
			passwordId = args.getLong("CurrentPasswordId", -1);
		}
		if (passwordId < 1) {
			MyLog.e(TAG, "DetailsFragment: getShownPasswordId. " + String.valueOf(passwordId) + " is not a valid Id!");
		}*/
		return passwordId;
	}

	public Uri getShownPasswordUri() {
		long passwordId = -1;
		passwordId = mCurrentPasswordId;
		Uri uri = null;
		/*Bundle args = getArguments();
		if (args != null) {
			passwordId = args.getLong("CurrentPasswordId", -1);
		}*/
		if (passwordId > 0) {
			uri = Uri.withAppendedPath(PasswordItemsTable.CONTENT_URI, String.valueOf(passwordId));
		}
		return uri;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// DetailsFragment lifecycle skeleton
	// /////////////////////////////////////////////////////////////////////////////
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		MyLog.i(TAG, "DetailsFragment onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i(TAG, "DetailsFragment onCreate");

		// Retrieve saved states
		SharedPreferences applicationStates = getActivity().getSharedPreferences("Passwords", Context.MODE_PRIVATE);
		mCurrentPasswordId = applicationStates.getLong("CurrentPasswordId", -1);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MyLog.i(TAG, "DetailsFragment onViewCreated");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MyLog.d(TAG, "DetailsFragment onActivityCreated");
		doActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		MyLog.i(TAG, "DetailsFragment onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		MyLog.i(TAG, "DetailsFragment onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		MyLog.i(TAG, "DetailsFragment onPause");
	}

	@Override
	public void onStop() {
		super.onStop();

		MyLog.i(TAG, "DetailsFragment onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MyLog.i(TAG, "DetailsFragment onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyLog.i(TAG, "DetailsFragment onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		MyLog.i(TAG, "DetailsFragment onDetach");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		MyLog.i(TAG, "DetailsFragment onSaveInstanceState");

		/*// save states
		SharedPreferences storedStates = getActivity().getSharedPreferences("Passwords", Context.MODE_PRIVATE);
		SharedPreferences.Editor applicationStates = storedStates.edit();

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			applicationStates.putBoolean("DetailsInView", true);
		} else {
			applicationStates.putBoolean("DetailsInView", false);
		}
		applicationStates.commit();*/
	}

	// /////////////////////////////////////////////////////////////////////////////
	// DetailsFragment methods
	// /////////////////////////////////////////////////////////////////////////////
	private void doActivityCreated(Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			int temp = 0;
		}
		mtxtItemName = (EditText) getActivity().findViewById((R.id.txtItemName));
		if (mtxtItemName == null) {
			MyLog.v(TAG, "DetailsFragment doActivityCreated - Details View NOT inflated.");
			return;
		}
		MyLog.v(TAG, "DetailsFragment doActivityCreated - Details View inflated.");

		mspnItemType = (Spinner) getActivity().findViewById((R.id.spnItemType));
		mckHideItem = (CheckBox) getActivity().findViewById((R.id.ckHideItem));
		mtxtAccountNumber = (EditText) getActivity().findViewById((R.id.txtAccountNumber));
		mspnSubGroupSize = (Spinner) getActivity().findViewById((R.id.spnSubGroupSize));
		mtxtCreditCardSecurityCode = (EditText) getActivity().findViewById((R.id.txtCreditCardSecurityCode));
		mtvCreditCardType = (TextView) getActivity().findViewById((R.id.tvCreditCardType));
		mbtnCopyAccountNumber = (Button) getActivity().findViewById((R.id.btnCopyAccountNumber));
		mllExpirationDate = (LinearLayout) getActivity().findViewById((R.id.llExpirationDate));
		mtvExpirationDate = (TextView) getActivity().findViewById((R.id.tvExpirationDate));
		mspnExpireMonth = (Spinner) getActivity().findViewById((R.id.spnExpireMonth));
		mspnExpireYear = (Spinner) getActivity().findViewById((R.id.spnExpireYear));
		mtxtPrimaryPhone = (EditText) getActivity().findViewById((R.id.txtPrimaryPhone));
		mtxtSecondaryPhone = (EditText) getActivity().findViewById((R.id.txtSecondaryPhone));

		mtxtWebSiteURL = (EditText) getActivity().findViewById((R.id.txtWebSiteURL));
		mtxtWebSiteUserID = (EditText) getActivity().findViewById((R.id.txtWebSiteUserID));
		mtxtWebSitePassword = (EditText) getActivity().findViewById((R.id.txtWebSitePassword));
		mckShowWebsitePassword = (CheckBox) getActivity().findViewById((R.id.ckShowWebsitePassword));
		mbtnCopyPassword = (Button) getActivity().findViewById((R.id.btnCopyPassword));
		mbtnCopyPasswordAndGoToSite = (Button) getActivity().findViewById((R.id.btnCopyPasswordAndGoToSite));
		mtxtComments = (EditText) getActivity().findViewById((R.id.txtComments));
		mSeperatorLine = getActivity().findViewById((R.id.seperatorLine));

		mckShowWebsitePassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// checkbox status is changed from uncheck to checked.
				int cursorStart = mtxtWebSitePassword.getSelectionStart();
				int cursorEnd = mtxtWebSitePassword.getSelectionEnd();

				if (!isChecked) {
					// show password
					mtxtWebSitePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
				} else {
					// hide password
					mtxtWebSitePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}
				mtxtWebSitePassword.setSelection(cursorStart, cursorEnd);
			}
		});

		mtxtPrimaryPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		mtxtSecondaryPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		setSpinners();

		Uri uri = getShownPasswordUri();
		if (uri != null) {
			String id = uri.getLastPathSegment();
			MyLog.v(TAG, "DetailsFragment doActivityCreated. Id from uri.");
			MyLog.v(TAG, "DetailsFragment doActivityCreated - id = " + id);
			String[] projection = PasswordItemsTable.PROJECTION_ALL;
			String selection = null;
			String[] selectionArgs = null;
			String sortOrder = PasswordItemsTable.SORT_ORDER_PASSWORD_ITEM_NAMES;

			ContentResolver cr = getActivity().getContentResolver();
			Cursor cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			if (cursor != null) {
				setRecordFields(cursor);
				cursor.close();
			}
		}
	}

	private void setSpinners() {
		if (mspnExpireMonth != null) {
			Integer[] months = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
			ArrayAdapter<Integer> monthsAdapter = new ArrayAdapter<Integer>(getActivity(),
					android.R.layout.simple_spinner_item, months);
			mspnExpireMonth.setAdapter(monthsAdapter);
		}

		if (mspnExpireYear != null) {
			int numberOfYears = 7;
			Integer[] years = new Integer[numberOfYears];
			Calendar cal = Calendar.getInstance();
			int currentYear = cal.get(Calendar.YEAR);
			int startYear = currentYear - 1;
			for (int i = 0; i < numberOfYears; i++) {
				years[i] = startYear;
				startYear++;
			}
			ArrayAdapter<Integer> yearsAdapter = new ArrayAdapter<Integer>(getActivity(),
					android.R.layout.simple_spinner_item, years);
			mspnExpireYear.setAdapter(yearsAdapter);
		}
		if (mspnSubGroupSize != null) {
			Integer[] subGroupSize = new Integer[] { 1, 2, 3, 4, 5, 6 };
			ArrayAdapter<Integer> monthsAdapter = new ArrayAdapter<Integer>(getActivity(),
					android.R.layout.simple_spinner_item, subGroupSize);
			mspnSubGroupSize.setAdapter(monthsAdapter);
			mspnSubGroupSize.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					// your code here
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// your code here
				}
			});
		}

		if (mspnItemType != null) {
			String[] itemTypes = getActivity().getResources().getStringArray(R.array.ItemTypes);
			ArrayAdapter<String> monthsAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_spinner_item, itemTypes);
			mspnItemType.setAdapter(monthsAdapter);
			mspnItemType.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					mItemType = position;
					ShowControls(mItemType);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					mItemType = 3;
					ShowControls(mItemType);
				}
			});

		}
	}

	protected void ShowControls(int itemType) {
		ShowAllControls();
		switch (itemType) {
		case 0: // Credit Card
			// hide mspnSubGroupSize
			HideSpnSubGroupSize();
			break;

		case 1: // General Account
			// hide: 
			// mtxtCreditCardSecurityCode, mtvCreditCardType, 
			// mtvExpirationDate, mspnExpireMonth, mspnExpireYear
			HideCreditCardInfo();
			break;

		case 2: // Software
			// hide: 
			// mtxtCreditCardSecurityCode, mtvCreditCardType, 
			// mtvExpirationDate, mspnExpireMonth, mspnExpireYear
			HideCreditCardInfo();
			break;

		case 3: // Website
			// hide:
			// hide mspnSubGroupSize

			// mtxtCreditCardSecurityCode, mtvCreditCardType, 
			// mtvExpirationDate, mspnExpireMonth, mspnExpireYear

			// mtxtAccountNumber, mbtnCopyAccountNumber
			// mtxtPrimaryPhone, mtxtSecondaryPhone	
			HideSpnSubGroupSize();
			HideCreditCardInfo();
			HideAccountInfo();
			break;

		default:
			// do nothing
			break;
		}

	}

	private void HideAccountInfo() {
		mtxtAccountNumber.setVisibility(View.GONE);
		mbtnCopyAccountNumber.setVisibility(View.GONE);
		mtxtPrimaryPhone.setVisibility(View.GONE);
		mtxtSecondaryPhone.setVisibility(View.GONE);
		mSeperatorLine.setVisibility(View.GONE);
	}

	private void HideCreditCardInfo() {
		mtxtCreditCardSecurityCode.setVisibility(View.GONE);
		mtvCreditCardType.setVisibility(View.GONE);
		mllExpirationDate.setVisibility(View.GONE);
	}

	private void HideSpnSubGroupSize() {
		// hide mspnSubGroupSize
		mspnSubGroupSize.setVisibility(View.GONE);
	}

	private void ShowAllControls() {
		mtxtAccountNumber.setVisibility(View.VISIBLE);
		mbtnCopyAccountNumber.setVisibility(View.VISIBLE);
		mtxtPrimaryPhone.setVisibility(View.VISIBLE);
		mtxtSecondaryPhone.setVisibility(View.VISIBLE);
		mSeperatorLine.setVisibility(View.VISIBLE);

		mtxtCreditCardSecurityCode.setVisibility(View.VISIBLE);
		mtvCreditCardType.setVisibility(View.VISIBLE);
		mllExpirationDate.setVisibility(View.VISIBLE);

		mspnSubGroupSize.setVisibility(View.VISIBLE);
	}

	@SuppressWarnings("null")
	private void setRecordFields(Cursor cursor) {
		if (cursor != null) {

			if (mtxtItemName != null) {
				String name = cursor.getString(cursor
						.getColumnIndexOrThrow(PasswordItemsTable.COL_PASSWORD_ITEM_NAME));
				mtxtItemName.setText(name);
			}
		}

		if (mspnItemType != null) {
			int position = cursor
					.getInt(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_PASSWORD_ITEM_TYPE_ID));
			mspnItemType.setSelection(position);
		}

		int intIsHidden = cursor
				.getInt(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_PASSWORD_ITEM_TYPE_ID));
		boolean isHidden = PasswordsUtils.intToBoolean(intIsHidden);
		mckHideItem.setChecked(isHidden);

		String accountNumber = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_ACCOUNT_NUMBER));
		if (accountNumber == null) {
			accountNumber = "";
		}
		mtxtAccountNumber.setText(accountNumber);

		int subGroupSize = cursor
				.getInt(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_SOFTWARE_KEY_CODE_SUBGROUP_LENGTH));
		int subGroupSizeIndex = PasswordsUtils.getSpinnerIndex(mspnSubGroupSize, subGroupSize);
		mspnSubGroupSize.setSelection(subGroupSizeIndex);

		String securityCode = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_ACCOUNT_NUMBER));
		if (securityCode == null) {
			securityCode = "";
		}
		mtxtCreditCardSecurityCode.setText(securityCode);

		String creditCardType = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_ACCOUNT_NUMBER));
		if (creditCardType == null) {
			creditCardType = "";
		}
		mtvCreditCardType.setText(creditCardType);

		int expireMonth = cursor
				.getInt(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_CREDIT_CARD_EXPIRATION_MONTH));
		expireMonth--;
		mspnExpireMonth.setSelection(expireMonth);

		int expireYear = cursor
				.getInt(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_CREDIT_CARD_EXPIRATION_YEAR));
		int expireYearIndex = PasswordsUtils.getSpinnerIndex(mspnExpireYear, expireYear);
		mspnExpireYear.setSelection(expireYearIndex);

		String primaryPhone = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_PRIMARY_PHONE_NUMBER));
		if (primaryPhone == null) {
			primaryPhone = "";
		}
		mtxtPrimaryPhone.setText(primaryPhone);

		String secondaryPhone = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_ALTERNATE_PHONE_NUMBER));
		if (secondaryPhone == null) {
			secondaryPhone = "";
		}
		mtxtSecondaryPhone.setText(secondaryPhone);

		String webSiteURL = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_WEBSITE_URL));
		if (webSiteURL == null) {
			webSiteURL = "";
		}
		mtxtWebSiteURL.setText(webSiteURL);

		String webSiteUserID = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_WEBSITE_USER_ID));
		if (webSiteUserID == null) {
			webSiteUserID = "";
		}
		mtxtWebSiteUserID.setText(webSiteUserID);

		String webSitePassword = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_WEBSITE_PASSWORD));
		if (webSitePassword == null) {
			webSitePassword = "";
		}
		mtxtWebSitePassword.setText(webSitePassword);

		String comments = cursor
				.getString(cursor.getColumnIndexOrThrow(PasswordItemsTable.COL_COMMENTS));
		if (comments == null) {
			comments = "";
		}
		mtxtComments.setText(comments);

	}

}
