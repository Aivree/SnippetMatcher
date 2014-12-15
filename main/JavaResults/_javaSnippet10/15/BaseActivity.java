package kid.a.com.twiclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by koushirou on 2014/11/30.
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doCreate(savedInstanceState);
    }

    private void doCreate(Bundle savedInstanceState) {
        setLayout();
        initializeFields();
        getViews();
        setListeners();
    }

    protected abstract void setLayout();

    protected abstract void initializeFields();

    protected abstract void getViews();

    protected abstract void setListeners();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        doRestoreInstanceState(savedInstanceState);
    }

    protected void doRestoreInstanceState(Bundle savedInstanceState) {

        try {

            for (Field field : getClass().getDeclaredFields()) {

                if (field.getAnnotation(InstanceStateField.class) == null) {
                    continue;
                }

                String key = getBundleKey(this, field);
                field.setAccessible(true);

                if (boolean.class.equals(field.getType())) {
                    field.setBoolean(this, savedInstanceState.getBoolean(key));
                } else if (byte.class.equals(field.getType())) {
                    field.setByte(this, savedInstanceState.getByte(key));
                } else if (char.class.equals(field.getType())) {
                    field.setChar(this, savedInstanceState.getChar(key));
                } else if (double.class.equals(field.getType())) {
                    field.setDouble(this, savedInstanceState.getDouble(key));
                } else if (float.class.equals(field.getType())) {
                    field.setFloat(this, savedInstanceState.getFloat(key));
                } else if (int.class.equals(field.getType())) {
                    field.setInt(this, savedInstanceState.getInt(key));
                } else if (long.class.equals(field.getType())) {
                    field.setLong(this, savedInstanceState.getLong(key));
                } else if (short.class.equals(field.getType())) {
                    field.setShort(this, savedInstanceState.getShort(key));
                } else if (String.class.equals(field.getType())) {
                    field.set(this, savedInstanceState.getString(key));
                } else if (field.get(this) instanceof CharSequence) {
                    field.set(this, savedInstanceState.getCharSequence(key));
                } else if (field.get(this) instanceof Parcelable) {
                    field.set(this, savedInstanceState.getParcelable(key));
                } else if (field.get(this) instanceof Serializable) {
                    field.set(this, savedInstanceState.getSerializable(key));
                } else {
                    throw new RuntimeException("unsupported type.");
                }
            }

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doResume();
    }

    protected void doResume() {
        display();
    }

    protected abstract void display();

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        doSaveInstanceState(outState);
    }

    protected void doSaveInstanceState(Bundle outState) {
        prepareInstanceStateFields();
        saveInstanceState(outState);
    }

    protected abstract void prepareInstanceStateFields();

    private void saveInstanceState(Bundle outState) {

        try {

            for (Field field : getClass().getDeclaredFields()) {

                if (field.getAnnotation(InstanceStateField.class) == null) {
                    continue;
                }

                String key = getBundleKey(this, field);
                field.setAccessible(true);

                if (boolean.class.equals(field.getType())) {
                    outState.putBoolean(key, field.getBoolean(this));
                } else if (byte.class.equals(field.getType())) {
                    outState.putByte(key, field.getByte(this));
                } else if (char.class.equals(field.getType())) {
                    outState.putChar(key, field.getChar(this));
                } else if (double.class.equals(field.getType())) {
                    outState.putDouble(key, field.getDouble(this));
                } else if (float.class.equals(field.getType())) {
                    outState.putFloat(key, field.getFloat(this));
                } else if (int.class.equals(field.getType())) {
                    outState.putInt(key, field.getInt(this));
                } else if (long.class.equals(field.getType())) {
                    outState.putLong(key, field.getLong(this));
                } else if (short.class.equals(field.getType())) {
                    outState.putShort(key, field.getShort(this));
                } else if (String.class.equals(field.getType())) {
                    outState.putString(key, (String) field.get(this));
                } else if (field.get(this) instanceof CharSequence) {
                    outState.putCharSequence(key, (CharSequence) field.get(this));
                } else if (field.get(this) instanceof Parcelable) {
                    outState.putParcelable(key, (Parcelable) field.get(this));
                } else if (field.get(this) instanceof Serializable) {
                    outState.putSerializable(key, (Serializable) field.get(this));
                } else {
                    throw new RuntimeException("unsupported type.");
                }
            }

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String getBundleKey(Object obj, Field field) {
        return "BundleKey_" + obj.getClass().getSimpleName() + "_" + field.getName();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        doClick(v);
    }

    public void doClick(View v) {
        click(v);
    }

    public abstract void click(View v);
}
