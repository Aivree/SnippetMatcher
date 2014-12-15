package com.android.inflatersample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//ActivityクラスのonCreateを実行
        super.onCreate(savedInstanceState);
        
        //レイアウト設定ファイルの指定
        setContentView(R.layout.main);
        
        //main.xml の LinearLayout を取得
        final LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);

        //button1を押下した場合
        (findViewById(R.id.button1)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//LinearLayout初期化
				layout.removeAllViews();
				//getLayoutInflater().inflate(R.layout.layout1, layout);	//レイアウトの入れ替え
				
				//layout1.xml のルートビューごと取得する
				LayoutInflater mInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout mLinearLayout = (LinearLayout)mInflater.inflate(R.layout.layout1, null, false);
				TextView tv1 = (TextView)mLinearLayout.findViewById(R.id.textView1);
				Toast.makeText(MainActivity.this, tv1.getText().toString(), Toast.LENGTH_SHORT).show();
			}
        });
        
        //button2を押下した場合
        (findViewById(R.id.button2)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//LinearLayout初期化
				layout.removeAllViews();
				getLayoutInflater().inflate(R.layout.layout2, layout);
			}
        });
    }
}
