package by.colorlife.economist;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorWork extends Activity {
	double res,oper1,oper2;
	int oper;
	String sres,sfullres;
	boolean addOK,firstOper;
	TextView resOut, resFullOut;
	Button BtnDot;
	String decimalsep;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculator);
		
		resOut = (TextView) findViewById(R.id.calcResult);
		resFullOut = (TextView) findViewById(R.id.calcFullResult);
		BtnDot = (Button)  findViewById(R.id.calcDotBT);
		
		Intent intent = getIntent();
		oper1=0; oper2=0; oper=0;
		firstOper=false;
		res = intent.getDoubleExtra("calcValue", 0);
		
		sres= new DecimalFormat(AppData.calcFormat).format(res);
		sfullres="";
		resOut.setText(sres);
		resFullOut.setText(sfullres);

		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
		decimalsep = String.valueOf(symbols.getDecimalSeparator());
		BtnDot.setText(decimalsep);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		oper1= savedInstanceState.getDouble("oper1");
		oper2= savedInstanceState.getDouble("oper2");
		res= savedInstanceState.getDouble("res");
		oper= savedInstanceState.getInt("oper");
		sres= savedInstanceState.getString("sres");
		sfullres= savedInstanceState.getString("sfullres");
		addOK= savedInstanceState.getBoolean("addOK");
		firstOper= savedInstanceState.getBoolean("firstOper");

		resOut.setText(sres);
		resFullOut.setText(savedInstanceState.getString("sfullresscreen"));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putDouble("oper1", oper1);
		outState.putDouble("oper2", oper2);
		outState.putDouble("res", res);
		outState.putInt("oper", oper);
		outState.putString("sres", sres);
		outState.putString("sfullres", sfullres);
		outState.putString("sfullresscreen", resFullOut.getText().toString()); // надо сохранять отдельно
		outState.putBoolean("addOK", addOK);
		outState.putBoolean("firstOper", firstOper);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {  
		getMenuInflater().inflate(R.menu.main, menu);
        return true;
	}
	
private boolean addSRES(String sv){
	if (firstOper) {
		if (sv.equalsIgnoreCase("B")) return false;		// Обработка BackSpace
		if (sv.equalsIgnoreCase("S")) return false;		// Обработка плюс\минус		
		if (sv.equalsIgnoreCase(decimalsep)) {sres="0"+decimalsep;}		// Обработка точки
		else {sres=sv;}									// Обработка остальных цифр
		res=Double.parseDouble(sres.replaceAll(",", "."));
		firstOper=false;
		return true;}
	else {
		
	if (sv.equalsIgnoreCase("B")) {						// Обработка BackSpace
		if (sres.length()==1) {sres="0";}
		else {sres=sres.substring(0, sres.length()-1);}
		res=Double.parseDouble(sres.replaceAll(",", "."));
		return true;		
	}
	
	if (sv.equalsIgnoreCase("S")) {						// Обработка плюс\минус
		if (res==0) return false;
		if (((sres.length()==AppData.calcMaxLength) && (sres.charAt(0)=='-')) || (sres.length()<AppData.calcMaxLength)) {
		if (sres.charAt(0)=='-') { sres=sres.substring(1, sres.length()); }
		else{sres="-"+sres;}
		res=Double.parseDouble(sres.replaceAll(",", "."));
		return true;		
		}
		else return false;
	}
	
	if (sres.length()==AppData.calcMaxLength) return false;	// Обработка максимальной длины строки
	
	if (sv.equalsIgnoreCase(decimalsep)) {							// Обработка точки
		if (sres.contains(decimalsep)) {return false;}
		else {sres=sres+sv;
			  res=Double.parseDouble(sres.replaceAll(",", "."));
			  return true;}
	}

	if ((res==0) && (!sres.contains(decimalsep))) {sres=sv;}
	else {sres=sres+sv;}										// Обработка остальных цифр
	res=Double.parseDouble(sres.replaceAll(",", "."));
	return true;
}
}

private boolean setBefore(int op){
	String soper="";
	oper1=res;
	oper=op;
	firstOper=true;
	switch (op) {
	case 1: // + 
		soper="+";
		break;
	case 2: // - 
		soper="-";
		break;
	case 3: // * 
		soper="*";
		break;
	case 4: // / 
		soper="/";
		break;
	}
	//sfullres=sres+soper;
	sfullres= new DecimalFormat(AppData.calcFormat).format(res)+soper;
	return true;
}

private void calcResult(int b) { //TODO Вставить try catch
	String sfr1,soper2,sop="";
	switch (b) {
	case 1: // кнопка "равно"
		if (oper==0) return;
		oper2=res;
		switch (oper) {
		case 1:
			res=oper1+oper2;
			break;
		case 2:
			res=oper1-oper2;
			break;
		case 3:
			res=oper1*oper2;
			break;
		case 4:
			if (oper2 != 0) {res=oper1/oper2;}
			else {res=0;}
			break;
		}
		sop="=";
		break;
	case 2: // 
		break;
	}
	firstOper=true;
	sfr1=sfullres;
	soper2=new DecimalFormat(AppData.calcFormat).format(oper2);
	if (oper2<0) {sfullres=sfullres+"("+soper2+")"+sop;}
	else {sfullres=sfullres+soper2+sop;}
	sres = new DecimalFormat(AppData.calcFormat).format(res);
	resOut.setText(sres);
	resFullOut.setText(sfullres);
	sfullres=sfr1;
}

public void onClick (View v) {
	Button btn = (Button) findViewById(v.getId());
	btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	addOK=false;
	switch (v.getId()) {
	case R.id.calcACBT:
		res = 0;	
		oper1=0;
		oper2=0;
		oper=0;
		firstOper=false;
		sres="0";
		sfullres="";
		addOK=true;
		break;
	case R.id.calcBSBT:
		addOK=addSRES("B");
		break;
	case R.id.calcSignBT:
		addOK=addSRES("S");
		break;
	case R.id.calc0BT:
		if ((res==0) && !sres.contains(decimalsep)) {break;}
			addOK=addSRES("0");
		break;
	case R.id.calc1BT:
		addOK=addSRES("1");
		break;
	case R.id.calc2BT:
		addOK=addSRES("2");
	break;
	case R.id.calc3BT:
		addOK=addSRES("3");
	break;
	case R.id.calc4BT:
		addOK=addSRES("4");
	break;
	case R.id.calc5BT:
		addOK=addSRES("5");
	break;
	case R.id.calc6BT:
		addOK=addSRES("6");
	break;
	case R.id.calc7BT:
		addOK=addSRES("7");
	break;
	case R.id.calc8BT:
		addOK=addSRES("8");
	break;
	case R.id.calc9BT:
		addOK=addSRES("9");
	break;
	case R.id.calcDotBT:
		addOK=addSRES(decimalsep);
	break;
	case R.id.calcPlusBT:
		addOK=setBefore(1);
	break;
	case R.id.calcMinusBT:
		addOK=setBefore(2);
	break;
	case R.id.calcUmnBT:
		addOK=setBefore(3);
	break;
	case R.id.calcDelBT:
		addOK=setBefore(4);
	break;
	case R.id.calcItogBT:
		calcResult(1);
	break;
	
	} // switch
	
	if (addOK) {
	//Log.d(AppData.LOG_TAG, sres+ "   "+ String.valueOf(sres.length())+"  "+String.valueOf(sres.charAt(sres.length()-1))+"  "+decimalsep);
	resOut.setText(sres);
	resFullOut.setText(sfullres);
	}
	
}

public void onSetClick (View v) {
	Intent intent = new Intent();
    intent.putExtra("calcValue", res);
    setResult(RESULT_OK, intent);
	finish();
}

@Override
public void onBackPressed() {
	Intent intent = new Intent();
    setResult(RESULT_CANCELED, intent);
	finish();

}

}

