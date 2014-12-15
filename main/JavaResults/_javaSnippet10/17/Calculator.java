package com.ruswizard.test.simplecalculator.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ruswizard.test.simplecalculator.R;
import com.ruswizard.test.simplecalculator.model.Constant;
import com.ruswizard.test.simplecalculator.model.FormatResult;
import com.ruswizard.test.simplecalculator.model.MathLogic;

/**
 * @author Doc
 */
public class Calculator extends Activity {

	/* block constant for save state activity, when rotated screen */
	private static final String rotNum1 = "num1";
	private static final String rotNum2 = "num2";
	private static final String rotOp = "operation";
	private static final String viewResult = "viewResult";
	private static final String rotFlagNumber = "flagNumber";
	private static final String rotFlagSub = "flagSub";
	private static final String rotFlagPoint = "flagPoint";
	private static final String rotFlagClear = "flagClear";
	private static final String rotFlagEquals = "flagEquals";
	private static final String rotFlagOper = "flagOper";
	private static final String rotStartSecontNum = "startSecondNum";

	private Button bnum0;
	private Button bnum1;
	private Button bnum2;
	private Button bnum3;
	private Button bnum4;
	private Button bnum5;
	private Button bnum6;
	private Button bnum7;
	private Button bnum8;
	private Button bnum9;
	private Button bPoint;
	private Button bAdd;
	private Button bSub;
	private Button bMul;
	private Button bDiv;
	private Button bEqu;
	private Button bClear;

	private EditText eResult;
	private SharedPreferences sp;

	/* boolean flag for state pressed button */
	private boolean flagNumber = false;
	private boolean flagSub = false;
	private boolean flagPoint = false;
	private boolean flagClear = false;
	private boolean flagOper = false;
	private boolean flagEquals = false;

	private MathLogic mathLogic;
	private int startSecontNum;
	private FormatResult formatResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);

		sp = getPreferences(MODE_PRIVATE);

		bnum0 = (Button) findViewById(R.id.b0);
		bnum1 = (Button) findViewById(R.id.b1);
		bnum2 = (Button) findViewById(R.id.b2);
		bnum3 = (Button) findViewById(R.id.b3);
		bnum4 = (Button) findViewById(R.id.b4);
		bnum5 = (Button) findViewById(R.id.b5);
		bnum6 = (Button) findViewById(R.id.b6);
		bnum7 = (Button) findViewById(R.id.b7);
		bnum8 = (Button) findViewById(R.id.b8);
		bnum9 = (Button) findViewById(R.id.b9);
		bPoint = (Button) findViewById(R.id.bpoint);

		bAdd = (Button) findViewById(R.id.badd);
		bSub = (Button) findViewById(R.id.bsub);
		bMul = (Button) findViewById(R.id.bmul);
		bDiv = (Button) findViewById(R.id.bdiv);
		bEqu = (Button) findViewById(R.id.bequals);
		bClear = (Button) findViewById(R.id.bclear);

		bnum0.setOnClickListener(onNum);
		bnum1.setOnClickListener(onNum);
		bnum2.setOnClickListener(onNum);
		bnum3.setOnClickListener(onNum);
		bnum4.setOnClickListener(onNum);
		bnum5.setOnClickListener(onNum);
		bnum6.setOnClickListener(onNum);
		bnum7.setOnClickListener(onNum);
		bnum8.setOnClickListener(onNum);
		bnum9.setOnClickListener(onNum);

		setonClickSub(onSub);
		setonClickPoint(onPoint);
		setonClickClear(onClear);
		setonClickEquals(onNull);

		eResult = (EditText) findViewById(R.id.result);
		eResult.setFocusable(false);
		mathLogic = new MathLogic();
		formatResult = new FormatResult();

		eResult.setText(sp.getString("result", ""));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putDouble(rotNum1, mathLogic.getNum1());
		outState.putDouble(rotNum2, mathLogic.getNum2());
		outState.putInt(rotOp, mathLogic.getOperation());
		outState.putInt(rotStartSecontNum, startSecontNum);
		outState.putBoolean(rotFlagClear, flagClear);
		outState.putBoolean(rotFlagNumber, flagNumber);
		outState.putBoolean(rotFlagPoint, flagPoint);
		outState.putBoolean(rotFlagSub, flagSub);
		outState.putBoolean(rotFlagOper, flagOper);
		outState.putString(viewResult, eResult.getText().toString());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.getBoolean(rotFlagEquals, true)) {
			setonClickEquals(onEquals);
		} else {
			setonClickEquals(onNum);
		}
		if (savedInstanceState.getBoolean(rotFlagOper, true)) {
			setOnClickOper(onOper);
		} else {
			setOnClickOper(onNull);
		}
		if (savedInstanceState.getBoolean(rotFlagPoint, true)) {
			setonClickPoint(onPoint);
		} else {
			setonClickPoint(onNull);
		}
		if (savedInstanceState.getBoolean(rotFlagSub, true)) {
			setonClickSub(onSub);
		} else {
			setonClickSub(onNull);
		}
		mathLogic.setNum1(savedInstanceState.getDouble(rotNum1));
		mathLogic.setNum2(savedInstanceState.getDouble(rotNum2));
		mathLogic.setOperation(savedInstanceState.getInt(rotOp));
		flagNumber = savedInstanceState.getBoolean(rotFlagNumber, true);
		startSecontNum = savedInstanceState.getInt(rotStartSecontNum);
		eResult.setText(savedInstanceState.getString(viewResult));

	}

	OnClickListener onOper = new OnClickListener() {
		@Override
		public void onClick(View view) {
			setonClickSub(onSub);
			setonClickPoint(onPoint);
			setonClickEquals(onEquals);
			setOnClickOper(onNum);
			flagClear = false;
			/* if not set math operation */
			if (mathLogic.getOperation() == 0) {
				switch (view.getId()) {
				case R.id.badd:
					mathLogic.setOperation(Constant.ADD);
					getNumber(view);
					eResult.append(getResources().getString(R.string.sum));
					break;

				case R.id.bmul:
					mathLogic.setOperation(Constant.MUL);
					getNumber(view);
					eResult.append(getResources().getString(R.string.mul));
					break;

				case R.id.bdiv:
					mathLogic.setOperation(Constant.DIV);
					getNumber(view);
					eResult.append(getResources().getString(R.string.div));
					break;
				default:
					break;
				}
			} else {
				/* if set operetion then need get math result */
				onEquals.onClick(view);

			}
		}
	};

	OnClickListener onNum = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (flagClear) {
				onClear.onClick(view);
				flagClear = false;
			}
			setOnClickOper(onOper);
			flagNumber = true;
			/* returm enable button sub */
			setonClickSub(onSub);

			/* append in textView text number from preesed button */
			Button btemp = (Button) view;
			eResult.append(btemp.getText().toString());

		}
	};

	OnClickListener onSub = new OnClickListener() {

		@Override
		public void onClick(View view) {
			flagClear = false;
			if (mathLogic.getOperation() == 0 && eResult.getText().length() > 0) {
				getNumber(view);
				setonClickPoint(onPoint);
				eResult.append(getResources().getString(R.string.sub));
				setonClickEquals(onEquals);
				mathLogic.setOperation(Constant.SUB);
			} else {
				if (mathLogic.getOperation() == 0 && eResult.getText().length() == 0) {
					eResult.append(getResources().getString(R.string.sub));
					setonClickSub(null);
				} else {

					mathStringLogic(view, eResult.getText().toString());
				}

			}
		}
	};

	OnClickListener onClear = new OnClickListener() {

		@Override
		public void onClick(View view) {
			setonClickPoint(onPoint);
			setonClickEquals(onNull);
			setOnClickOper(onNull);
			eResult.setText("");
			mathLogic.clear();
			startSecontNum = 0;
			flagNumber = false;
			flagClear = true;

		}
	};
	OnClickListener onEquals = new OnClickListener() {
		@Override
		public void onClick(View view) {
			getNumber(view);
			setonClickEquals(null);
		}
	};

	OnClickListener onPoint = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (flagClear) {
				onClear.onClick(view);
				flagClear = false;
			}
			if (flagNumber == false) {
				eResult.append("0.");
			} else {
				eResult.append(".");
			}
			setonClickPoint(null);

		}
	};
	OnClickListener onNull = new OnClickListener() {
		@Override
		public void onClick(View view) {
			/* null click listener. Disable events button */
		}
	};

	/*---------------------------------------------------*/
	/* Block for set click listner on button group */
	/*---------------------------------------------------*/

	private void setonClickSub(OnClickListener onListner) {
		bSub.setOnClickListener(onSub);
		if (onListner == onSub) {
			flagSub = true;
		} else {
			flagSub = false;
		}
	}

	private void setonClickPoint(OnClickListener onListner) {
		bPoint.setOnClickListener(onListner);
		if (onListner == onPoint) {
			flagPoint = true;
		} else {
			flagPoint = false;
		}
	}

	private void setonClickClear(OnClickListener onListner) {
		bClear.setOnClickListener(onListner);
		if (onListner == onClear) {
			flagClear = true;
		} else {
			flagClear = false;
		}

	}

	private void setonClickEquals(OnClickListener onListner) {
		bEqu.setOnClickListener(onListner);
		if (onListner == onEquals) {
			flagEquals = true;
		} else {
			flagEquals = false;
		}

	}

	private void setOnClickOper(OnClickListener onListner) {
		bAdd.setOnClickListener(onListner);
		bDiv.setOnClickListener(onListner);
		bMul.setOnClickListener(onListner);
		if (onListner == onOper) {
			flagOper = true;
		} else {
			flagOper = false;
		}
	}

	/*----------------------------------------------*/
	/* End Block */
	/*----------------------------------------------*/

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Editor spEdit = sp.edit();
		if (!flagEquals) {
			spEdit.putString("result", eResult.getText().toString());
			spEdit.commit();
		} else {
			spEdit.putString("result", "");
			spEdit.commit();
		}

	}

	private boolean getNumber(View view) {
		flagNumber = false;
		if (startSecontNum == 0) {
			/* get end position first number */
			startSecontNum = eResult.getText().length() + 1;
			/* set first number */
			try {
				mathLogic.setNum1(Double.parseDouble(eResult.getText().toString()));
			} catch (NumberFormatException e) {
				mathLogic.setNum1(Constant.DEFAULT);
			}
		} else {

			setonClickEquals(onEquals);
			/* parse secont number */
			String temp = eResult.getText().toString().substring(startSecontNum);
			/* set second number */
			try {
				mathLogic.setNum2(Double.parseDouble(temp));
			} catch (NumberFormatException e) {
				mathLogic.setNum1(Constant.DEFAULT);
			}
			double tempResult = mathLogic.calcLogic();
			onClear.onClick(view);
			eResult.append(formatResult.getFormatResult(tempResult));
			setOnClickOper(onOper);
		}
		return false;
	}

	/* definition of the state pressed minus */
	private void mathStringLogic(View view, String data) {

		Pattern pattern = Pattern.compile(getResources().getString(R.string.sub));
		Matcher matcher = pattern.matcher(data);
		Integer n = 0;
		while (matcher.find()) {
			n++;
		}
		if (n == 3) {
			getNumber(view);
		} else {
			if (n < 3) {
				String temp = data.substring(data.length() - 1);
				if (temp.compareTo(getResources().getString(R.string.sum)) != 0
						&& temp.compareTo(getResources().getString(R.string.sub)) != 0
						&& temp.compareTo(getResources().getString(R.string.mul)) != 0
						&& temp.compareTo(getResources().getString(R.string.div)) != 0) {
					getNumber(view);
				} else {
					eResult.append(getResources().getString(R.string.sub));
					bSub.setOnClickListener(null);
					flagClear = false;
				}
			}
		}
	}

}
