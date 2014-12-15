package mobilize.me;


import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;

import mobilize.me.background.MapUpdater;
import mobilize.me.utils.BitmapFixer;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * The main activity for our app which is the Map of the protests. We are using the Support Fragments
 * so we can target older devices. This class really needs a major overhaul.
 * {@link LocationClient}.
 */
public class OccupyMapActivity extends FragmentActivity
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
//        OnMyLocationButtonClickListener,
        LocationResultListener, OnMarkerClickListener {

	private ConnectivityManager cMgr;

    private GoogleMap gMap;

    private LocationClient mLocationClient;
    private TextView mMessageView;
        
    private double lastLatitude;
    private double lastLongitude;
    
    private static final int REQUEST_CODE = 1; // Camera Request
    private Bitmap bitmap;
    private ImageView imageView;

	private Uri mImageUri;

	private File outputFileName;
	
	File photo;

	private String userId;
	
	// Image Loader for Google Map
    private Hashtable<String, String> markers;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

	private Handler recurringTaskHandler;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private static final String TAG = "MapActivity";
	
	// Spice Manager for making Network calls
	/***
     * With {@link UncachedSpiceService} there is no cache management.
     * Remember to declare it in AndroidManifest.xml
     */
//	protected SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
	
	// The progress bar when user captures a protest
	ProgressBar progressbar;
	
	// Handle protest pin results after they're returned from the server
	Handler foundProtestsHandler = new Handler();

	private Runnable recurringTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		requestWindowFeature(Window.FEATURE_PROGRESS);
		
        setContentView(R.layout.activity_map);
//        mMessageView = (TextView) findViewById(R.id.message_text);    
        
        // Get a pointer to our progress bar
        progressbar = (ProgressBar) findViewById(R.id.progress_bar);

        this.cMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);  
        
        // We need to create a unique user id for this user we also want 
        // to make sure this id is only created the first time the app is run
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // code here will only be run once after the app has been installed
        if(!prefs.getBoolean("app_configured", false)) {
            // Let's build a pseudo unique user ID from the user's telephone information
            // This string will end up looking like this: 355715565309247
            String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                	Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + 
                	Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + 
                	Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + 
                	Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + 
                	Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + 
                	Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + 
                	Build.USER.length() % 10 ; //13 digits
            // The preferences object is like a local database for shared preferences
            SharedPreferences.Editor editor = prefs.edit();
            // Save the user's unique id
            editor.putString("user_id", m_szDevIDShort);            
        	// Let the OS know that we have configured the app and not to run this code again	        
	        editor.putBoolean("app_configured", true);
	        editor.commit();
        }
        
        // get the user's id and save it for future use while he's using the app
        userId = prefs.getString("user_id", "INVALID_ID");
        
        
        // Create the Map markers
        markers = new Hashtable<String, String>();
        
        // Create the Drawer that will slide from the bottom displaying the image
//		SlidingUpPanelLayout infoDrawer = (SlidingUpPanelLayout) findViewById(R.id.infobox);
		
		// drawer preferences
//		infoDrawer.setPanelHeight(0);
		
		
		
		// Create our background task that will update the pins
		MapUpdater mapUpdater = new MapUpdater();
		
		
		
		// TODO Remove this Test code
		// try to make a thread see how well that works
        // Start lengthy operation in a background thread
		
		// Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
//		NetworkLoaderClient networkLoaderClient = new NetworkLoaderClient( getApplicationContext() );
//        getSupportLoaderManager().initLoader(0, null, networkLoaderClient);
        
		
		recurringTaskHandler = new Handler();	
        recurringTask = new Runnable() {
        	PointsRetrievalTask protestRetrievalTask;
            public void run() {
            	protestRetrievalTask = new PointsRetrievalTask();
            	protestRetrievalTask.execute();
            	// rerun this task every 60 seconds 
        		recurringTaskHandler.postDelayed(recurringTask, 60000);
            }            
        };        
        
        bitmapFixer = new BitmapFixer();
        

        setUpLocationClientIfNeeded();
    }
    
    public void updateProtestPoints() {

		// TODO Move this code somewhere else
//    	protests = ProtestMapSingleton.getInstance().getProtestMap();
		
		foundProtestsHandler.post(new Runnable() {
			
			@Override
			public void run() {				
				gMap.clear();
				Marker marker;
				protestMap.clear();
				
				if( ProtestMapSingleton.getInstance().getProtestMap() == null )
					return;
				
				for( Protest protest : ProtestMapSingleton.getInstance().getProtestMap() ) {					
					marker = gMap.addMarker( new MarkerOptions()
								.position( new LatLng(protest.getLatitude(), protest.getLongitude() ) )
//								.title("TITLE")
						);
					protestMap.put( marker.getId(), protest );						
				}
			}
		});   	
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

    @Override
	protected void onStart() {
        super.onStart();
        // Run the repeating thread that updates the map pins
        recurringTask.run();
	}


	@Override
	protected void onStop() {
        super.onStop();
        // remove all repeating threads
        recurringTaskHandler.removeCallbacks(recurringTask);
	}


	@Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        // Connect the client
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (gMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (gMap != null) {
//                SlidingDrawer infoDrawer = (SlidingDrawer) findViewById(R.id.infobox);
                gMap.setMyLocationEnabled(true);
                // pan to the user's location

//                gMap.setOnMyLocationButtonClickListener(this);
//                gMap.setOnMarkerClickListener( new MarkerClickListener(infoDrawer) );
                gMap.setOnMarkerClickListener( this );
//                gMap.setInfoWindowAdapter( new CustomInfoWindow(getLayoutInflater()) );
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient( getApplicationContext(), this, this ); // OnConnectionFailedListener
        }
    }
        
    public void captureClash(View view) {
    	ServerAccess.DEV_SITE = "https://demo-project-c9-developerux.c9.io";
    	ServerAccess.PORT = "8080";
    	takePicture(view);
    }
    public void captureProtest(View view) {
    	takePicture(view);
    }
    public void captureIncident(View view) {
    	takePicture(view);
    }
    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    public void takePicture(View view) {    		
    	/**
    	 * STEP 1:
    	 * Create Intent to take a picture and return control to the calling application
    	 * Using android.provider.MediaStore.ACTION_IMAGE_CAPTURE, we can launch an intent 
    	 * to take the picture without having to specify any Camera APIs 
    	 */
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
        
        try {
	        // place where to store camera taken picture
        	if( ServerAccess.DEV )	photo = createTemporaryFile("picture", ".jpg");
        	else 					photo = createPermFile("picture", ".jpg");       	
	        photo.delete();
        }
        
        catch(Exception e) {
        	Log.w(TAG, "Can't create file to take picture!");
            Toast.makeText(this, "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG).show();
            return;
        }
                       
        mImageUri = Uri.fromFile(photo);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri); // set the image file name
//        takePictureIntent.setData(mImageUri);        
                
        // start the image capture Intent
        startActivityForResult(takePictureIntent, REQUEST_CODE);        
    }
    
    // Create a temporary file for the image to be saved in
    private File createTemporaryFile(String part, String ext) throws Exception {
    	// STEP 4 :: Create a new file that will be stored in the SD card with a name; here “image.jpg”.
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdir();
        }
        // STEP 5: Write the Bitmap to the file by writing the BiteArrayOutputStream to the file using a FileOutputStream.
        return File.createTempFile(part, ext, tempDir);
    }
    
    /**
     * Store the image file permanently on the device. 
     * TODO: Add functionality to allow user to choose if he wants to store permanently and add images to Gallery
     * @param part beginning name of picture
     * @param ext extension of the image
     * @return The file location to store the image
     * @throws Exception
     */
    private File createPermFile(String part, String ext) throws Exception {
    	// STEP 4 :: Create a new file that will be stored in the SD card with a name; here “image.jpg”.
    	File storageDir = new File(
    		    Environment.getExternalStoragePublicDirectory(
    		        Environment.DIRECTORY_PICTURES
    		    ), 
    		    "R4BIA"
    		);   

	    // Check if the directory already exists
	    if( !storageDir.exists() )
	    	storageDir.mkdirs();	// If it doesn't, make one
	    
	    // STEP 5: Write the Bitmap to the file by writing the BiteArrayOutputStream to the file using a FileOutputStream.
	    return File.createTempFile(part, ext, storageDir);
    }
    

	BitmapFixer bitmapFixer;
	
    /** 
     * Called after the camera intent is finished and a picture has been captured 
    **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	if (requestCode == REQUEST_CODE) {    		
            if (resultCode == RESULT_OK) {
            	 
//            	Uri imageFileUri = intent.getData();
                            	
                // Image captured and saved to fileUri specified in the Intent            	
            	Toast.makeText(this, "Image saved to your phone", Toast.LENGTH_LONG).show();
//                Toast.makeText(this, "Image saved to:\n" + intent.getData(), Toast.LENGTH_LONG).show();
//                ImageView imageView = new ImageView(MyLocationDemoActivity.this);
                //... some code to inflate/create/find appropriate ImageView to place grabbed image
//                this.grabImage(imageView);
                
            	// Scale down the original image so we could upload faster
            	Bitmap scaledBitmap = bitmapFixer.decodeSampledBitmap(mImageUri.getPath(), 1600, 1600);
            	String imageBase64 = bitmapFixer.compressImage(scaledBitmap);
            	// Scale down the original image again to create our thumbnail that we will load first
            	Bitmap thumbBitmap = bitmapFixer.decodeSampledBitmap(mImageUri.getPath(), 240, 240);
            	String thumbBase64 = bitmapFixer.compressImage(thumbBitmap);
            	
        		// Run upload task in background to upload the image to the backend server along with location coordinates
            	ImageUploaderTask uploaderTask = new ImageUploaderTask();
            	// SEND!
            	uploaderTask.execute( thumbBase64, imageBase64, String.valueOf(lastLatitude), String.valueOf(lastLongitude), userId );
            	
            	
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            	Toast.makeText(this, "Could not save image to:\n" + photo, Toast.LENGTH_LONG).show();
            }
        }
    	
		if (mLocationClient != null && mLocationClient.isConnected()) {        	
			String msg = "Location = " + mLocationClient.getLastLocation();
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();      
		}
		
				
    }

    /** After the image is captured and the intent returns, we just grab the image from the URI */
    public void grabImage(ImageView imageView) {
    	
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        
        /** 
         * STEP 2:
         * From the data extras we get back from ActivityonResult, 
         * we can get the image Bitmap and put it in our ImageView. 
        */
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
        }
        
        catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
    }
    

	private class ImageUploaderTask extends AsyncTask<String, Integer, Void> {
		
		@Override
		protected void onPreExecute(){
			progressbar.setVisibility(View.VISIBLE);	// show the progress bar
			setProgressBarIndeterminate(true);
			setProgressBarIndeterminateVisibility(true);	
		}
		
		@Override
		protected Void doInBackground(String... params) {
			ServerAccess.sendToServer( params[0], params[1], params[2], params[3], params[4] );
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			progressbar.setVisibility(View.INVISIBLE);	// hide the progressbar
		}
	}

	private class PointsRetrievalTask extends AsyncTask<String, Integer, Void> {
		
		@Override
		protected void onPreExecute(){
			progressbar.setVisibility(View.VISIBLE);	// show the progress bar
			setProgressBarIndeterminate(true);
			setProgressBarIndeterminateVisibility(true);	
		}
		
		@Override
		protected Void doInBackground(String... params) {
			// Get the protest data from the backend
			ProtestMapSingleton.getInstance().set( ServerAccess.updateFromServer("") );
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			// update the map with the new protest spots
			updateProtestPoints();
			progressbar.setVisibility(View.INVISIBLE);	// hide the progressbar
		}
	}
	
//	List<Protest> protests;
	HashMap<String, Protest> protestMap = new HashMap<String, Protest>();
//	int c = 0;
    
    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
    	boolean firstLocationLock = false; 
    	if( lastLatitude == 0.0 && lastLongitude == 0.0 )
    		firstLocationLock = true;
    		
    	if( location != null ) {
	        lastLatitude = location.getLatitude();
	        lastLongitude = location.getLongitude();
    	}
    	
    	if( firstLocationLock ) {
			// Pan to the user's location now that we found him!
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
		    gMap.animateCamera(cameraUpdate);
    	}
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates( REQUEST, this );  // LocationListener
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

//    @Override
//    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
//        
//        return false;
//    }

    /** 
     * From LocationResultListener :: Called when user location has been found
     * For some odd reason this function is not being called 
     **/
	@Override
	public void onLocationResultAvailable(Location location) {
		if( location != null ) {
			// Save the user's new found location
			lastLatitude = location.getLatitude();
			lastLongitude = location.getLongitude();
		}
	}


	@Override
	public boolean onMarkerClick(Marker marker) {		
		return new_functionality(marker);
//		return old_functionality(marker);		
	}
	
	// Include package name to ensure intent extra is unique device wide
	public final static String IMAGE_URL = "mobilize.me.IMAGE_URL";
	public static final String THUMBNAIL = "mobilize.me.THUMBNAIL_URL";	
	
	public boolean new_functionality(Marker marker) {
		Intent intent = new Intent(this, ProtestInfoActivity.class);
		intent.putExtra(IMAGE_URL, protestMap.get( marker.getId() ).getImageUrl());	// attach the image's url info
//		Bundle bitmapBundle = new Bundle();
//		bitmapBundle.putParcelable(THUMBNAIL, protestMap.get( marker.getId() ).getThumbnail());
		intent.putExtra(THUMBNAIL, protestMap.get( marker.getId() ).getThumbnail());	// attach the image's thumbnail url 
		startActivity( intent );	// Show the Image in a new Activity
		return false;
	}
	
	public boolean old_functionality(Marker marker) {
//		SlidingUpPanelLayout infoSlidingPanel = (SlidingUpPanelLayout) findViewById(R.id.infobox);
		
				
//		infoDrawer.setSlidingEnabled(false);		
//		infoDrawer.setAnchorPoint(0.5f);		
		
		// Getting reference to the TextView to set title
		
//		TextView title = (TextView) findViewById(R.id.description);                       
//		ImageView imageView = ((ImageView) findViewById(R.id.image));
		
		// restrict the drag area of the sliding panel to a specific view. 
		// Otherwise, the whole panel will be slideable and it will intercept all clicks.
//		infoSlidingPanel.setDragView( imageView );
//		infoSlidingPanel.setPanelHeight(10);
		
//		title.setText( marker.getTitle() );
		  
		// load the image
		imageLoader.displayImage( protestMap.get( marker.getId() ).getImageUrl(), imageView, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				imageBitmap = loadedImage;
			}
		});
		  
//      	imageLoader.displayImage( protestMap.get( marker.getId() ).getImageUrl(), imageView);
		
		// if the Infobox is already showing hide it        
//        if( infoSlidingPanel.isExpanded() )
//        	infoSlidingPanel.collapsePane();
//        // else show it
//        else
//        	infoSlidingPanel.expandPane(0);
        
		return false;
		
	}
	
	Bitmap imageBitmap;
	
	@Override
	public void onSaveInstanceState(Bundle bundle){
		super.onSaveInstanceState(bundle);
		bundle.putParcelable("image", imageBitmap);
		bundle.putParcelable("photoUri", mImageUri);
		// if we have a location other than the default [0,0] save it for when the camera returns
		if( lastLatitude != 0.0 || lastLongitude != 0.0 ) {
			bundle.putDouble("latitude", lastLatitude);
			bundle.putDouble("longitude", lastLongitude);
		}
	}
	@Override
	public void onRestoreInstanceState(Bundle b){
		super.onRestoreInstanceState(b);
		//you need to handle NullPionterException here.
		if( b.getParcelable("photoUri") != null )
			mImageUri = b.getParcelable("photoUri");
		if( b.getParcelable("image") != null  ) {
			imageBitmap = (Bitmap) b.getParcelable("image");
			ImageView imageView = ((ImageView) findViewById(R.id.loaded_image));
			imageView.setImageBitmap( imageBitmap );
		}
		if( b.getDouble("latitude") != 0.0 || b.getDouble("longitude") != 0.0 ) {
			lastLatitude = b.getDouble("latitude");
			lastLongitude = b.getDouble("longitude");
		}
	}
   

}
