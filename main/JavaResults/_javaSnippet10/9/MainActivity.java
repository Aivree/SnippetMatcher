package com.anabellolguin.calculadoraconfragmentos;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements
		KeyboardFragment.ICalculadora {

	private Calculadora calc = null;
	private StringBuilder numberDisplayed = null;
	private Double resultNumber = 0.0;
	protected String storedNumber;

	private final String SUM = "SUM";
	private final String MUL = "MUL";
	private final String DIV = "DIV";
	private final String RES = "RES";

	private String opActual = null;
	private boolean clear = true;

	DisplayFragment display;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		display = (DisplayFragment) getFragmentManager().findFragmentById(
				R.id.fragment1);

		calc = new Calculadora();
		numberDisplayed = new StringBuilder(resultNumber.toString());
		// display = (TextView) findViewById(R.id.display);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("DISPLAY", numberDisplayed.toString());
		outState.putDouble("RESULT", resultNumber);
		outState.putString("OP", opActual);
		outState.putBoolean("CLEAR", clear);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		numberDisplayed.setLength(0);
		numberDisplayed.append(savedInstanceState.getString("DISPLAY"));
		resultNumber = savedInstanceState.getDouble("RESULT");
		opActual = savedInstanceState.getString("OP");
		clear = savedInstanceState.getBoolean("CLEAR");
	}

	@Override
	protected void onResume() {
		super.onResume();

		updateTextField();
	}

	public void setOp(View v) {
		Button button = (Button) v;

		String op = button.getText().toString();

		if (op.equals("+")) {
			opActual = SUM;
		} else if (op.equals("-")) {
			opActual = RES;
		} else if (op.equals("*")) {
			opActual = MUL;
		} else if (op.equals("/")) {
			opActual = DIV;
		}

		clear = true;

		// processNumbers();
	}

	/*
	 * private void processNumbers() { if (opActual == SUM) { resultNumber =
	 * calc.sum( Double.parseDouble(numberDisplayed.toString()), resultNumber);
	 * } else if (opActual == RES) { resultNumber =
	 * calc.difference(resultNumber,
	 * Double.valueOf(numberDisplayed.toString())); } else if (opActual == MUL)
	 * { resultNumber = calc.product(resultNumber,
	 * Double.valueOf(numberDisplayed.toString())); } else if (opActual == DIV)
	 * { String resultDev = calc.devide(resultNumber,
	 * Double.valueOf(numberDisplayed.toString())); resultNumber =
	 * Double.valueOf(resultDev); } else { resultNumber =
	 * Double.valueOf(numberDisplayed.toString()); }
	 * 
	 * Log.d("CALC", resultNumber.toString());
	 * 
	 * numberDisplayed.setLength(0); numberDisplayed.append(resultNumber);
	 * 
	 * Log.d("CALC", numberDisplayed.toString());
	 * 
	 * updateTextField(); }
	 */

	public void clear(View v) {
		numberDisplayed.setLength(0);

		numberDisplayed.append(resultNumber.toString());
		opActual = null;
		clear = true;
		updateTextField();
	}

	private void updateTextField() {
		display.muestraResultado(this.numberDisplayed.toString());
	}

	@Override
	public void pulsaDigito(int d) {
		if (clear) {
			numberDisplayed.setLength(0);
			clear = false;
		}

		numberDisplayed.append(d);

		updateTextField();
	}

	@Override
	public void pulsaOperador(String op) {
		if (opActual == SUM) {
			resultNumber = calc.sum(
					Double.parseDouble(numberDisplayed.toString()),
					resultNumber);
		} else if (opActual == RES) {
			resultNumber = calc.difference(resultNumber,
					Double.valueOf(numberDisplayed.toString()));
		} else if (opActual == MUL) {
			resultNumber = calc.product(resultNumber,
					Double.valueOf(numberDisplayed.toString()));
		} else if (opActual == DIV) {
			String resultDev = calc.devide(resultNumber,
					Double.valueOf(numberDisplayed.toString()));
			resultNumber = Double.valueOf(resultDev);
		} else {
			resultNumber = Double.valueOf(numberDisplayed.toString());
		}

		Log.d("CALC", resultNumber.toString());

		numberDisplayed.setLength(0);
		numberDisplayed.append(resultNumber);

		Log.d("CALC", numberDisplayed.toString());

		updateTextField();

	}
}
