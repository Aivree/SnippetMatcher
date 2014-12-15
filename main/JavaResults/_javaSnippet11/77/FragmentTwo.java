package com.sample.screennavigation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.sample.screennavigation.UtilRangeSeekBar.OnRangeSeekBarChangeListener;

public class FragmentTwo extends Fragment{

	String className;
	EditText editTextId2;
	CheckBox checkBoxStaticId2;
	SeekBar seekBarStaticId2;
	Spinner spinnerId2;
	Button buttonId2;
	UtilRangeSeekBar<Integer> seekBarDynamicId2;
	int minValue=0, maxValue=100;
	private CheckBox checkboxArray[] = null;
	LinearLayout checkBoxDynamicId2;

	final boolean[] dynamicCheckBoxStates = new boolean[1];

	//Declare variables that hold the data for configuration change
	boolean localCheckBoxStaticId2,localCheckBoxDynamicId2;
	String localEditTextId2;
	int localMinValue,localMaxValue,localStaticSeekbarValue,localSpinnerValue;


	public static FragmentTwo newInstance(){
		Log.d("FragmentTwo", "newInstance");
		FragmentTwo fragment = new FragmentTwo();
		return  fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("FragmentOne", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d("FragmentOne", "onAttach");
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("FragmentOne", "onCreateView");
		View view=inflater.inflate(R.layout.fragment_two, container, false);

		//Store the name of the class
		className=FragmentOne.class.getSimpleName();

		editTextId2=(EditText) view.findViewById(R.id.editTextId2);
		checkBoxStaticId2=(CheckBox) view.findViewById(R.id.checkBoxStaticId2);
		seekBarStaticId2=(SeekBar) view.findViewById(R.id.seekBarStaticId2);
		spinnerId2=(Spinner) view.findViewById(R.id.spinnerId2);
		buttonId2=(Button) view.findViewById(R.id.buttonId2);
		checkBoxDynamicId2=(LinearLayout) view.findViewById(R.id.checkBoxDynamicId2);

		seekBarDynamicId2 = new UtilRangeSeekBar<Integer>(minValue, maxValue, getActivity().getApplicationContext());

		return view;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("FragmentTwo", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		//***************************Dynamic Checkbox*************************//
		checkboxArray=new CheckBox[1];
		checkboxArray[0]= new CheckBox(getActivity());
		checkboxArray[0].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		checkboxArray[0].setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
		checkboxArray[0].setText("Dynamic Checkbox");
		checkBoxDynamicId2.addView(checkboxArray[0]);
		//***************************Dynamic Checkbox*************************//

		if (savedInstanceState != null) {

			editTextId2.setText(savedInstanceState.getString("localEditTextId2"));
			checkBoxStaticId2.setChecked(savedInstanceState.getBoolean("localCheckBoxStaticId2"));
			checkboxArray[0].setChecked(savedInstanceState.getBoolean("dynamicCheckBoxStates"));
			seekBarStaticId2.setMax(savedInstanceState.getInt("localStaticSeekbarValue"));
			seekBarDynamicId2.setSelectedMinValue(savedInstanceState.getInt("localMinValue"));
			seekBarDynamicId2.setSelectedMaxValue(savedInstanceState.getInt("localMaxValue"));
			spinnerId2.setSelection(savedInstanceState.getInt("localSpinnerValue"));

		}



	}

	@Override
	public void onStart() {
		Log.d("FragmentOne", "onStart");
		super.onStart();

		//***************************Dynamic Seek Bar*************************//
		seekBarDynamicId2.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(UtilRangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				// handle changed range values

			}
		});

		ViewGroup layoutPrice = (ViewGroup) getActivity().findViewById(R.id.seekBarDynamicId2);
		layoutPrice.addView(seekBarDynamicId2);
		//***************************Dynamic Seek Bar*************************//

		//***************************Button Onclick*************************//
		buttonId2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//Fragment fragment=FragmentOne.newInstance();
				//getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
				
				FragmentManager manager = getActivity().getSupportFragmentManager();
				  
				  Fragment frgObj=FragmentOne.newInstance();
				    FragmentTransaction ft = manager.beginTransaction();
				    ft.replace(R.id.container, frgObj,"FragmentOne");
				    ft.addToBackStack(null);
				    ft.commit();
			}
		});
		//***************************Button Onclick*************************//

	}

	@Override
	public void onResume() {
		Log.d("FragmentOne", "onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.d("FragmentOne", "onPause");
		super.onPause();

		//Showing the backstack count
		Log.d("Backstack-Count", getActivity().getSupportFragmentManager().getBackStackEntryCount()+"");

		//Store the view states locally for view restoration on orientation change
		localEditTextId2=editTextId2.getText().toString();
		localCheckBoxStaticId2=checkBoxStaticId2.isChecked();
		dynamicCheckBoxStates[0]=checkboxArray[0].isChecked();
		localStaticSeekbarValue=seekBarStaticId2.getMax();
		localMinValue=seekBarDynamicId2.getSelectedMinValue();
		localMaxValue=seekBarDynamicId2.getSelectedMaxValue();
		localSpinnerValue=spinnerId2.getSelectedItemPosition();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d("FragmentOne", "onSaveInstanceState");
		super.onSaveInstanceState(outState);


		outState.putString("localEditTextId2", localEditTextId2);
		outState.putBoolean("localCheckBoxStaticId2", localCheckBoxStaticId2);
		outState.putBoolean("dynamicCheckBoxStates", dynamicCheckBoxStates[0]);
		outState.putInt("localStaticSeekbarValue", localStaticSeekbarValue);
		outState.putInt("localMinValue", localMinValue);
		outState.putInt("localMaxValue", localMaxValue);
		outState.putInt("localSpinnerValue", localSpinnerValue);
	}

	@Override
	public void onStop() {
		Log.d("FragmentOne", "onStop");
		super.onStop();
	}

	@Override
	public void onDetach() {
		Log.d("FragmentOne", "onDetach");
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		Log.d("FragmentOne", "onDestroy");
		super.onDestroy();
	}	
}