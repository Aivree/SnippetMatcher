package com.maltesetraffic;

import org.json.JSONArray;
import org.json.JSONObject;

import com.maltesetraffic.models.TrafficReport;
import com.maltesetraffic.utils.ConnectionChecker;
import com.maltesetraffic.utils.HTTP;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.bugsense.trace.BugSenseHandler;
//import com.google.ads.AdRequest;
//import com.google.ads.AdView;


public class MainActivity extends ApplicationMT {

	
	//set up a broadcast receiver to listen to location changes from the background service
	LocationChangeReceiver lcr = new LocationChangeReceiver();
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        System.out.println("Called MainActivity onCreate");
        
        BugSenseHandler.initAndStartSession(MainActivity.this, "90dd1666");
        
        setContentView(R.layout.main);
        
/*        // admob widget
        adView = (AdView)findViewById(R.id.ad);
        adView.loadAd(new AdRequest());*/

        actionBar.show();
        // listener for action bar navigation menu
        ActionBar.OnNavigationListener mNavigationCallback = new ActionBar.OnNavigationListener() {

            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                if (actions[itemPosition].equals("Closest")) {


                    distanceSort = "yes";
                    RefreshTask task = new RefreshTask();
                    task.execute("params");
                }
                if (actions[itemPosition].equals("Newest")) {


                    distanceSort = "no";
                    RefreshTask task = new RefreshTask();
                    task.execute("params");
                }

                return false;
            }

        };

        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mNavigationCallback);

        checkFirstRun();
        
    }


	private void checkFirstRun() {

	      //show dialog on first run
        boolean firstrun = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getBoolean("firstrun", true);
        
        if (firstrun){
        //display the dialog
		ContextThemeWrapper wrapper = new ContextThemeWrapper(MainActivity.this, R.style.AppTheme);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(wrapper);
			 
			// set title
			alertDialogBuilder.setTitle("Welcome");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Hi! Malta Traffic is a community based and location-aware service. The more accurate your traffic reports, the better the service will be. Please remember to turn on your GPS for better location accuracy."+ "\n" + "\n" + "Do you want to open your GPS settings now?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if the yes button is clicked, open the GPS settings
						startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
        	
        //save the state
        getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE)
            .edit()
            .putBoolean("firstrun", false)
            .commit();
        }
        
	}


	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(lcr);
        
	}
	


	@Override
	protected void onResume() {
		super.onResume();
        registerReceiver(lcr, new IntentFilter("NEW_LOCATION"));
        
        System.out.println("Called MainActivity onResume");

	}

	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		
        String origin = "";
        
        Bundle extras = getIntent().getExtras();
        
        if (extras != null){
        origin = extras.getString("origin");
        }
        
        System.out.println("The origin in onNewIntent is: "+origin);
        
        if (origin.equals("aftersubmit")&& mRefresh != null ){     
        	trafficJams.clear();
        	RefreshTask task = new RefreshTask();
        	task.execute("params");
       }
	}


	// receives callback with the ID of the navigation menu item selected
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

        // app icon in action bar clicked; restart this activity
        if (item.getItemId() == android.R.id.home) {
        	/*            
        	Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            */
        }

        // refresh button is clicked; refresh list
        if (item.getItemId() == R.id.menu_refresh) {
            RefreshTask task = new RefreshTask();
            task.execute("params");
        }
        
        // add button is clicked; start the SubmitActivity
        if (item.getItemId() == R.id.menu_add) {
        	if (!ConnectionChecker.check(MainActivity.this)) {
				Toast.makeText(MainActivity.this,"No internet connection!", Toast.LENGTH_LONG).show();
			}
    		else {
		    		intentSubmit = new Intent(getApplicationContext(),SubmitActivity.class);
		    		startActivity(intentSubmit);
		    	
    		}
        }
                       
        return super.onOptionsItemSelected(item);
    }

    
    
     private class RefreshTask extends AsyncTask<String, TrafficReport, String> {

        @Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if (!ConnectionChecker.check(MainActivity.this)) {
				Toast.makeText(MainActivity.this,"No internet connection!", Toast.LENGTH_LONG).show();
			}
			
			else {
				
	            // clear the trafficjams array
	            trafficJams.clear();
				
	            // Apply the animation to our View
	            refreshView.startAnimation(rotateClockwise);
	            
	            // Apply the View to our MenuItem
	            mRefresh.setActionView(refreshView);		
			
			}

		}

		@Override
        protected String doInBackground(String... params) {			
			 
            String json = HTTP.getHTTPurl(Constants.ENDPOINT_trafficjams  + "?lat="+currentLat+"&lon="+currentLon+"&distancesort=" + distanceSort);
            
            System.out.println("JSON response: " + json);

			    	// Instantiate a JSON array from the request response
			    	try {
		            JSONArray the_json_array = new JSONArray(json);

		            // Get a list of traffic reports
		            for (int i = 0; i < the_json_array.length(); i++) {

		                JSONObject childJSONObject = the_json_array.getJSONObject(i);

		                int reportID = childJSONObject.getInt("reportID");
		                int userID = childJSONObject.getInt("userID");
		                double lat = childJSONObject.getDouble("lat");
		                double lon = childJSONObject.getDouble("lon");
		                String description = childJSONObject.getString("description");
		                String location = childJSONObject.getString("location");
		                String timeStamp = childJSONObject.getString("timeStamp");
		                int distance = (int) Math.floor(childJSONObject.getDouble("distance"))+1;
		                String userName = childJSONObject.getString("userName");
		                String fbPostLink= childJSONObject.getString("fbPostLink");

		                // Construct TrafficJam object
		                final TrafficReport trafficjam = new TrafficReport(reportID, userID, lat, lon,
		                        description, location, timeStamp, distance, userName, fbPostLink);
		                
		                // For every trafficjam object, add it to the list via onProgressUpdate (which runs on the UI thread)
		                publishProgress(trafficjam);
		            }
			    	
		            } catch (Exception e) {
			            e.printStackTrace();
			        }

			
            return null;
        }
		

        @Override
		protected void onProgressUpdate(TrafficReport... reports) {
			super.onProgressUpdate(reports);
			// add the TrafficReport object to the arraylist
        	trafficJams.add(reports[0]);
		}
        

		@Override
        protected void onPostExecute(String result) {
			aa.notifyDataSetChanged();
            refreshView.clearAnimation();
            mRefresh.setActionView(null);

        }
    };

    
    
    // a BroadCast receiver that receives a broadcast when the location changes in the LocationBackgroundService
	private class LocationChangeReceiver extends BroadcastReceiver 
	{
	@Override
	   public void onReceive(Context context, Intent intent) 
	   {    
	    String action = intent.getAction();
	    
	       
	    if(action.equalsIgnoreCase("NEW_LOCATION")){    
	          Bundle extra = intent.getExtras();
	          currentLat = extra.getDouble("lat");   
	          currentLon = extra.getDouble("lon");
				   
	      }
	    
	    
	    
	   }
	}
	
	
	@Override
	//click listener for the traffic reports listview
	//get the report ID and open the activity to show the report details
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    super.onListItemClick(l, v, position, id);
	    TrafficReport o = (TrafficReport) this.getListAdapter().getItem(position);
	    int reportID = o.getReportID();
	    String userName = o.getUserName();
	    double lat = o.getLat();
	    double lon = o.getLon();
	    String location = o.getLocation();
	    String timeStamp = o.getTimeStamp();
	    String description = o.getDescription();
	    String fbPostLink = o.getFbPostLink();
	    
    
		intentViewReport = new Intent(getApplicationContext(),ViewReportActivity.class);
		intentViewReport.putExtra("reportID", reportID);
		intentViewReport.putExtra("userName", userName);
		intentViewReport.putExtra("lat", lat);
		intentViewReport.putExtra("lon", lon);
		intentViewReport.putExtra("location", location);
		intentViewReport.putExtra("timeStamp", timeStamp);
		intentViewReport.putExtra("description", description);
		intentViewReport.putExtra("fbPostLink", fbPostLink);
		
    	if (!ConnectionChecker.check(MainActivity.this)) {
			Toast.makeText(MainActivity.this,"No internet connection!", Toast.LENGTH_LONG).show();
		}
    	else {
		startActivity(intentViewReport);
		
    	}
	
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  // Save UI state changes to the savedInstanceState.
	  // This bundle will be passed to onCreate if the process is killed and restarted.
	  super.onSaveInstanceState(savedInstanceState);
	  
		
	  savedInstanceState.putDouble("currentLat", currentLat);
	  savedInstanceState.putDouble("currentLon", currentLon);
	  savedInstanceState.putFloat("accuracy", accuracy);
	  savedInstanceState.putInt("userID", userID);
	  savedInstanceState.putString("distanceSort", distanceSort);
  
	  
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  

	  
	  // Restore state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.
	  
	  currentLat = gs.getLatestLocation().getLatitude();
	  currentLon = gs.getLatestLocation().getLongitude();
 
	  accuracy = savedInstanceState.getFloat("accuracy");
	  userID = savedInstanceState.getInt("userID");
	  distanceSort = savedInstanceState.getString("distanceSort");
	  
	  if (distanceSort.equals("yes")){
		  actionBar.setSelectedNavigationItem(1);
	  }
	  else {
		  actionBar.setSelectedNavigationItem(0);
	  }
	  
	  	  
	}




	@Override
	protected void onDestroy() {
		super.onDestroy();
		//adView.destroy();

	}

}
