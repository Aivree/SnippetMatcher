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

public class FragmentOne extends Fragment{

	String className;
	EditText editTextId1;
	CheckBox checkBoxStaticId1;
	SeekBar seekBarStaticId1;
	Spinner spinnerId1;
	Button buttonId1;
	UtilRangeSeekBar<Integer> seekBarDynamicId1;
	int minValue=0, maxValue=100;
	private CheckBox checkboxArray[] = null;
	LinearLayout checkBoxDynamicId1;
	
	final boolean[] dynamicCheckBoxStates = new boolean[1];



	//Declare variables that hold the data for configuration change
	boolean localCheckBoxStaticId1,localCheckBoxDynamicId1;
	String localEditTextId1;
	int localMinValue,localMaxValue,localStaticSeekbarValue,localSpinnerValue;

	public static FragmentOne newInstance(){
		Log.d("FragmentOne", "newInstance");
		FragmentOne fragment = new FragmentOne();
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
		View view=inflater.inflate(R.layout.fragment_one, container, false);

		//Store the name of the class
		className=FragmentOne.class.getSimpleName();

		editTextId1=(EditText) view.findViewById(R.id.editTextId1);
		checkBoxStaticId1=(CheckBox) view.findViewById(R.id.checkBoxStaticId1);
		seekBarStaticId1=(SeekBar) view.findViewById(R.id.seekBarStaticId1);
		spinnerId1=(Spinner) view.findViewById(R.id.spinnerId1);
		buttonId1=(Button) view.findViewById(R.id.buttonId1);
		checkBoxDynamicId1=(LinearLayout) view.findViewById(R.id.checkBoxDynamicId1);

		seekBarDynamicId1 = new UtilRangeSeekBar<Integer>(minValue, maxValue, getActivity().getApplicationContext());

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("FragmentOne", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		
		//***************************Dynamic Checkbox*************************//
		checkboxArray=new CheckBox[1];
		checkboxArray[0]= new CheckBox(getActivity());
		checkboxArray[0].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		checkboxArray[0].setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
		checkboxArray[0].setText("Dynamic Checkbox");
		checkBoxDynamicId1.addView(checkboxArray[0]);
		//***************************Dynamic Checkbox*************************//
		
		if (savedInstanceState != null) {
			
			editTextId1.setText(savedInstanceState.getString("localEditTextId1"));
			checkBoxStaticId1.setChecked(savedInstanceState.getBoolean("localCheckBoxStaticId1"));
			checkboxArray[0].setChecked(savedInstanceState.getBoolean("dynamicCheckBoxStates"));
			seekBarStaticId1.setMax(savedInstanceState.getInt("localStaticSeekbarValue"));
			seekBarDynamicId1.setSelectedMinValue(savedInstanceState.getInt("localMinValue"));
			seekBarDynamicId1.setSelectedMaxValue(savedInstanceState.getInt("localMaxValue"));
			spinnerId1.setSelection(savedInstanceState.getInt("localSpinnerValue"));

		}
	}

	@Override
	public void onStart() {
		Log.d("FragmentOne", "onStart");
		super.onStart();


		//***************************Dynamic Seek Bar*************************//
		seekBarDynamicId1.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(UtilRangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				// handle changed range values

			}
		});

		ViewGroup layoutPrice = (ViewGroup) getActivity().findViewById(R.id.seekBarDynamicId1);
		layoutPrice.addView(seekBarDynamicId1);
		//***************************Dynamic Seek Bar*************************//

		//***************************Button Onclick*************************//
		buttonId1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//Fragment fragment=FragmentTwo.newInstance();
				//getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
				
				FragmentManager manager = getActivity().getSupportFragmentManager();
				  
				  Fragment frgObj=FragmentTwo.newInstance();
				    FragmentTransaction ft = manager.beginTransaction();
				    ft.replace(R.id.container, frgObj,"FragmentTwo");
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
		localEditTextId1=editTextId1.getText().toString();
		localCheckBoxStaticId1=checkBoxStaticId1.isChecked();
		dynamicCheckBoxStates[0]=checkboxArray[0].isChecked();
		localStaticSeekbarValue=seekBarStaticId1.getMax();
		localMinValue=seekBarDynamicId1.getSelectedMinValue();
		localMaxValue=seekBarDynamicId1.getSelectedMaxValue();
		localSpinnerValue=spinnerId1.getSelectedItemPosition();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d("FragmentOne", "onSaveInstanceState");
		super.onSaveInstanceState(outState);

		outState.putString("localEditTextId1", localEditTextId1);
		outState.putBoolean("localCheckBoxStaticId1", localCheckBoxStaticId1);
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