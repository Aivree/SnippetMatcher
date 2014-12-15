package gaia.c2.context;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import gaia.c2.content.C2ContentProvider;
import gaia.c2.content.C2ContentService;
import gaia.c2.context.view.C2Message;
import gaia.c2.context.view.C2View;
import gaia.c2.receivers.ContextBroadcastReceiver;
import gaia.c2.receivers.ContextEventReceiver;

/**
 * Created by kmr on 4/7/14.
 */
public abstract class C2Context extends FragmentActivity implements ContextEventReceiver {
    private static final String SHARED_STATE = "gaia.c2.SharedState";
    private static final String CURRENT_VIEW_NAME = "gaia.c2.CurrentView";

    private Bundle sharedState;

    private Map<String, C2View> views;
    protected String currentView;

    private ContextBroadcastReceiver c2BroadcastReceiver;
    private C2ContentService c2ContentService;

    //region lifecycle
    private ServiceConnection contentServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            c2ContentService = ((C2ContentService.C2ContentServiceBinder) service).getService();
            C2Context.this.onContextReady();
            c2ContentService.onCreateAllCp();
        }

        public void onServiceDisconnected(ComponentName className) {
            c2ContentService.onDestroyAllCp();
            c2ContentService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.views = new TreeMap<String, C2View>();
        this.sharedState = new Bundle();

        if (c2ContentService == null) {
            this.onCreateContext();
        }
        bindService(new Intent(this, C2ContentService.class), contentServiceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ContextBroadcastReceiver.CONTENT_MESSAGE_OK);
        filter.addAction(ContextBroadcastReceiver.CONTENT_MESSAGE_FAILED);

        this.c2BroadcastReceiver = new ContextBroadcastReceiver(this);
        this.registerReceiver(c2BroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.onDestroyContext();

        if (c2ContentService != null) {
            unbindService(contentServiceConnection);
            c2ContentService = null;
        }

        this.unregisterReceiver(this.c2BroadcastReceiver);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            this.sharedState = savedInstanceState.getBundle(SHARED_STATE);
            this.currentView = savedInstanceState.getString(CURRENT_VIEW_NAME);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle(SHARED_STATE, this.sharedState);
        outState.putString(CURRENT_VIEW_NAME, this.currentView);
    }
    //endregion

    //region event handling
    @Override
    public final void onSuccess(Bundle message) {
        delegateHandle(message);
    }

    @Override
    public final void onFail(Bundle message, Exception ex) {
        delegateHandle(message);
    }

    private void delegateHandle(Bundle msg) {
        C2View v = views.get(currentView);
        v.handleMessage(new C2Message(msg));
    }

    //endregion

    //region api
    protected abstract void handleViewChange(C2View fromView, C2View toView);
    protected void onCreateContext() {}

    protected void onDestroyContext() {}
    protected void onContextReady() {}

    protected void addContentProvider(C2ContentProvider c2cp) {
        c2ContentService.addContentProvider(c2cp);
    }

    protected void addView(String name, C2View view) {
        if (!views.containsKey(name)) {
            views.put(name, view);
        }
    }

    public C2View getView(String name) {
        return views.get(name);
    }

    public void setView(final String name) {
        if (!views.containsKey(name)) {
            throw new IllegalArgumentException(name + " does not exist");
        }

        final C2View current = currentView == null ? null : views.get(currentView);
        final C2View newView = views.get(name);

        if (current != null) {
            current.onHide();
        }

        handleViewChange(current, newView);

        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                newView.onShow();

                C2Context.this.currentView = name;
            }
        });
    }

    public void query(String q, Bundle params) {
        Log.i("[C2Context]", "Sending query: " + q);

        Intent intent = new Intent(this, C2ContentService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setAction(C2ContentService.ACTION_QUERY);

        intent.putExtra(C2ContentService.FIELD_QUERY_PARAMETERS, params);
        intent.putExtra(C2ContentService.ACTION_QUERY, q);

        this.startService(intent);
        Log.i("[C2Context]", "Sent query: " + q);
    }

    public void query(String q) {
        query(q, new Bundle());
    }

    //endregion

    //region share
    public void shareBoolean(String key, boolean value) {
        sharedState.putBoolean(key, value);
    }

    public void shareByte(String key, byte value) {
        sharedState.putByte(key, value);
    }

    public void shareChar(String key, char value) {
        sharedState.putChar(key, value);
    }

    public void shareShort(String key, short value) {
        sharedState.putShort(key, value);
    }

    public void shareInt(String key, int value) {
        sharedState.putInt(key, value);
    }

    public void shareLong(String key, long value) {
        sharedState.putLong(key, value);
    }

    public void shareFloat(String key, float value) {
        sharedState.putFloat(key, value);
    }

    public void shareDouble(String key, double value) {
        sharedState.putDouble(key, value);
    }

    public void shareString(String key, String value) {
        sharedState.putString(key, value);
    }

    public void shareCharSequence(String key, CharSequence value) {
        sharedState.putCharSequence(key, value);
    }

    public void shareParcelable(String key, Parcelable value) {
        sharedState.putParcelable(key, value);
    }

    public void shareParcelableArray(String key, Parcelable[] value) {
        sharedState.putParcelableArray(key, value);
    }

    public void shareParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
        sharedState.putParcelableArrayList(key, value);
    }

    public void shareSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
        sharedState.putSparseParcelableArray(key, value);
    }

    public void shareIntegerArrayList(String key, ArrayList<Integer> value) {
        sharedState.putIntegerArrayList(key, value);
    }

    public void shareStringArrayList(String key, ArrayList<String> value) {
        sharedState.putStringArrayList(key, value);
    }

    public void shareCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
        sharedState.putCharSequenceArrayList(key, value);
    }

    public void shareSerializable(String key, Serializable value) {
        sharedState.putSerializable(key, value);
    }

    public void shareBooleanArray(String key, boolean[] value) {
        sharedState.putBooleanArray(key, value);
    }

    public void shareByteArray(String key, byte[] value) {
        sharedState.putByteArray(key, value);
    }

    public void shareShortArray(String key, short[] value) {
        sharedState.putShortArray(key, value);
    }

    public void shareCharArray(String key, char[] value) {
        sharedState.putCharArray(key, value);
    }

    public void shareIntArray(String key, int[] value) {
        sharedState.putIntArray(key, value);
    }

    public void shareLongArray(String key, long[] value) {
        sharedState.putLongArray(key, value);
    }

    public void shareFloatArray(String key, float[] value) {
        sharedState.putFloatArray(key, value);
    }

    public void shareDoubleArray(String key, double[] value) {
        sharedState.putDoubleArray(key, value);
    }

    public void shareStringArray(String key, String[] value) {
        sharedState.putStringArray(key, value);
    }

    public void shareCharSequenceArray(String key, CharSequence[] value) {
        sharedState.putCharSequenceArray(key, value);
    }

    public void shareBundle(String key, Bundle value) {
        sharedState.putBundle(key, value);
    }

    public void shareBinder(String key, IBinder value) {
        sharedState.putBinder(key, value);
    }

    public boolean getSharedSharedBoolean(String key) {
        return sharedState.getBoolean(key);
    }

    public boolean getSharedBoolean(String key, boolean defaultValue) {
        return sharedState.getBoolean(key, defaultValue);
    }

    public byte getSharedByte(String key) {
        return sharedState.getByte(key);
    }

    public Byte getSharedByte(String key, byte defaultValue) {
        return sharedState.getByte(key, defaultValue);
    }

    public char getSharedChar(String key) {
        return sharedState.getChar(key);
    }

    public char getSharedChar(String key, char defaultValue) {
        return sharedState.getChar(key, defaultValue);
    }

    public short getSharedShort(String key) {
        return sharedState.getShort(key);
    }

    public short getSharedShort(String key, short defaultValue) {
        return sharedState.getShort(key, defaultValue);
    }

    public int getSharedInt(String key) {
        return sharedState.getInt(key);
    }

    public int getSharedInt(String key, int defaultValue) {
        return sharedState.getInt(key, defaultValue);
    }

    public long getSharedLong(String key) {
        return sharedState.getLong(key);
    }

    public long getSharedLong(String key, long defaultValue) {
        return sharedState.getLong(key, defaultValue);
    }

    public float getSharedFloat(String key) {
        return sharedState.getFloat(key);
    }

    public float getSharedFloat(String key, float defaultValue) {
        return sharedState.getFloat(key, defaultValue);
    }

    public double getSharedDouble(String key) {
        return sharedState.getDouble(key);
    }

    public double getSharedDouble(String key, double defaultValue) {
        return sharedState.getDouble(key, defaultValue);
    }

    public String getSharedString(String key) {
        return sharedState.getString(key);
    }

    public String getSharedString(String key, String defaultValue) {
        return sharedState.getString(key, defaultValue);
    }

    public CharSequence getSharedCharSequence(String key) {
        return sharedState.getCharSequence(key);
    }

    public CharSequence getSharedCharSequence(String key, CharSequence defaultValue) {
        return sharedState.getCharSequence(key, defaultValue);
    }

    public Bundle getSharedBundle(String key) {
        return sharedState.getBundle(key);
    }

    public <T extends Parcelable> T getSharedParcelable(String key) {
        return sharedState.getParcelable(key);
    }

    public Parcelable[] getSharedParcelableArray(String key) {
        return sharedState.getParcelableArray(key);
    }

    public <T extends Parcelable> ArrayList<T> getSharedParcelableArrayList(String key) {
        return sharedState.getParcelableArrayList(key);
    }

    public <T extends Parcelable> SparseArray<T> getSharedSparseParcelableArray(String key) {
        return sharedState.getSparseParcelableArray(key);
    }

    public Serializable getSharedSerializable(String key) {
        return sharedState.getSerializable(key);
    }

    public ArrayList<Integer> getSharedIntegerArrayList(String key) {
        return sharedState.getIntegerArrayList(key);
    }

    public ArrayList<String> getSharedStringArrayList(String key) {
        return sharedState.getStringArrayList(key);
    }

    public ArrayList<CharSequence> getSharedCharSequenceArrayList(String key) {
        return sharedState.getCharSequenceArrayList(key);
    }

    public boolean[] getSharedBooleanArray(String key) {
        return sharedState.getBooleanArray(key);
    }

    public byte[] getSharedByteArray(String key) {
        return sharedState.getByteArray(key);
    }

    public short[] getSharedShortArray(String key) {
        return sharedState.getShortArray(key);
    }

    public char[] getSharedCharArray(String key) {
        return sharedState.getCharArray(key);
    }

    public int[] getSharedIntArray(String key) {
        return sharedState.getIntArray(key);
    }

    public long[] getSharedLongArray(String key) {
        return sharedState.getLongArray(key);
    }

    public float[] getSharedFloatArray(String key) {
        return sharedState.getFloatArray(key);
    }

    public double[] getSharedDoubleArray(String key) {
        return sharedState.getDoubleArray(key);
    }

    public String[] getSharedStringArray(String key) {
        return sharedState.getStringArray(key);
    }

    public CharSequence[] getSharedCharSequenceArray(String key) {
        return sharedState.getCharSequenceArray(key);
    }

    public IBinder getSharedBinder(String key) {
        return sharedState.getBinder(key);
    }
    //endregion
}
