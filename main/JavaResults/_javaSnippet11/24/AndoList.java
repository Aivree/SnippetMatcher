package jp.critique.ando;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class AndoList extends Activity {
    
    private final static String TAG = "AndoList";
    
    private Button              addButton;
    private EditText            inputTodoTitle;
    private LinearLayout        todoListContainer;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ando);
        
        addButton = (Button) findViewById(R.id.addButton);
        inputTodoTitle = (EditText) findViewById(R.id.inputTodoTitle);
        todoListContainer = (LinearLayout) findViewById(R.id.todoListContainer);
        todoListContainer.setOrientation(LinearLayout.VERTICAL);
        
        CheckBox sampleTodo = (CheckBox) findViewById(R.id.sampleTodo);
        
        sampleTodo
                .setOnCheckedChangeListener(new OnCheckedChangeListenerImplementation(
                                                                                      sampleTodo));
        
        addButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                createNewTodoItem();
            }
        });
        
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.v(TAG, "keyCode : " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            createNewTodoItem();
        }
        return super.onKeyUp(keyCode, event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.activity_ando, menu);
        return true;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        int todoItemCount = savedInstanceState.getInt("todoItemCount");
//        Log.v(TAG,"restore " + todoItemCount + "items.");
//        if(todoItemCount > 0) {
//            todoListContainer.removeAllViews();
//            for (int i = 0; i < todoItemCount; i++) {
//                CheckBox todoItem = new CheckBox(getApplicationContext());
//                todoItem.setChecked(savedInstanceState.getBoolean("todoItemCheck" + i));
//                todoItem.setText(savedInstanceState.getString("todoItemTitle" + i));
//            }
//        }
//    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("todoItemCount", todoListContainer.getChildCount());
//        Log.v(TAG,"save " + todoListContainer.getChildCount() + "items.");
//        for (int i = 0; i < todoListContainer.getChildCount(); i++) {
//            CheckBox tmpCheckBox = (CheckBox) todoListContainer.getChildAt(i);
//            
//            outState.putBoolean("todoItemCheck" + i , tmpCheckBox.isChecked());
//            outState.putString("todoItemTitle" + i, (String) tmpCheckBox.getText());
//        }
//    }

    /**
     * 
     */
    public void createNewTodoItem() {
        String newTodoTitle = inputTodoTitle.getText().toString();
        
        if (newTodoTitle.length() > 0) {
            
            final CheckBox todoItem = new CheckBox(getApplicationContext());
            todoItem.setText(newTodoTitle);
            
            todoListContainer.addView(todoItem);
            
            inputTodoTitle.setText("");
            inputTodoTitle.clearFocus();
            
            todoItem.setOnCheckedChangeListener(new OnCheckedChangeListenerImplementation(
                                                                                          todoItem));
        }
    }
    
    private final class OnCheckedChangeListenerImplementation implements
            OnCheckedChangeListener {
        private final CheckBox todoItem;
        
        private OnCheckedChangeListenerImplementation(CheckBox todoItem) {
            this.todoItem = todoItem;
        }
        
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            if (isChecked) {
                // todoListContainer.removeView(todoItem);
            }
        }
    }
    
}
