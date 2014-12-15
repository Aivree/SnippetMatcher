package com.example.rssreader;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rssreader.ExternalComm.RSSFeedIBM;
import com.example.rssreader.ExternalComm.RSSHandlerIBM;
import com.example.rssreader.ExternalComm.RSSItemIBM;

public class MainActivity extends Activity implements OnItemClickListener {

	public final String RSSFEEDOFCHOICE = "http://www.ibm.com/developerworks/views/rss/customrssatom.jsp?zone_by=XML&zone_by=Java&zone_by=Rational&zone_by=Linux&zone_by=Open+source&zone_by=WebSphere&type_by=Tutorials&search_by=&day=1&month=06&year=2007&max_entries=20&feed_by=rss&isGUI=true&Submit.x=48&Submit.y=14";
	public final String tag = "RSSReader";
	private RSSFeedIBM feed = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		RetreiveFeedTask  task = new RetreiveFeedTask();
		task.execute(RSSFEEDOFCHOICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    private void UpdateDisplay()
    {
        TextView feedtitle = (TextView) findViewById(R.id.feedtitle);
        TextView feedpubdate = (TextView) findViewById(R.id.feedpubdate);
        ListView itemlist = (ListView) findViewById(R.id.itemlist);
  
        if (feed == null)
        {
        	feedtitle.setText("No RSS Feed Available");
        	return;
        }
        
        feedtitle.setText(feed.getTitle());
        feedpubdate.setText(feed.getPubDate());

		ArrayAdapter<RSSItemIBM> adapter = new ArrayAdapter<RSSItemIBM>(this,android.R.layout.simple_list_item_1,feed.getAllItems());

        itemlist.setAdapter(adapter);
        
        itemlist.setOnItemClickListener(this);
        
        itemlist.setSelection(0); 
        
    }
    
    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    {
	   	 Log.i(tag,"item clicked! [" + feed.getItem(position).getTitle() + "]");
	
	   	 Intent itemintent = new Intent(this,ShowDescription.class);
	        
	   	 Bundle b = new Bundle();
	   	 b.putString("title", feed.getItem(position).getTitle());
	   	 b.putString("description", feed.getItem(position).getDescription());
	   	 b.putString("link", feed.getItem(position).getLink());
	   	 b.putString("pubdate", feed.getItem(position).getPubDate());
	   	 
	   	 itemintent.putExtra("android.intent.extra.INTENT", b);
	        
	   	 startActivityForResult(itemintent,0);
    }
    
	private class RetreiveFeedTask extends AsyncTask<String, Void, RSSFeedIBM> {
		
	    ProgressDialog dialog;
	    
	    @Override
	    protected void onPreExecute() {
	        dialog = ProgressDialog.show(MainActivity.this, "", "Loading RSS Feed....");
	    }
	    
		@Override
		protected RSSFeedIBM doInBackground(String... params) {

			RSSFeedIBM values = null;
	    	try
	    	{
	    		// setup the url
	    	   URL url = new URL(params[0]);

	           // create the factory
	           SAXParserFactory factory = SAXParserFactory.newInstance();
	           // create a parser
	           SAXParser parser = factory.newSAXParser();

	           // create the reader (scanner)
	           XMLReader xmlreader = parser.getXMLReader();
	           // instantiate our handler
	           RSSHandlerIBM theRssHandler = new RSSHandlerIBM();
	           // assign our handler
	           xmlreader.setContentHandler(theRssHandler);
	           // get our data via the url class
	           InputSource is = new InputSource(url.openStream());
	           // perform the synchronous parse           
	           xmlreader.parse(is);
	           // get the results - should be a fully populated RSSFeed instance, or null on error
	           
	           values = theRssHandler.getFeed();
	    	}
	    	catch (Exception ee)
	    	{
	    		// if we have a problem, simply return null
	    		//return Void;
	    	}
	    	return values;
		}
		
	    @Override
	    protected void onPostExecute(RSSFeedIBM result) {
	    	dialog.dismiss();
	    	feed = result;
	    	UpdateDisplay();
	    }
	}
}
