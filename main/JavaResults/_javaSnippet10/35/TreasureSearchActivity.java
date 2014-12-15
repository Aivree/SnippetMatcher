package jp.co.aclox.android.treasuresearch;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.graphics.Color;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.GeoPoint;

public class TreasureSearchActivity extends MapActivity implements TreasureMapView.TreasureMapViewListener {
	private static final String TAG = "Treasure";
    private static final GeoPoint CENTER_GEOPOINT =
    	new GeoPoint((int)(38.0 * 1E6), (int)(138.0 * 1E6));
    private static final int MENU_START_ID = 1;
    private static final int MENU_END_ID = 2;
    private static final int MENU_HELP_ID = 9;

	private TreasureMapView mMapView = null;
	private TextView mTextMessage = null;
	private TextView mTextCheckResult = null;
	private AreaEngine mAreaEngine = null;
	private Area mTreasureArea = null;
	private LatLng2DistanceEngine mDistanceEngine = new LatLng2DistanceEngine();
	private boolean mStartFlag = false;
	private CheckPointList mCheckPoints = new CheckPointList();
	private ProgressDialog mStartProgressDialog = null;
	private ProgressDialog mCalcDistanceProgressDialog = null;
	private int mCheckCount = 0;
	private int mGameCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treasuresearch);

    	mAreaEngine = new AreaEngine(getApplicationContext());
        mTextMessage = (TextView)findViewById(R.id.textMessage);
        mTextCheckResult = (TextView)findViewById(R.id.textCheckResult);
        mTextCheckResult.setBackgroundColor(Color.DKGRAY);
        mMapView = (TreasureMapView)findViewById(R.id.mapView);
        mMapView.setListener(this);

        if (savedInstanceState != null) {
        	try{
        		mGameCount = savedInstanceState.getInt("gameCount");
        	} catch (ClassCastException e) {
        		e.printStackTrace();
            	Log.e(TAG, "savedInstanceState[gameCount]:" + e.toString());
        	}
        	try{
        		mStartFlag = savedInstanceState.getBoolean("startFlag");
        	} catch (ClassCastException e) {
        		e.printStackTrace();
            	Log.e(TAG, "savedInstanceState[startFlag]:" + e.toString());
        	}
            mTreasureArea = (Area)savedInstanceState.getSerializable("treasureArea");
        	try{
                mCheckCount =  savedInstanceState.getInt("checkCount");
        	} catch (ClassCastException e) {
        		e.printStackTrace();
            	Log.e(TAG, "savedInstanceState[checkCount]:" + e.toString());
        	}
            mCheckPoints = (CheckPointList)savedInstanceState.getSerializable("checkPoints");
        	Log.i(TAG, "ゲーム再開");
            resumeGame();
        } else {
        	Log.i(TAG, "アプリケーション開始");
        	startGame();
        }

    }

    @Override
    public void onStart() {
    	super.onStart();
    }

    @Override
    public void onResume() {
    	super.onResume();
    }

    @Override
    public void onPause() {
    	super.onPause();
    }

    @Override
    public void onStop() {
    	super.onStop();
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("gameCount", mGameCount);
        outState.putBoolean("startFlag", mStartFlag);
        outState.putSerializable("treasureArea", mTreasureArea);
        outState.putInt("checkCount", mCheckCount);
        outState.putSerializable("checkPoints", mCheckPoints);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_START_ID, Menu.NONE, "新しいゲーム");
		menu.add(0, MENU_END_ID, Menu.NONE, "ゲームの終了");
		menu.add(1, MENU_HELP_ID, Menu.NONE, "説明");

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case MENU_START_ID:
			if (mStartFlag) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle("新しいゲーム");
				builder.setMessage("現在のゲームを終了しますか？");
				builder.setPositiveButton(
						"OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								endGame();
								startGame();
							}
						}
				);

				builder.setNegativeButton(
						"キャンセル",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});
				builder.setCancelable(true);
				builder.show();

			} else {
				startGame();
			}
			break;

		case MENU_END_ID:
			if ( mStartFlag ) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle("ゲームの終了");
				builder.setMessage("現在のゲームを終了して解答を表示しますか？");
				builder.setPositiveButton(
						"OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								endGame();
					    		mMapView.setTreasureDiscovered(true);
					        	MapController mController = mMapView.getController();
					        	mController.animateTo(mTreasureArea.getGeoPoint());
							}
						}
				);

				builder.setNegativeButton(
						"キャンセル",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});
				builder.setCancelable(true);
				builder.show();

				break;
			}

		case MENU_HELP_ID:
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    /*
     * (非 Javadoc)
     * @see jp.co.aclox.android.treasuresearch.TreasureMapView.TreasureMapViewListener#onMapViewLongPress(com.google.android.maps.GeoPoint)
     */
    public void onMapViewLongPress(GeoPoint point) {
        if (!mStartFlag) return;

        final GeoPoint currentPoint = point;

        final Handler mHandler = new Handler() {
        	public void handleMessage(Message msg) {
        		Bundle data = msg.getData();
        		double distance = data.getDouble("distance");
        		if (mCalcDistanceProgressDialog != null && mCalcDistanceProgressDialog.isShowing()) {
        			mCalcDistanceProgressDialog.dismiss();
        			mCalcDistanceProgressDialog = null;
        		}
	    		if ( distance < 0) {
		    		Log.w(TAG, "Cannot get distance.");
					errorDialog("宝箱までの距離計算ができません。\n通信状態を確認してください。");
	    			return;
	    		}

	            CheckPoint checkPoint = new CheckPoint(currentPoint.getLatitudeE6() / 1E6, currentPoint.getLongitudeE6() / 1E6);
	    		checkPoint.setDistance(distance);
	    		mCheckPoints.add(checkPoint);
	        	mCheckCount++;
	    		Log.i(TAG, String.format("捜索(%d):位置=%.2f,%.2f 距離=%.3f", mCheckCount, checkPoint.getLatitude(), checkPoint.getLongitude(), checkPoint.getDistance()));
	        	int number = mCheckPoints.size();
	            mMapView.addCheckPoint(number, checkPoint);
				updateScreen();

				String desc = (distance >= 1000 ?
						String.format("[%d] 宝箱まで%.1fkm", mCheckCount, distance / 1000) :
						String.format("[%d] 宝箱まで%dm", mCheckCount, (int)distance));
				Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT).show();
				mTextCheckResult.setText(desc);
        	}
        };

    	Thread thread = new Thread(new Runnable() {
			public void run() {
	        	GeoPoint destPoint = mTreasureArea.getGeoPoint();
	    		double distance = mDistanceEngine.getDistance(currentPoint, destPoint);
	    		Bundle data = new Bundle();
	    		data.putDouble("distance", distance);
	    		Message msg = new Message();
	    		msg.what = 1;
	    		msg.setData(data);
	    		mHandler.sendMessage(msg);
			}
		});

    	mCalcDistanceProgressDialog = new ProgressDialog(this);
        mCalcDistanceProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mCalcDistanceProgressDialog.setMessage("距離計算中...");
        mCalcDistanceProgressDialog.setCancelable(true);
	    mCalcDistanceProgressDialog.show();

	    thread.start();
	}

    /*
     * (非 Javadoc)
     * @see jp.co.aclox.android.treasuresearch.TreasureMapView.TreasureMapViewListener#onCheckPointTap(int)
     */
	public void onCheckPointTap(int number) {
		if(number <= 0 || number > mCheckPoints.size()) {
			Log.w(TAG, "Why? Tap number(" + number + ") is invalid. Total checkpoints=" + mCheckPoints.size());
			return;
		}
		CheckPoint checkPoint = mCheckPoints.get(number - 1);
		double distance = checkPoint.getDistance();
		String desc = (distance >= 1000 ?
				String.format("[%d] 宝箱まで%.1fkm", number, distance / 1000) :
				String.format("[%d] 宝箱まで%dm", number, (int)distance));
		mTextCheckResult.setText(desc);
	}

    /*
     * (非 Javadoc)
     * @see jp.co.aclox.android.treasuresearch.TreasureMapView.TreasureMapViewListener#onTreasureItemTap()
     */
    public void onTreasureItemTap() {
    	if ( mStartFlag ) {
    		mCheckCount++;

    		mMapView.moveTo(mTreasureArea.getGeoPoint());
    		mMapView.setTreasureDiscovered(true);
			endGame();
			updateScreen();

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(android.R.drawable.ic_dialog_info);
			builder.setTitle(R.string.app_name);
			builder.setMessage("宝箱発見！\n捜索回数は" + mCheckCount + "回でした");
			builder.setPositiveButton(
					"新しいゲーム",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							startGame();
						}
					}
			);

			builder.setNegativeButton(
					"終了",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			builder.setCancelable(true);
			builder.show();

    	} else {
//        	Toast.makeText(getApplicationContext(), mTreasureArea.getAddress(), Toast.LENGTH_LONG).show();
    	}
    	mTextCheckResult.setText("[宝箱] " + mTreasureArea.getAddress());
    	return;
    }

    /*
     *
     */
	private void startGame() {
		mStartFlag = false;
		mTreasureArea = null;
		mCheckPoints.clear();
		mCheckCount = 0;
		mMapView.clear();

    	MapController mController = mMapView.getController();
    	mController.setZoom(6);
    	mController.setCenter(CENTER_GEOPOINT);

    	mStartProgressDialog = new ProgressDialog(this);
	    mStartProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    mStartProgressDialog.setMessage("準備中...");
	    mStartProgressDialog.setCancelable(true);
	    mStartProgressDialog.show();

		final Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {

				if ( mStartProgressDialog != null && mStartProgressDialog.isShowing()) {
					mStartProgressDialog.dismiss();
					mStartProgressDialog = null;
				}
	    		Area area = (Area)msg.obj;
		    	if (area == null) {
		    		Log.w(TAG, "Destination:No match.");
					errorDialog("ゲームを開始できません。\n通信状態を確認してください。");
					endGame();
		    		return;
		    	}
				mTreasureArea = area;

		    	Log.i(TAG, "宝箱:" + area.getAddress() + " (" + area.getLatitude() + "," + area.getLongtitude() + ")");
		    	mMapView.setTreasure(mTreasureArea);
				mStartFlag = true;
				mGameCount++;
		    	Log.i(TAG, String.format("ゲーム開始 %d回目", mGameCount));
				updateScreen();
			}
		};

		Thread thread = new Thread(new Runnable() {
			public void run() {
				Area area = mAreaEngine.getRandomArea();
				Message msg = new Message();
				msg.what = 1;
				msg.obj = area;
				mHandler.sendMessage(msg);
			}
		});
		thread.start();
	}

	private void resumeGame() {
		mMapView.clear();

		if (mTreasureArea != null) {
			mMapView.setTreasureDiscovered(!mStartFlag);
			mMapView.setTreasure(mTreasureArea);
		}

		int size = mCheckPoints.size();
		for (int i = 0; i < size; i++) {
			CheckPoint checkPoint = mCheckPoints.get(i);
			mMapView.addCheckPoint(i + 1, checkPoint);
		}

		updateScreen();
	}

	private void updateScreen() {

		mMapView.invalidate();
		mTextMessage.setText(String.format("捜索回数: %d回", mCheckCount));
		mTextCheckResult.setText("");
	}

	private void errorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(R.string.app_name);
		builder.setMessage(message);
		builder.setPositiveButton(
				"OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}
		);
		builder.setCancelable(true);
		builder.show();
	}

	private void endGame() {
		mStartFlag = false;
	}

}
