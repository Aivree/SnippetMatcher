package com.placepinner.android;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import org.json.*;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import com.placepinner.android.MainActivity;
import com.placepinner.android.Place;
import com.placepinner.android.MyApp;

import android.location.Location;
import android.location.LocationManager;

public class RetrievePlaces extends AsyncTask<String, Void, String> {
    private Exception exception;
    private MainActivity activity;

    public RetrievePlaces(MainActivity a){
    	this.activity = a;
    }
    
    protected String doInBackground(String... urls) {
    	InputStream stream;
    	
        try {
            URL url = new URL(urls[0]);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(5000);
            stream = urlConnection.getInputStream();
        } catch (Exception ex) {
            Log.i ("Exception!", ex.toString() );
            Log.i ("info", ex.toString() );
            return null;
        }

    	String str = "";
    	
        try{
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        byte[] buffer = new byte[1024];
	        int length = 0;
	        while ((length = stream.read(buffer)) != -1) {
	            baos.write(buffer, 0, length);
	        }
	        str = new String(baos.toByteArray());
    	} catch (Exception ex) {
    		Log.i ("Exception!", ex.toString() );
    	}

        return str; 
    }

//    protected RSSFeed doInBackground(String... urls) {
//        try {
//            URL url= new URL(urls[0]);
//            SAXParserFactory factory =SAXParserFactory.newInstance();
//            SAXParser parser=factory.newSAXParser();
//            XMLReader xmlreader=parser.getXMLReader();
//            RssHandler theRSSHandler=new RssHandler();
//            xmlreader.setContentHandler(theRSSHandler);
//            InputSource is=new InputSource(url.openStream());
//            xmlreader.parse(is);
//            return theRSSHandler.getFeed();
//        } catch (Exception e) {
//            this.exception = e;
//            return null;
//        }
//    }

    protected void onPostExecute(String output) {
    	Log.i ("info", "HTTP Fetch complete");
        // Log.i ("info", output);

        try{
        	JSONArray jArray = new JSONArray(output);

        	for (int i=0; i < jArray.length(); i++){
	        	Place place = new Place(jArray.getJSONObject(i));
	        	place.save();
	        }
        }catch(JSONException e){
        	Log.i ("info", "Error parsing json");
    		Log.d ("Exception!", e.toString() );
        }
        
        activity.progressDialog.dismiss();

        // Populate the places list
        MyApp app = (MyApp)activity.getApplicationContext();
        // app.resetPlaces(places);
        
    	List<Place> places = new Place().all();

        final Location home = new Location(LocationManager.NETWORK_PROVIDER);
        home.setLatitude(-41.22852);
        home.setLongitude(174.88207);
        
        Collections.sort(places, new Comparator<Place>() {
            public int compare(Place a, Place b) {
                return Integer.signum(distance(a) - distance(b));
            }
            
            private Integer distance(Place a){
            	if(a.hasGeometry()){
            		return Math.round(a.getLocation().distanceTo(home));
            	}else{
            		return 99999999;
            	}
            }
        });

        activity.populatePlaces(
        	places.toArray(new Place[places.size()])
        );

        //        countries.toArray(
//        	new String[countries.size()]
//        ));
        
        // TODO: check this.exception 
        // TODO: do something with the feed
    }
}
