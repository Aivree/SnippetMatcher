package com.renefernandez.whenapp.presentation.activity;

import com.renefernandez.whenapp.business.pictures.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.renefernandez.whenapp.R;
import com.renefernandez.whenapp.business.location.GPSTracker;
import com.renefernandez.whenapp.model.Moment;
import com.renefernandez.whenapp.model.dao.MomentDao;
import com.renefernandez.whenapp.presentation.dialog.DatePickerFragment;
import com.renefernandez.whenapp.presentation.dialog.TimePickerFragment;
import com.renefernandez.whenapp.presentation.util.DateFormater;
import com.renefernandez.whenapp.presentation.util.DialogHelper;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;
import android.provider.MediaStore;
import android.provider.Settings;

/**
 * Activity encargada de añadir nuevos Moment a la base de datos.
 * 
 * @author rene
 * 
 */
public class AddNewActivity extends ActionBarActivity implements
		OnDateSetListener, OnTimeSetListener {

	// MAP
	static final LatLng EUITIO = new LatLng(43.35560534, -5.850938559);
	private GoogleMap googleMap;
	private Marker marker;
	private GPSTracker gps;

	// MEDIA
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_LOAD_IMAGE = 2;
	private static final int ACTION_TAKE_VIDEO = 3;

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";

	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private static final String CURRENT_PHOTO_PATH = "currentPhotoPath";

	private Bitmap mImageBitmap;

	private static final String VIDEO_STORAGE_KEY = "viewvideo";
	private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
	private VideoView mVideoView;
	private Uri mVideoUri;

	private String mCurrentPhotoPath;

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	// Fields
	private EditText textTitle;

	private EditText textDate;
	private EditText textTime;
	private ImageView imageView;

	// /////////////////////////////////
	// /// Methods
	// ////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new);

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map_fragment);

		textTitle = (EditText) findViewById(R.id.editText1);
		textDate = (EditText) findViewById(R.id.editText2);
		textTime = (EditText) findViewById(R.id.editText3);

		imageView = (ImageView) findViewById(R.id.imgView);
		mVideoView = (VideoView) findViewById(R.id.videoView1);

		setLoadVideoButtonClickListener();

		loadInitialLocationOnMap(mapFragment);

		loadCalendarCurrentDate();
	}

	/**
	 * Establece un Listener para el botón de vídeo.
	 */
	private void setLoadVideoButtonClickListener() {
		Button videoButton = (Button) findViewById(R.id.button3);

		videoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v("rene", "loadVideo: ACTION_TAKE_VIDEO");
				dispatchTakeVideoIntent();

			}
		});
	}

	/**
	 * Establece la ubicación inicial en el mapa.
	 * 
	 * @param mapFragment
	 */
	private void loadInitialLocationOnMap(MapFragment mapFragment) {
		if (mapFragment == null)
			Log.e("AddNewActivity", "MAPFRAGMENT ES NULL");

		googleMap = mapFragment.getMap();

		if (googleMap != null) {

			gps = new GPSTracker(this);
			if (gps.canGetLocation()) { // gps enabled

				setMarkerInPosition(
						new LatLng(gps.getLatitude(), gps.getLongitude()), true);

			} else {
				showGPSSettingsAlert();
			}

		}
	}

	/**
	 * Establece la fecha actual en los campos de fecha y hora.
	 */
	private void loadCalendarCurrentDate() {
		Calendar calendar = Calendar.getInstance();

		textDate.setText(DateFormater.getDateFormated(
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)));

		textTime.setText(DateFormater.getTimeFormated(
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE)));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
	}

	protected void onResume() {
		super.onResume();

		Log.d("rene", "AddNewActivity onResume");
		if (gps != null)
			if (gps.canGetLocation())
				setMarkerInPosition(
						new LatLng(gps.getLatitude(), gps.getLongitude()), true);
	}

	protected void onPause() {
		super.onPause();
		if (gps != null)
			gps.stopUsingGPS();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.add_new, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {

		case R.id.action_save:

			Toast.makeText(this.getBaseContext(), "Saving...",
					Toast.LENGTH_SHORT).show();

			this.addNewMoment();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Crea un nuevo Moment y lo almacena en la base de datos.
	 */
	private void addNewMoment() {

		if (!momentIsValid()) {
			return;
		}

		String title = this.textTitle.getText().toString();
		Double latitude = this.marker.getPosition().latitude;
		Double longitude = this.marker.getPosition().longitude;

		Date date = getDateFromString();

		if (date == null) {
			DialogHelper.displayAlertDialog("Error",
					"The date format was incorrect.", this);
		}

		Moment newMoment = new Moment(title).withDate(date).withLocation(
				latitude, longitude);
		if (mCurrentPhotoPath != null) {
			newMoment.setImagePath(mCurrentPhotoPath);
			Log.v("rene", "Imagen salvada");
		}

		if (this.mVideoUri != null)
			newMoment.setVideoPath(this.getVideoPath());

		saveMomentInDatabase(newMoment);

	}

	/**
	 * Almacena el Moment parámetro en la BD.
	 * 
	 * @param newMoment
	 */
	private void saveMomentInDatabase(Moment newMoment) {
		MomentDao dao = new MomentDao(this);

		long id = dao.insert(newMoment);

		if (id < 0) {
			DialogHelper.displayAlertDialog("Error",
					"Moment could not be saved.", this);
			Log.e("rene", "Error saving moment in DB: Returned id was " + id);
			return;
		}

		Intent intent = new Intent(this, MainActivity.class);

		/** Starting the activity by passing the implicit intent */
		startActivity(intent);

		Toast.makeText(this.getBaseContext(), "Moment was added successfully!",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Obtiene la fecha de una cadena de texto.
	 * 
	 * @return
	 */
	private Date getDateFromString() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy-hh:mm",
				Locale.US);
		String dateInString = this.textDate.getText().toString() + "-"
				+ this.textTime.getText().toString();

		Date date = null;
		try {

			date = formatter.parse(dateInString);
			System.out.println(date);
			System.out.println(formatter.format(date));

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	/**
	 * Comprueba que los datos del Moment son aptos para almacenarlo.
	 * 
	 * @return
	 */
	private boolean momentIsValid() {
		// Precondiciones
		if (this.textTitle.getText().toString() == null
				|| this.textTitle.getText().toString().equals("")) {
			DialogHelper.displayAlertDialog("Error",
					"Moment title cannot be empty", this);
			return false;
		}
		if (this.textDate.getText().toString() == null
				|| this.textDate.getText().toString().equals("")) {
			DialogHelper.displayAlertDialog("Error",
					"Yout must set a date for the moment", this);
			return false;
		}

		if (this.textTime.getText().toString() == null
				|| this.textTime.getText().toString().equals("")) {
			DialogHelper.displayAlertDialog("Error",
					"Yout must set a time for the moment", this);
			return false;
		}

		if (this.marker == null) {
			DialogHelper.displayAlertDialog("Error",
					"Yout must set a location for the moment", this);
			return false;
		}
		return true;
	}

	/**
	 * Muestra el selector de fuente de las imágenes.
	 * 
	 * @param view
	 */
	public void displaySelectImageDialog(View view) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder
				.setTitle("Select image source")

				.setPositiveButton("Take photo",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Log.v("rene",
										"displaySelectImageDialog: ACTION_TAKE_PHOTO_B");
								dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
							}
						})

				.setNegativeButton("Select from Media",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dispatchLoadPictureIntent();
							}

						});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

	/**
	 * Lanza el Intent de carga de imágenes
	 */
	private void dispatchLoadPictureIntent() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, ACTION_LOAD_IMAGE);

	}

	/**
	 * Muestra el diálogo de carga de fecha.
	 * 
	 * @param v
	 */
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	/**
	 * Muestra el diálogo de carga de hora.
	 * 
	 * @param v
	 */
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Log.v("DTE", "Date selected: " + year + "/" + monthOfYear + "/"
				+ dayOfMonth);

		textDate.setText(DateFormater.getDateFormated(year, monthOfYear,
				dayOfMonth));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Log.v("DTE", "Time selected: " + hourOfDay + "/" + minute);
		textTime.setText(DateFormater.getTimeFormated(hourOfDay, minute));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				Log.v("rene", "onActivityResult: ACTION_TAKE_PHOTO_B");
				handleBigCameraPhoto();
			}
			break;
		} // ACTION_TAKE_PHOTO_B

		case ACTION_LOAD_IMAGE: {
			if (resultCode == RESULT_OK) {
				handleLoadPicture(data);
			}
			break;
		} // ACTION_TAKE_PHOTO_S

		case ACTION_TAKE_VIDEO: {
			if (resultCode == RESULT_OK) {
				handleCameraVideo(data);
			}
			break;
		} // ACTION_TAKE_VIDEO
		} // switch
	}

	/**
	 * Gestiona la carga de imágenes desde la biblioteca.
	 * 
	 * @param data
	 */
	private void handleLoadPicture(Intent data) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		this.mCurrentPhotoPath = cursor.getString(columnIndex);
		cursor.close();

		this.setPictureOnImageView();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);

		Log.v("rene", "onSaveInstance");

		if (this.marker == null)
			Log.v("rene", "el marcador es null");
		savedInstanceState.putString("title", this.textTitle.getText()
				.toString());
		savedInstanceState
				.putString("date", this.textDate.getText().toString());
		savedInstanceState
				.putString("time", this.textTime.getText().toString());
		savedInstanceState.putDouble("latitude",
				this.marker.getPosition().latitude);
		savedInstanceState.putDouble("longitude",
				this.marker.getPosition().longitude);

		savedInstanceState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		savedInstanceState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
		savedInstanceState.putString(CURRENT_PHOTO_PATH, mCurrentPhotoPath);
		savedInstanceState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
				(mImageBitmap != null));
		savedInstanceState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY,
				(mVideoUri != null));
	}

	@Override
	public void onRestoreInstanceState(Bundle b) {
		super.onSaveInstanceState(b);

		Log.v("rene", "onrestoreInstance");

		if (b.getString("title") != null)
			this.textTitle.setText(b.getString("title"));

		if (b.getString("date") != null)
			this.textDate.setText(b.getString("date"));

		if (b.getString("time") != null)
			this.textTime.setText(b.getString("time"));

		setMarkerInPosition(
				new LatLng(b.getDouble("latitude"), b.getDouble("longitude")),
				true);

		mImageBitmap = b.getParcelable(BITMAP_STORAGE_KEY);
		mVideoUri = b.getParcelable(VIDEO_STORAGE_KEY);
		mCurrentPhotoPath = b.getString(CURRENT_PHOTO_PATH);
		imageView.setImageBitmap(mImageBitmap);
		/*
		 * imageView .setVisibility(savedInstanceState
		 * .getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE :
		 * ImageView.INVISIBLE);
		 */
		mVideoView.setVideoURI(mVideoUri);
		setPictureOnImageView();

	}

	/**
	 * En caso de que la localización esté desactivada, muestra un diálogo
	 * indicando que se debe activar y redirige a Ajustes en caso de que sea
	 * necesario.
	 */
	public void showGPSSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						setMarkerInPosition(EUITIO, true);
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	/**
	 * Mueve el marcador de posición
	 * @param position
	 * @param withZoom
	 */
	private void setMarkerInPosition(LatLng position, boolean withZoom) {

		if (marker == null) {
			marker = googleMap.addMarker(new MarkerOptions().position(position)
					.title("Your Location").draggable(true));
		} else {
			marker.setPosition(position);
		}

		if (withZoom == true) {

			googleMap.moveCamera(CameraUpdateFactory
					.newLatLngZoom(position, 17));

			googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

		}

	}

	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory
					.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
				albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	/**
	 * Para evitar problemas de memoria es necesario redimensionar la imagen
	 */
	private void setPictureOnImageView() {

		Log.v("rene", "setPic");

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = imageView.getWidth();
		int targetH = imageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
		imageView.setImageBitmap(bitmap);
		// mVideoUri = null;
		// imageView.setVisibility(View.VISIBLE);
		// mVideoView.setVisibility(View.INVISIBLE);
	}

	private void addPictureToGallery() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	/**
	 * Llama al Intent encargado de tomar fotografías.
	 * @param actionCode
	 */
	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Log.v("rene", "dispatchTakePictureIntent: ACTION_IMAGE_CAPTURE");
		switch (actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;

			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}

	private void dispatchTakeVideoIntent() {
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
	}

	private void handleBigCameraPhoto() {
		Log.v("rene", "handleBigCameraPhoto:" + mCurrentPhotoPath);
		if (mCurrentPhotoPath != null) {
			setPictureOnImageView();
			addPictureToGallery();
		}

	}

	private void handleCameraVideo(Intent intent) {
		mVideoUri = intent.getData();
		mVideoView.setVideoURI(mVideoUri);
	}

	public String getVideoPath() {

		return this.mVideoUri.toString();

	}

}
