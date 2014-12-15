/*
 * Copyright (C) 2012 The Android Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.pencilsofpromise.rss;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pencilsofpromise.R;
import org.pencilsofpromise.ShowDescription;

import org.pencilsofpromise.rss.RSSFragment.OnOsmdroidMenuListener;
import org.pencilsofpromise.rssstorage.RSSnote;
import org.pencilsofpromise.rssstorage.RSSprovider;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RSSFragment extends ListFragment implements OnItemClickListener,LoaderManager.LoaderCallbacks<Cursor>{
public final String RSSFEEDOFCHOICE = "http://www.pencilsofpromise.org/feed";
	
	public final String tag = "RSSReader";
	private RSSFeed feed = null;
	OnOsmdroidMenuListener mListener;
	private SimpleCursorAdapter adapter;
	private List<RSSItem> _itemlist;
	public RSSItem item;
	private static final int RSS_LIST_LOADER = 0x01;
	public static int sequence_id=0;
	boolean InternetState ;
	
	public void setRSSFragment(boolean i)
	{
		InternetState = i;
	}
	
	
	
	 public interface OnOsmdroidMenuListener {
	        public void osmdroidCitySelected(String option);
	    }
	 @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
	
		        
		 View v =  inflater.inflate(R.layout.rss_fragment, container, false);
//		 	feed=getFeed(RSSFEEDOFCHOICE);
		
		 if (InternetState)
		 {
		       DownloadFeedTask task=new DownloadFeedTask();
		       task.execute(null,null,null);
		 }
		        
		 try
		 {
			 
		     	TextView title= (TextView) v.findViewById(R.id.title);
		     	TextView pubdate= (TextView) v.findViewById(R.id.pub_date);
		     	TextView description= (TextView) v.findViewById(R.id.description);
		     
		       String[] uiBindFrom = { RSSnote.RSS_tab.COL_TITLE,RSSnote.RSS_tab.COL_DESCRIPTION,RSSnote.RSS_tab.COL_PUB_DATE,RSSnote.RSS_tab.COL_IMAGE};
		        int[] uiBindTo = { R.id.title, R.id.description,R.id.pub_date,R.id.imageview0};
		       

		        getLoaderManager().initLoader(RSS_LIST_LOADER, null, this);
		        ListView itemlist = (ListView) v.findViewById(android.R.id.list);
		        adapter = new SimpleCursorAdapter(
		                getActivity().getApplicationContext(), R.layout.list_xml,
		                null, uiBindFrom, uiBindTo,
		                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		       
		      adapter.setViewBinder(new ProductViewBinder());
		       
		       itemlist.setAdapter(adapter);
		        itemlist.setOnItemClickListener(this);
		        itemlist.setSelection(0); 
		 }
		 catch (Exception e)
		 {
			 Toast.makeText(getActivity().getApplicationContext(), "No internet, first time load", Toast.LENGTH_LONG).show();
		 }
		      
		        		
			return v;
		}
	 
		   
	    
	    
	 	
	 private class DownloadFeedTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... arg0) {
				
				feed=getFeed(RSSFEEDOFCHOICE);
				 
				 
				return null;
			}
	 }
	 
	 
	 public class ProductViewBinder implements ViewBinder 
	 {
	     public boolean setViewValue(View view, Cursor cursor, int columnIndex)
	     {
	         try
	         {
	             if (view instanceof ImageView) 
	             {
	                
	            	
	            	 
	            	byte[] url=cursor.getBlob(4);
	            	
	            	
	            
	            	
	            	Bitmap theImage = BitmapFactory.decodeByteArray(url, 0, url.length);
	            	ImageView imagev =(ImageView)view.findViewById(R.id.imageview0);
	                imagev.setImageBitmap(theImage);
	            	
	            	
	                return true;
	             }

	         }
	         catch(Exception e)
	         {
	             
	         }
	         return false;
	     }
	 }
	
	 	
	
public RSSFeed getFeed(String urlToRssFeed)
{
	try
	{
		// setup the url
	   URL url = new URL(urlToRssFeed);

       // create the factory
       SAXParserFactory factory = SAXParserFactory.newInstance();
       // create a parser
       SAXParser parser = factory.newSAXParser();

       // create the reader (scanner)
       XMLReader xmlreader = parser.getXMLReader();
       // instantiate our handler
       RSSHandler theRssHandler = new RSSHandler(getActivity().getApplicationContext());
       // assign our handler
       xmlreader.setContentHandler(theRssHandler);
       // get our data via the url class
       InputSource is = new InputSource(url.openStream());
       // perform the synchronous parse           
       xmlreader.parse(is);
       // get the results - should be a fully populated RSSFeed instance, or null on error
       return theRssHandler.getFeed();
	}
	catch (Exception ee)
	{
		// if we have a problem, simply return null
		return null;
	}
}
public boolean onCreateOptionsMenu(Menu menu) 
{
	super.onCreateOptionsMenu(menu, null);
	
	menu.add(0,0,0, "Choose RSS Feed");
	menu.add(0, 1, 1, "Refresh");
	
	return true;
	
}

public boolean onOptionsItemSelected(MenuItem item){
    switch (item.getItemId()) {
    case 0:
    	
        return true;
    case 1:
 
        return true;
    }
    return false;
}





 public void onItemClick(AdapterView parent, View v, int position, long id)
 {
	

	 Intent itemintent = new Intent(getActivity(),ShowDescription.class);
     ContentValues values=new ContentValues();
	 Bundle b = new Bundle();
	 b.putString("title", feed.getItem(position).getTitle());
	 b.putString("description", feed.getItem(position).getDescription());
	 b.putString("content", feed.getItem(position).getContent());
	 b.putString("link", feed.getItem(position).getLink());
	 b.putString("pubdate", feed.getItem(position).getPubDate().toString());
	

	 
	 
	 
	 itemintent.putExtra("android.intent.extra.INTENT", b);
     
     startActivityForResult(itemintent,0);
 }
 
 @Override
 public void onListItemClick(ListView l, View v, int position, long id) {
    String projection[] = { RSSnote.RSS_tab.COL_TITLE,RSSnote.RSS_tab.COL_LINK, RSSnote.RSS_tab.COL_PUB_DATE,RSSnote.RSS_tab.COL_DESCRIPTION,RSSnote.RSS_tab.COL_CONTENT};
     Cursor tutorialCursor = getActivity().getContentResolver().query(
             Uri.withAppendedPath(RSSprovider.CONTENT_URI,
                     String.valueOf(id)), projection, null, null, null);
     Intent itemintent = new Intent(getActivity(),ShowDescription.class);
     
	 Bundle b = new Bundle();
	 if (tutorialCursor.moveToFirst()) {
	
	 b.putString("title", tutorialCursor.getString(0));
	 b.putString("description",tutorialCursor.getString(3) );
	 b.putString("content", tutorialCursor.getString(4));
	 b.putString("link", tutorialCursor.getString(1));
	 b.putString("pubdate", tutorialCursor.getString(2));
     
	 }
   
 itemintent.putExtra("android.intent.extra.INTENT", b);
     
     startActivityForResult(itemintent,0);
     tutorialCursor.close();
	
 }

 
 public Bitmap getBitmap(String bitmapUrl) {
	  try {
	    URL url = new URL(bitmapUrl);
	    return BitmapFactory.decodeStream(url.openConnection().getInputStream()); 
	  }
	  catch(Exception ex) {return null;}
	}
 
 
 /*returns the String with html contents removed from it */
 private Spanned removeContentSpanObjects(String sb) {
		SpannableStringBuilder spannedStr = (SpannableStringBuilder)Html.fromHtml(sb.toString().trim());
		Object[] spannedObjects = spannedStr.getSpans(0,spannedStr.length(),Object.class);
		for (int i = 0; i < spannedObjects.length; i++) {
		
			if (spannedObjects[i] instanceof ImageSpan)
				spannedStr.replace(spannedStr.getSpanStart(spannedObjects[i]), spannedStr.getSpanEnd(spannedObjects[i]), "");
				
		}	
		return spannedStr;
	}
 

@Override
public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	String[] projection = { RSSnote.RSS_tab.ID, RSSnote.RSS_tab.COL_TITLE,RSSnote.RSS_tab.COL_PUB_DATE,RSSnote.RSS_tab.COL_DESCRIPTION,RSSnote.RSS_tab.COL_IMAGE};

    CursorLoader cursorLoader = new CursorLoader(getActivity(),
            RSSprovider.CONTENT_URI, projection, null, null, RSSnote.RSS_tab.COL_STORE_DATE + " DESC");
    return cursorLoader;
}

@Override
public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
	adapter.swapCursor(cursor);
	
}

@Override
public void onLoaderReset(Loader<Cursor> loader) {
	adapter.swapCursor(null);
	
}


	
}

