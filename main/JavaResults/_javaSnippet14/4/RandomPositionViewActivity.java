package com.example.MyTestSample;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.MyTestSample.widget.AugmentLayout;
import com.example.MyTestSample.widget.PositionedView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * User: michael
 * Date: 13-10-18
 * Time: 下午3:32
 */
public class RandomPositionViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<View> views = new ArrayList<View>();
        LayoutInflater inflateor = getLayoutInflater();
        for (int i=0;i<3;i++){
            final View view = inflateor.inflate(R.layout.simple_viewgroup_layout,null);
            final Button testBtn = (Button) view.findViewById(R.id.simple_viewgroup_btn);
            testBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.bringToFront();
                    view.getParent().requestLayout();
                    view.invalidate();
                }
            });
            views.add(view);
        }
        final PositionedView positionedView = new PositionedView(this,views);
//        setContentView(positionedView);
        AugmentLayout augmentLayout = new AugmentLayout(this);
        augmentLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView textView = new TextView(this);
        textView.setText("test11");
        textView.setTextColor(0xffffffff);
        textView.setTag(-10);

        TextView textView2 = new TextView(this);
        textView2.setText("test222");
        textView2.setTextColor(getResources().getColor(R.color.green));
        textView2.setTag(200);

        augmentLayout.addView(textView);
        augmentLayout.addView(textView2);


        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        addContentView(augmentLayout,params);
        setContentView(augmentLayout);
    }
}
