package com.paradigmcreatives.customswitcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewSwitcher.ViewFactory;

public class CustomSwitcherActivity extends Activity {
    /** Called when the activity is first created. */
	private CustomView customView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        customView = (CustomView) findViewById(R.id.custom_switcher);
        customView.setFactory(new MyCustomSwitcher());
        customView.setData("CustomSwitcher", "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcT5fVjEWEQVvirEKCs16e_761UIB9cDoH_FEi9isNy5aUWKKVyxU01lk1OKiA");
    }
    public class MyCustomSwitcher implements ViewFactory {
    	@Override
    	public View makeView() {
    		LayoutInflater inflater = getLayoutInflater();
    		View mView = inflater.inflate(R.layout.custom_switch, null);
    		return mView;
    	}
    }
}