package com.vijay.promoteurnotes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class AddNotesActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {
	private Button backbutton, savebutton;// , imagebutton, videobutton,
											// recordingbutton;
	private EditText titleEdittext, notesEdittext;
	private ImageView img1, img2,  recordingimg;//videoimg
	private Spinner categoryspinner;
	private LocationManager locationManager;
	private int RESULT_LOAD_IMAGE1 = 1;
	private int CAMERA_PIC_IMAGE1 = 2;
	private int RESULT_LOAD_IMAGE2 = 11;
	private int CAMERA_PIC_IMAGE2 = 21;
	private int VIDEO_IMAGE = 3;
	private int RECORD_IMAGE = 4;
	private Uri selectedImage1, selectedImage2;
	private String base64Image = "";
	private String img1data = "", img2data = "";
	private Bitmap bm1, bm2;
	private Dialog dialog;
	private int imgCount = 0;
	private MyDataBase mdb = null;
	private double lat, lon;
	private String spinnerselectVal;
	private List<String> lables = new ArrayList<String>();
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private Boolean isLocationConnected = false;
	private boolean isNetworkEnabled = false;
	private Boolean isimageset1 = false,isimageset2 = false;
	private String imagefilename1, imagefilename2;
	private int savecount=0;
	private String recordingpath="", videopath="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnoteactivity);
		backbutton = (Button) findViewById(R.id.backbutton);
		savebutton = (Button) findViewById(R.id.savebutton);
		titleEdittext = (EditText) findViewById(R.id.titleEdittext);
		notesEdittext = (EditText) findViewById(R.id.notesEdittext);
		categoryspinner = (Spinner) findViewById(R.id.categoryspinner);

		/*
		 * imagebutton = (Button) findViewById(R.id.imagebutton); videobutton =
		 * (Button) findViewById(R.id.videobutton); recordingbutton = (Button)
		 * findViewById(R.id.recordingbutton);
		 */

		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		//videoimg = (ImageView) findViewById(R.id.videoimg);
		recordingimg = (ImageView) findViewById(R.id.recordingimg);

		mdb = new MyDataBase(AddNotesActivity.this);
		loadSpinnerData();
		// locationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		/*
		 * locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		 * 0, 0, this);
		 */

		categoryspinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {

				spinnerselectVal = (String) parent.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		backbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddNotesActivity.this.finish();
			}
		});
		savebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				savecount=1;
				// saveNote();
				if (isLocationServiceEnable()) {
					if (locationclient == null) {
						initiateLocation();
						/*
						 * if (!isLocationConnected && locationclient != null) {
						 * locationclient.connect(); }
						 */
					} else {
						locationclient
								.requestLocationUpdates(
										locationrequest,
										new com.google.android.gms.location.LocationListener() {

											@Override
											public void onLocationChanged(
													Location location) {
												lat = location.getLatitude();
												lon = location.getLongitude();

												locationclient
														.removeLocationUpdates(AddNotesActivity.this);
																								
												if(savecount>0){
													savecount=0;
												saveNote();
												}
											}
										});
					}
				} else {

					showSettingsAlert();
				}
			}
		});
		img1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSelectImageOptions(RESULT_LOAD_IMAGE1, CAMERA_PIC_IMAGE1);
			}
		});
		img2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSelectImageOptions(RESULT_LOAD_IMAGE2, CAMERA_PIC_IMAGE2);
			}
		});
		/*videoimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, VIDEO_IMAGE);

				
				 * Uri contentUri = ContentUris.withAppendedId(
				 * MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_IMAGE);
				 * 
				 * try { Intent intent = new Intent(Intent.ACTION_VIEW,
				 * contentUri); startActivity(intent); } catch
				 * (ActivityNotFoundException e) {
				 * Toast.makeText(AddNotesActivity.this, "Not Supported",
				 * Toast.LENGTH_SHORT).show(); }
				 

			}
		});*/
		recordingimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(AddNotesActivity.this,AudioRecordingActivity.class);
				startActivityForResult(i, RECORD_IMAGE);
			}
		});
		
		
		/*
		 * imagebutton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { showSelectImageOptions(); }
		 * }); videobutton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * 
		 * Intent i = new Intent( Intent.ACTION_PICK,
		 * android.provider.MediaStore .Images.Media.EXTERNAL_CONTENT_URI);
		 * startActivityForResult(i, RESULT_LOAD_IMAGE);
		 * 
		 * 
		 * Uri contentUri = ContentUris.withAppendedId(
		 * MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_IMAGE);
		 * 
		 * try { Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
		 * startActivity(intent); } catch (ActivityNotFoundException e) {
		 * Toast.makeText(AddNotesActivity.this, "Not Supported",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } }); recordingbutton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub
		 * 
		 * } });
		 */

		Bitmap bitmap = (Bitmap) getLastNonConfigurationInstance();
		if (bitmap != null) {
			//isimageset1 = true;
			System.out.println("isimageset1 :"+isimageset1);
			System.out.println("isimageset2 :"+isimageset2);
			// captureimage.setImageBitmap(bitmap);
			// captureimage.setRotation((float) rotateImage);
			if (isimageset1) {
				img1.setImageBitmap(bitmap);
			} else if (isimageset2) {
				img2.setImageBitmap(bitmap);
			} 
		}
		if (isLocationServiceEnable()) {
			initiateLocation();
		} else {
			showSettingsAlert();
		}

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("isimageset1", true);
		outState.putBoolean("isimageset2", true);
		/*outState.putDouble("myDouble", 1.9);
		outState.putInt("MyInt", 1);*/
		outState.putString("imagefilename1", ""+imagefilename1);
		outState.putString("imagefilename2", ""+imagefilename2);
		outState.putString("recordingpath", ""+recordingpath);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		isimageset1 = savedInstanceState.getBoolean("isimageset1");
		isimageset2 = savedInstanceState.getBoolean("isimageset2");
		/* double myDouble = savedInstanceState.getDouble("myDouble");
		  int myInt = savedInstanceState.getInt("MyInt");*/
		  imagefilename1 = savedInstanceState.getString("imagefilename1");
		  imagefilename2 = savedInstanceState.getString("imagefilename2");
		  recordingpath = savedInstanceState.getString("recordingpath");
		  setImages(isimageset1,isimageset2);
	}
	
	private void setImages(Boolean isimageset12, Boolean isimageset22) {
		// TODO Auto-generated method stub
		
	}

	protected void saveNote() {
		// System.out.println(spinnerselectVal);
		if (validateFields()) {
			String category = categoryspinner.getSelectedItem().toString();
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String timestamp = df.format(cal.getTime());
			if (isimageset1) {
				Writeimagefile(selectedImage1, imagefilename1,
						AddNotesActivity.this);
			}
			if (isimageset2) {
				Writeimagefile(selectedImage2, imagefilename2,
						AddNotesActivity.this);
			}
			System.out.println("recordingpathrecordingpath: "+recordingpath);
			if (mdb.insertNote(titleEdittext.getText().toString(),
					notesEdittext.getText().toString(), category, imagefilename1,imagefilename1,imagefilename2,imagefilename2,
					timestamp, lat, lon,recordingpath,videopath)) {
				
				/*if (mdb.insertNote(titleEdittext.getText().toString(),
						notesEdittext.getText().toString(), category, img1data,
						img2data, timestamp, lat, lon)) {*/
				Toast.makeText(AddNotesActivity.this, "Note Added",
						Toast.LENGTH_LONG).show();
				clearallformfeilds();
				
				this.finish();
			} else {
				Toast.makeText(AddNotesActivity.this, "Error, Try again..",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private boolean validateFields() {

		// String cat=categoryspinner.getSelectedItem().toString();
		if (titleEdittext.getText().toString().length() <= 0) {
			titleEdittext.requestFocus();
			Toast.makeText(AddNotesActivity.this, "Enter title",
					Toast.LENGTH_LONG).show();
			return false;
		}
		if (lables.size() <= 0) {
			titleEdittext.requestFocus();
			Toast.makeText(AddNotesActivity.this, "Create Category",
					Toast.LENGTH_LONG).show();
			return false;
		}
		if (notesEdittext.getText().toString().length() <= 0) {
			notesEdittext.requestFocus();
			Toast.makeText(AddNotesActivity.this, "Enter Notes",
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			return true;
		}

	}

	private void showSelectImageOptions(final int RESULT_LOAD_IMAGE,
			final int CAMERA_PIC_IMAGE) {
		dialog = new Dialog(AddNotesActivity.this, R.style.DialogSlideAnim);
		dialog.setContentView(R.layout.dialogviewlayout);
		Button photoalbum = (Button) dialog.findViewById(R.id.photoalbum);
		Button camera = (Button) dialog.findViewById(R.id.camera);
		Button btncancel = (Button) dialog.findViewById(R.id.cancel);
		photoalbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePicture, CAMERA_PIC_IMAGE);
			}
		});
		btncancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();

			}
		});
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// dialog.cancel();
		if (requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK
				&& null != data) {
			try {
				isimageset1 = true;

				selectedImage1 = data.getData();
				// captureimage.setImageURI(selectedImage);

				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage1,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				File inputfile = new File(picturePath);

				Bitmap bm = decodeFile(inputfile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] b = baos.toByteArray();
				base64Image = Base64.encodeToString(b, Base64.DEFAULT);

				img1data = base64Image;
				// img1.setImageBitmap(bm);

				// //////////////////////////////

				img1.setImageBitmap(decodeSampledBitmapFromResource(
						picturePath, 100, 100));

				// retrieving the input filename
				imagefilename1 = inputfile.getName();
				Log.v("filename", imagefilename1);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		else if (requestCode == CAMERA_PIC_IMAGE1 && resultCode == RESULT_OK
				&& null != data) {
			try {
				isimageset1 = true;

				selectedImage1 = data.getData();
				// captureimage.setImageURI(selectedImage);

				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage1,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				// retrieving the input filename
				File inputfile = new File(picturePath);

				Bitmap bm = decodeFile(inputfile);
				// SingletoneClass.getInstance().setBitmap(bm);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] b = baos.toByteArray();
				base64Image = Base64.encodeToString(b, Base64.DEFAULT);

				img1data = base64Image;
				// img1.setImageBitmap(bm);
				// captureimage.setRotation((float) rotateImage);
				// ////////////////////////////
				img1.setImageBitmap(decodeSampledBitmapFromResource(
						picturePath, 100, 100));

				// retrieving the input filename
				imagefilename1 = inputfile.getName();
				Log.v("filename", imagefilename1);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (requestCode == RESULT_LOAD_IMAGE2 && resultCode == RESULT_OK
				&& null != data) {
			try {
				isimageset2 = true;

				selectedImage2 = data.getData();
				// captureimage.setImageURI(selectedImage);

				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage2,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				File inputfile = new File(picturePath);

				Bitmap bm = decodeFile(inputfile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] b = baos.toByteArray();
				base64Image = Base64.encodeToString(b, Base64.DEFAULT);

				img2data = base64Image;
				// img2.setImageBitmap(bm);

				// //////////////////////////////

				img2.setImageBitmap(decodeSampledBitmapFromResource(
						picturePath, 100, 100));

				// retrieving the input filename
				imagefilename2 = inputfile.getName();
				Log.v("filename", imagefilename2);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		else if (requestCode == CAMERA_PIC_IMAGE2 && resultCode == RESULT_OK
				&& null != data) {
			try {
				isimageset2 = true;

				selectedImage2 = data.getData();
				// captureimage.setImageURI(selectedImage);

				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage2,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				// retrieving the input filename
				File inputfile = new File(picturePath);

				Bitmap bm = decodeFile(inputfile);
				// SingletoneClass.getInstance().setBitmap(bm);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] b = baos.toByteArray();
				base64Image = Base64.encodeToString(b, Base64.DEFAULT);

				img2data = base64Image;
				// img2.setImageBitmap(bm);
				// captureimage.setRotation((float) rotateImage);

				// ////////////////////////////
				img2.setImageBitmap(decodeSampledBitmapFromResource(
						picturePath, 100, 100));

				// retrieving the input filename
				imagefilename2 = inputfile.getName();
				Log.v("filename", imagefilename2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}/* else if ((requestCode == VIDEO_IMAGE) && (resultCode == RESULT_OK)
				&& (data != null)) {

			// mIntentFromGallery = data;

			
			 * Intent intent = new Intent(Intent.ACTION_MAIN, null);
			 * intent.setType("video/*"); intent.setData(data.getData()); try {
			 * startActivity(intent); } catch (Exception e) { }
			 

			Uri videoUri = data.getData();
			String path = videoUri.getPath();

			// int id = "The Video's ID";
			ContentResolver crThumb = getContentResolver();
			
			String[] proj = { BaseColumns._ID };
			Cursor c = crThumb.query(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null,
					null, null);
			if (c.moveToFirst()) {
				do {
					int id = c.getInt(0);
					int id1 = c.getInt(c
							.getColumnIndex(MediaStore.Video.Media._ID));
					Bitmap b = MediaStore.Video.Thumbnails.getThumbnail(
							crThumb, id, MediaStore.Video.Thumbnails.MINI_KIND,
							null);
					Bitmap b1 = MediaStore.Video.Thumbnails.getThumbnail(
							crThumb, id1,
							MediaStore.Video.Thumbnails.MINI_KIND, null);
					Log.d("*****My Thumbnail*****", "onCreate bitmap " + b);
					img1.setImageBitmap(b);
					videoimg.setImageBitmap(b);
				} while (c.moveToNext());
			}
			c.close();

		}*/else if ((requestCode == RECORD_IMAGE) && (resultCode == RESULT_OK)
				&& (data != null)) {
				
			recordingpath=data.getExtras().getString("recordingpath");
			
		} else {
			setResult(RESULT_CANCELED);
			// finish();
		}

	}

	private Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * @Override public void onLocationChanged(Location location) { //
	 * txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" // +
	 * location.getLongitude()); lat= location.getLatitude();
	 * lon=location.getLongitude(); }
	 * 
	 * @Override public void onProviderDisabled(String provider) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onProviderEnabled(String provider) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onStatusChanged(String provider, int status, Bundle
	 * extras) { // TODO Auto-generated method stub
	 * 
	 * }
	 */

	private void loadSpinnerData() {
		lables = mdb.getAllCategories();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, lables);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categoryspinner.setAdapter(dataAdapter);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	private Boolean isLocationServiceEnable() {
		locationManager = (LocationManager) AddNotesActivity.this
				.getSystemService(LOCATION_SERVICE);

		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (isNetworkEnabled) {
			return true;
		} else {
			return false;
			// showSettingsAlert();
		}
	}

	private void initiateLocation() {
		int resp = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(AddNotesActivity.this);
		if (resp == ConnectionResult.SUCCESS) {
			locationrequest = LocationRequest.create();
			locationrequest.setInterval(0);
			locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationrequest.setFastestInterval(0);

			locationclient = new LocationClient(this, this, this);
			if (!isLocationConnected && locationclient != null) {
				locationclient.connect();
			}
			// getLocationDetails();
		} else {

			Dialog dialog = GooglePlayServicesUtil
					.getErrorDialog(resp, this, 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(getSupportFragmentManager(), "Notes");
			} else {
				Toast.makeText(this, "Google Play Service Error " + resp,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 * 
		 * @param dialog
		 *            An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (isLocationServiceEnable()) {
			if (!isLocationConnected && locationclient != null) {
				locationclient.connect();
			}
			// initiateLocation();
		}/*
		 * else { showSettingsAlert(); }
		 */
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				AddNotesActivity.this);

		alertDialog.setTitle("Settings");
		alertDialog
				.setMessage("Enable Use wireless Networks, Do you want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						AddNotesActivity.this.startActivity(intent);
						// startActivityForResult(intent, LOCATION_SERVICE);/
						dialog.cancel();
					}
				});
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
	}

	public void clearallformfeilds() {
		savecount=0;
		imagefilename1 = null;
		imagefilename2 = null;
		isimageset1 = false;
		isimageset2 = false;
		titleEdittext.setText("");
		notesEdittext.setText("");

	}

	public static Bitmap decodeSampledBitmapFromResource(String resId,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(resId, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
	public void Writeimagefile(Uri uri, String filename, Context context) {

		try {

			InputStream imageStream = context.getContentResolver()
					.openInputStream(uri);

			OutputStream out = context.getApplicationContext().openFileOutput(
					filename, 1);
			// OutputStream out = new FileOutputStream(new
			// File("c:\\newfile.xml"));
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = imageStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			imageStream.close();
			out.flush();
			out.close();
		} catch (Exception ex) {
			Log.v("Imagewriting", ex.getMessage());
		}
	}

	
}
