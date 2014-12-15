package com.foxberry.videogorillas;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Menu extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button=(Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.toast_layout,
                        (ViewGroup) findViewById(R.id.relative_layout_toast));
                Toast toast = new Toast(inflater.getContext());
                toast.setView(view);
                toast.show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
