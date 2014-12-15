package com.sjosan.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjosan.classes.TelephonyInfo;
import com.sjosan.phonedetail.R;

public class SIMInfoFragment extends Fragment {

	LinearLayout ll;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.siminfo, container, false);

		ll = (LinearLayout) rootView.findViewById(R.id.ll);

		setSIMCount(rootView, savedInstanceState);
		setIMEI(rootView, savedInstanceState);
		setSubID(rootView, savedInstanceState);
		setSIMSerial(rootView, savedInstanceState);
		setSIMCountry(rootView, savedInstanceState);
		setSIMOperator(rootView, savedInstanceState);
		setSIMOperatorCode(rootView, savedInstanceState);
		setSIMState(rootView, savedInstanceState);
		setSIMRoaming(rootView, savedInstanceState);
		setNWCountry(rootView, savedInstanceState);
		setNWOperator(rootView, savedInstanceState);
		setNWOperatorCode(rootView, savedInstanceState);
		setNWType(rootView, savedInstanceState);
		setCountryCode(rootView, savedInstanceState);
		setCallState(rootView, savedInstanceState);
		setPhoneType(rootView, savedInstanceState);
		setICC(rootView, savedInstanceState);
		setPhoneNmber(rootView, savedInstanceState);

		return rootView;
	}

	private void setSIMCount(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Number of SIM Card supported");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyInfo telephonyInfo = TelephonyInfo
					.getInstance(getActivity());
			boolean isDualSIM = telephonyInfo.isDualSIM();
			if (isDualSIM) {
				cv.setText("2");
			} else {
				cv.setText("1");
			}
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setIMEI(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("IMEI Number");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyInfo telephonyInfo = TelephonyInfo
					.getInstance(getActivity());
			cv.setText(telephonyInfo.getImeiSIM1());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setSubID(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Subscriber ID");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			cv.setText(mTelephonyMgr.getSubscriberId());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setSIMSerial(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("SIM Serial Number");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			cv.setText(mTelephonyMgr.getSimSerialNumber());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setSIMCountry(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("SIM Country ISO Code");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			cv.setText(mTelephonyMgr.getSimCountryIso());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setSIMOperator(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("SIM Operator Name");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			cv.setText(mTelephonyMgr.getSimOperatorName());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setSIMOperatorCode(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("SIM Operator Code");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			cv.setText(mTelephonyMgr.getSimOperator());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setSIMState(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("SIM State");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyInfo telephonyInfo = TelephonyInfo
					.getInstance(getActivity());
			if (telephonyInfo.isSIM1Ready()) {
				cv.setText("Ready");
			} else {
				cv.setText("Not Ready");
			}

		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setSIMRoaming(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Roaming Services");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (mTelephonyMgr.isNetworkRoaming()) {
				cv.setText("ON");
			} else {
				cv.setText("OFF");
			}
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setNWCountry(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Network Country ISO Code");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			cv.setText(mTelephonyMgr.getNetworkCountryIso());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setNWOperator(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Network Operator");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			cv.setText(mTelephonyMgr.getNetworkOperatorName());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setNWOperatorCode(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Network Operator Code");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			cv.setText(mTelephonyMgr.getNetworkOperator());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setNWType(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Network Type");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			cv.setText(networkType());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private String networkType() throws Exception {
		TelephonyManager teleMan = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = teleMan.getNetworkType();
		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return "1xRTT";
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return "CDMA";
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return "EDGE";
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return "eHRPD";
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return "EVDO rev. 0";
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return "EVDO rev. A";
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return "EVDO rev. B";
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return "GPRS";
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return "HSDPA";
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return "HSPA";
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return "HSPA+";
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return "HSUPA";
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return "iDen";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "LTE";
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return "UMTS";
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return "Unknown";
		default:
			return "Unknown";
		}
	}

	private void setCountryCode(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Mobile Country Code");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {


			cv.setText(GetCountryZipCode());
		} catch (Exception e) {
			cv.setText("Info not available.");
			e.printStackTrace();
		}
		ll.addView(view);
	}

	@SuppressLint("DefaultLocale")
	private String GetCountryZipCode() throws Exception {

		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}

	

	private void setCallState(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Call State");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager tel = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tel.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
				cv.setText("IDLE");
			} else {
				cv.setText("Call in progress.");
			}

		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setPhoneType(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Phone Type");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {

			cv.setText(getPhoneType());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private String getPhoneType() {
		TelephonyManager tel = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		int phoneType = tel.getPhoneType();
		switch (phoneType) {
		case TelephonyManager.PHONE_TYPE_NONE:
			return "NONE";

		case TelephonyManager.PHONE_TYPE_GSM:
			return "GSM";

		case TelephonyManager.PHONE_TYPE_CDMA:
			return "CDMA";

		case TelephonyManager.PHONE_TYPE_SIP:
			return "SIP";

		default:
			return "UNKNOWN";
		}

	}

	private void setICC(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("ICC Card");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager tel = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);

			if (tel.hasIccCard()) {
				cv.setText("Present");
			} else {
				cv.setText("Not Present");
			}
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setPhoneNmber(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Phone Number");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
		try {
			TelephonyManager tel = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);

			cv.setText(tel.getLine1Number());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

}
