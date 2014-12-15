package com.example.spacecoatl.app;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Notification extends Fragment {
    Button sync, clearLog;
    LinearLayout notificationListLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification,
                container, false);

        notificationListLayout = (LinearLayout)view.findViewById(R.id.notificationListLayout);
        sync = (Button)view.findViewById(R.id.syncButton);
        clearLog = (Button)view.findViewById(R.id.clearLog);

        sync.setOnClickListener(new View.OnClickListener() {
            int i=0;

            @Override
            public void onClick(View view) {
                //
                LinearLayout element = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.notification_element,null);
                Resources res = getResources();
                ((TextView) element.findViewById(R.id.notificationText)).setText("i = " + i);
                ((ImageView)element.findViewById(R.id.notificationIcon)).setImageDrawable(res.getDrawable(R.drawable.medical_icon));
                notificationListLayout.addView(element, 0);
                i++;

            }
        });
        return view;

    }
}
