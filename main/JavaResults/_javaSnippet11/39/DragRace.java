/*
 * Copyright (C) 2012 Jamie Fenton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dragrace;

import com.example.android.dragrace.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


/**
 * A simple reaction-time trainer based on the "Christmas Tree" starting system used by drag racers.
 * as described at: www.nhra.com
 */
public class DragRace extends Activity {

    static final private String TREE_TYPE = "TreeType";

    private EditText mEditor;
    
    private XmasTree tree;
  
    String RT_FORMAT;
    String GET_READY;
   
    public DragRace() {
    }

    /** Called with the activity is first created. */
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.dragrace_activity);

        // Find the text editor view inside the layout.
        mEditor = (EditText) findViewById(R.id.editor);

        // Hook up button presses to the appropriate event handlers.
        ((Button) findViewById(R.id.stage)).setOnClickListener(mStageListener);
        ((Button) findViewById(R.id.go)).setOnTouchListener(mGoTouchListener);
        
        RT_FORMAT = getString(R.string.rt_format);
        GET_READY = getString(R.string.get_ready_msg);
        mEditor.setText(getText(R.string.main_label));
        
        tree = (XmasTree) findViewById(R.id.tree);
        
        tree.init();
        
        ((CheckBox) findViewById(R.id.full)).setOnClickListener(new OnClickListener() {	 
	      // Handle change to Pro Tree checkbox.
		  @Override
		  public void onClick(View v) {
			  tree.setProTree(!tree.getProTree());
		  }
      });
        
        if(savedInstanceState != null) {
          if( savedInstanceState.containsKey(TREE_TYPE) ) {
          	tree.setProTree(savedInstanceState.getBoolean(TREE_TYPE));
          }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
    	super.onSaveInstanceState(bundle);
    	bundle.putBoolean(TREE_TYPE, tree.getProTree());
    }
    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    
    /**
     * A call-back for when the user presses the stage button.
     */
    OnClickListener mStageListener = new OnClickListener() {
        public void onClick(View v) {
            mEditor.setText(GET_READY);
        	tree.cueStart();
        }
    };


    /**
     * A call-back for when the user presses the Go button.
     * We use a touch listener callback instead of the conventional
     * click listener since we want to measure the button activation time
     * from the leading edge.
     */
    OnTouchListener mGoTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
        	// filter out early and duplicate activations.
        	if(!tree.getTreeActive()) {
        		return true;
        	}
            tree.startRace();
            long rT = tree.getRT();
            double rTd = (double) rT / 1000.0;
            
            String rTs = String.format(RT_FORMAT, rTd);
            
            mEditor.setText(rTs);

            return true;
        }
    };
}
