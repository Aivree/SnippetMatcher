package com.example.teststack.picker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.teststack.R;

public class PickerBaseDialog extends Dialog {

    private LayoutInflater mInflater;
    private TextView tvTop;
    private TextView tvBottom;

    public PickerBaseDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public PickerBaseDialog(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init() {
        mInflater = getLayoutInflater();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = mInflater.inflate(layoutResID, null);
        this.setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        ViewGroup wrapViewGroup = (ViewGroup) mInflater.inflate(R.layout.base_picker_dialog_layout, null);
        tvTop = (TextView) wrapViewGroup.findViewById(R.id.tv_top);
        tvBottom = (TextView) wrapViewGroup.findViewById(R.id.tv_bottom);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.tv_top);
        params.addRule(RelativeLayout.ABOVE, R.id.tv_bottom);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        wrapViewGroup.addView(view, params);
        super.setContentView(wrapViewGroup);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int height = getWindow().getWindowManager().getDefaultDisplay().getHeight() / 2;
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        getWindow().setGravity(Gravity.CENTER);
    }

    protected void setTitleText(String title) {
        if(tvTop != null){
            tvTop.setText(title);
        }
    }

}
