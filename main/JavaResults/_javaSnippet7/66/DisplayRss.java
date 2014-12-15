package com.example.feedrssdisplay;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayRss extends ListActivity {
	private ArrayList<RSS> itemlist = null;
    private RSSListAdaptor rssadaptor = null;
    
    private String adr;
    private String tag1;
    private String tag2 ;
    private String tag3;
    private String tag4;
    
    private int[] boxes;
    Tags tags = new Tags();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_rss);
		// Show the Up button in the action bar.
		
		Intent intent = getIntent();
		adr = intent.getStringExtra(MainActivity.EXTRA_ADR);
		tag1 = intent.getStringExtra(MainActivity.EXTRA_TAG1);
		tag2 = intent.getStringExtra(MainActivity.EXTRA_TAG2);
		tag3 = intent.getStringExtra(MainActivity.EXTRA_TAG3);
		tag4 = intent.getStringExtra(MainActivity.EXTRA_TAG4);
		boxes = intent.getIntArrayExtra(MainActivity.EXTRA_CHECKBOX);
		
//		tags.setNameTag1(tag1); ------problemas para settar, se estiver ja definido na Tag, pode usar get
		tags.setNameTag2(tag2);
		tags.setNameTag3(tag3);
		tags.setNameTag4(tag4);
		
		 itemlist = new ArrayList<RSS>();

	        new RetrieveRSSFeeds().execute();
	}

	
	//setar as tags para o rss
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.display_rss, menu);
		return true;
		
	}
	
	
	 private void retrieveRSSFeed(String urlToRssFeed, ArrayList<RSS> list)
	    {
		
	        try
	        {
	            URL url = new URL(urlToRssFeed);
	            SAXParserFactory factory = SAXParserFactory.newInstance();
	            SAXParser parser = factory.newSAXParser();
	            XMLReader xmlreader = parser.getXMLReader();
	            ParserRSS theRssHandler = new ParserRSS(list);
	            
	           	    		
	    		String a = tags.getNameTag1();
	    		Log.i("taaag", a);
	    		
	    		theRssHandler.tagname(tags);

	            xmlreader.setContentHandler(theRssHandler);

	            InputSource is = new InputSource(url.openStream());

	            xmlreader.parse(is);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private Bitmap getBitmap(String url) {
		try{
			URL imageUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
			connection.setDoInput(true);
			connection.connect();
			
			InputStream input = connection.getInputStream();
			Bitmap image = BitmapFactory.decodeStream(input);
			input.close();
			return image;
		}
			catch(IOException ioe) {
		}
			return null;
	}
	
	  private class RetrieveRSSFeeds extends AsyncTask<Void, Void, Void>
	    {	
	        private ProgressDialog progress = null;

	        @Override
	        protected Void doInBackground(Void... params) {
	        	Log.i("doinback", adr);
	        	 // retrieveRSSFeed(adr,itemlist);  -  nao le o adr como link D:
	        	
	            retrieveRSSFeed("http://www.nasa.gov/rss/dyn/image_of_the_day.rss",itemlist);
	            rssadaptor = new RSSListAdaptor(DisplayRss.this, R.layout.activity_display_rss,itemlist);
	            return null;
	        }

	        @Override
	        protected void onCancelled() {
	            super.onCancelled();
	        }

	        @Override
	        protected void onPreExecute() {
	            progress = ProgressDialog.show(
	                    DisplayRss.this, null, "Loading RSS Feeds...");

	            super.onPreExecute();
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	            setListAdapter(rssadaptor);

	            progress.dismiss();

	            super.onPostExecute(result);
	        }

	        @Override
	        protected void onProgressUpdate(Void... values) {
	            super.onProgressUpdate(values);
	        }
	    }
	
	 private class RSSListAdaptor extends ArrayAdapter<RSS>{
	        private List<RSS> objects = null;

	        public RSSListAdaptor(Context context, int textviewid, List<RSS> objects) {
	            super(context, textviewid, objects);

	            this.objects = objects;
	        }

	        @Override
	        public int getCount() {
	            return ((null != objects) ? objects.size() : 0);
	        }

	        @Override
	        public long getItemId(int position) {
	            return position;
	        }

	        @Override
	        public RSS getItem(int position) {
	            return ((null != objects) ? objects.get(position) : null);
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	            View view = convertView;

	            if(null == view)
	            {
	                LayoutInflater vi = (LayoutInflater)DisplayRss.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                view = vi.inflate(R.layout.activity_display_rss, null);
	            }

	            RSS data = objects.get(position);

	            if(null != data)
	            {
	                TextView tag1 = (TextView)view.findViewById(R.id.txtTag1);
	                TextView tag2 = (TextView)view.findViewById(R.id.txtTag2);
	                TextView tag3 = (TextView)view.findViewById(R.id.txtTag3);
	                TextView tag4 = (TextView)view.findViewById(R.id.txtTag4);
	                
	                Log.i("data tag", data.tag1);
	                
	                tag1.setText(data.tag1);
	                
	                tag2.setText(data.tag2);
	                tag3.setText(data.tag3);
	                tag4.setText(data.tag4);
	            }

	            return view;
	        }
	    }	
	
	
}
