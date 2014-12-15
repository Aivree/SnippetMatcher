package com.music.thefix;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BlogFeedActivity extends ListActivity {


	private static String url = "http://www.youneedthefix.com/feed/";
	List<RSSItem> rssitems;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);       

		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			//online mode			
			if (savedInstanceState == null){	
				rssitems = new ArrayList<RSSItem>();			
				new Task().execute();
				
				ListView lv = getListView();
				lv.setDivider(null);
			}
			else{
				rssitems = savedInstanceState.getParcelableArrayList("rssitems");
				RSSEntryAdapter adapter = new RSSEntryAdapter(this,R.layout.activity_blog_feed_item,rssitems);
				setListAdapter(adapter);
			}
		}else {
			//offline mode
			new AlertDialog.Builder(this)
			.setTitle(Html.fromHtml("<font color='#79FCE7'>Server is temporarily unavailable</font>"))
			.setMessage(Html.fromHtml("Check your network connection and try again"))
			.setNegativeButton("Back", new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			})
			.show();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private class Task extends AsyncTask<Void, Integer, Void> {  

		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);		
		}

		@Override  
		protected void onPostExecute(Void result) {  	
			setListAdapter(new RSSEntryAdapter(BlogFeedActivity.this, R.layout.activity_blog_feed_item, rssitems));
		}		


		@Override
		protected Void doInBackground(Void... arg0) {
			RSSFeed myRssFeed = new RSSFeed();

			try{
				URL rssUrl = new URL(url);
				SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
				SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
				XMLReader myXMLReader = mySAXParser.getXMLReader();
				RSSHandler myRSSHandler = new RSSHandler();
				myXMLReader.setContentHandler(myRSSHandler);
				InputSource myInputSource = new InputSource(rssUrl.openStream());
				myXMLReader.parse(myInputSource);

				myRssFeed = myRSSHandler.getFeed();
				if (myRssFeed != null){
					List<RSSItem> all = new ArrayList<RSSItem>();
					all = myRssFeed.getList();
					for (int i=0; i < all.size(); i++ ){

						RSSItem cur = all.get(i);
						RSSItem curItem = new RSSItem();

						curItem.description = cur.getDescription();;
						curItem.title = cur.getTitle();
						curItem.pubdate = cur.getPubdate().substring(0,16);
						curItem.link = cur.getLink();

						rssitems.add(curItem);
						publishProgress(i);

					}
				}
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return null;
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri feedUri = Uri.parse(rssitems.get(position).getLink());
		  Intent myIntent = new Intent(Intent.ACTION_VIEW, feedUri);
		  startActivity(myIntent);
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

	private class RSSEntryAdapter extends ArrayAdapter<RSSItem>{
		private List<RSSItem> items;
		private LayoutInflater vi;

		public RSSEntryAdapter(Context context,int textViewResourceId,List<RSSItem> items) {  
			super(context, R.layout.activity_blog_feed_item, items);
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			this.items = items;
		}

		private class ViewHolder{
			TextViewPlus title;
			TextView description;
			TextView pubDate;
			TextView link;
		}

		@Override  
		public View getView(int position, View convertView, ViewGroup parent) {  
			ViewHolder holder;			
			if (convertView == null) {  
				convertView = vi.inflate(R.layout.activity_blog_feed_item, null);
				holder = new ViewHolder();
				holder.title = (TextViewPlus)convertView.findViewById(R.id.itemTitle);
				holder.description = (TextView)convertView.findViewById(R.id.itemDescription);
				holder.pubDate = (TextView)convertView.findViewById(R.id.itemPubDate);
				holder.link = (TextView)convertView.findViewById(R.id.itemLink);
				convertView.setTag(holder);

			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			RSSItem rssitem = items.get(position);
			holder.title.setText(Html.fromHtml(rssitem.getTitle()));			
			holder.description.setText(Html.fromHtml(rssitem.getDescription()));			
			holder.pubDate.setText(Html.fromHtml(rssitem.getPubdate()));
			holder.link.setText(Html.fromHtml(rssitem.getLink()));
			return convertView;  
		}
	}
}



