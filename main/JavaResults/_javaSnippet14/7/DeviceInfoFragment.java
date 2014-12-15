package com.sjosan.fragment;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjosan.phonedetail.R;

@SuppressLint("NewApi")
public class DeviceInfoFragment extends Fragment {


	LinearLayout ll;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.deviceinfo, container, false);

		ll = (LinearLayout) rootView.findViewById(R.id.ll);
		
		setDeviceBrand(rootView, savedInstanceState);
		setDeviceName(rootView, savedInstanceState);
		setCores(rootView, savedInstanceState);
		setDeviceHardware(rootView, savedInstanceState);
		setCPUABI(rootView, savedInstanceState);
		setCPUABI2(rootView, savedInstanceState);
		setDisplay(rootView, savedInstanceState);
		setFingerPrint(rootView, savedInstanceState);
		setManfactrer(rootView, savedInstanceState);
		setSDKVersion(rootView, savedInstanceState);
		setProduct(rootView, savedInstanceState);
		setBuildID(rootView, savedInstanceState);
		setAndroidID(rootView, savedInstanceState);
		setBuildType(rootView, savedInstanceState);
		setVersion(rootView, savedInstanceState);
		setHost(rootView, savedInstanceState);
		setUser(rootView, savedInstanceState);
		setRadio(rootView, savedInstanceState);
		setTime(rootView, savedInstanceState);
		setBootloader(rootView, savedInstanceState);
		setSerial(rootView, savedInstanceState);
		setVersionInc(rootView, savedInstanceState);
		
		return rootView;
	}

	private void setDeviceBrand(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Device Brand");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);
	try {
			cv.setText(Build.BRAND);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setDeviceName(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Device Name");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(getDeviceName());
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	
	private void setDeviceHardware(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Device Hardware");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.HARDWARE);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}

	private void setCPUABI(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("CPU ABI");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.CPU_ABI);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setCPUABI2(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("CPU ABI2");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.CPU_ABI2);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setDisplay(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Display");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.DISPLAY);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setFingerPrint(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Fingerprint");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.FINGERPRINT);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setManfactrer(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Manufacturer");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.MANUFACTURER);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setSDKVersion(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("SDK Version");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(String.valueOf(Build.VERSION.SDK_INT));
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setProduct(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Product");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.PRODUCT);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setBuildID(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Build ID");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.ID);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setAndroidID(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Android ID");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			String deviceId = Secure.getString(getActivity().getContentResolver(),
	                Secure.ANDROID_ID);
			cv.setText(deviceId);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setBuildType(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Build Type");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.TYPE);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setVersion(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Version Code Name");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.VERSION.CODENAME);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setHost(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Build Host");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.HOST);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setUser(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Build User");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.USER);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	@SuppressWarnings("deprecation")
	private void setRadio(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Build Radio");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.RADIO);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setTime(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Build Time");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(String.valueOf(Build.TIME));
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setBootloader(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Bootloader");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.BOOTLOADER);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setSerial(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Build Serial");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.SERIAL);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private void setVersionInc(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Version Increment");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(Build.VERSION.INCREMENTAL);
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	
	public String getDeviceName() throws Exception {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) throws Exception {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}
	private void setCores(View rootView, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(
				R.layout.list, ll, false);
		TextView hv = (TextView) view.findViewById(R.id.DHeadView1);
		hv.setText("Number of Cores");
		TextView cv = (TextView) view.findViewById(R.id.DContentView1);

		try {
			cv.setText(String.valueOf(getNumCores()));
		} catch (Exception e) {
			cv.setText("Info not available.");
		}
		ll.addView(view);
	}
	private int getNumCores() throws Exception {

	    //Private Class to display only CPU devices in the directory listing

	    class CpuFilter implements FileFilter {

	        @Override

	        public boolean accept(File pathname) {

	            //Check if filename is "cpu", followed by a single digit number

	            if(Pattern.matches("cpu[0-9]+", pathname.getName())) {

	                return true;

	            }

	            return false;

	        }      

	    }



	    try {

	        //Get directory containing CPU info

	        File dir = new File("/sys/devices/system/cpu/");

	        //Filter to only list the devices we care about

	        File[] files = dir.listFiles(new CpuFilter());


	        //Return the number of cores (virtual CPU devices)

	        return files.length;

	    } catch(Exception e) {

	        //Print exception


	        e.printStackTrace();

	        //Default to return 1 core

	        return 1;

	    }

	}
	

}
