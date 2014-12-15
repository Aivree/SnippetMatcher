package com.android.electricsheep.townportal;

// Stuart Filis - Saturday Night Special

// Source code found at http://www.ibm.com/developerworks/opensource/tutorials/os-eclipse-android/
// used for creation of RSS Reader application

/*
 * Brian Hague - Source for AsyncTask classes from Google: http://developer.android.com/training/articles/perf-anr.html
 * (used more as inspiration than anything)
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import android.view.*;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener; 

import com.android.electricsheep.townportal.ShowDescription;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;



public class RSSReader extends Activity implements OnItemClickListener
{

	public final String tag = "RSSReader";
    public String rssFeedOfChoice = null;
    private RSSFeed feed = null;
	
	/** Called when the activity is first created. */

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_rss_reader);
        
        Intent startingIntent = getIntent();
        
        if (startingIntent != null)
        {
        	Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
        	if (b == null)
        	{
        		rssFeedOfChoice = "bad bundle?";
        	}
        	else
    		{
        		rssFeedOfChoice = b.getString("url");
    		}
        }
        else
        {
        	rssFeedOfChoice = "Information Not Found.";
        }
        
        // go get our feed!
        feed = getFeed(rssFeedOfChoice);

        // display UI
        UpdateDisplay();
        
    }

    
    private RSSFeed getFeed(String urlToRssFeed)
    {
    	try
    	{
    		// setup the url
    	   URL url = new URL(urlToRssFeed);

           // get our data via the url class
           InputSource is = new DownloadURLTask().execute(url).get();
           // perform the synchronous parse
           return new ParseTask().execute(is).get();
    	}
    	catch (Exception ee)
    	{
    		// if we have a problem, simply return null
    		return null;
    	}
    }
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0,Menu.NONE,0,"Choose RSS Feed");
    	menu.add(0,Menu.NONE,1,"Refresh");
    	Log.i(tag,"onCreateOptionsMenu");
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
        case 0:
        	
        	Log.i(tag,"Set RSS Feed");
            return true;
        case 1:
        	Log.i(tag,"Refreshing RSS Feed");
            return true;
        }
        return false;
    }
    
    
    private void UpdateDisplay()
    {
        TextView feedtitle = (TextView) findViewById(R.id.feedtitle);
        TextView feedpubdate = (TextView) findViewById(R.id.feedpubdate);
        ListView itemlist = (ListView) findViewById(R.id.itemlist);
  
        
        if (feed == null)
        {
        	feedtitle.setText("No RSS Feed Available\n" + rssFeedOfChoice);
        	return;
        }
        
        feedtitle.setText(feed.getTitle());
        feedpubdate.setText(feed.getPubDate());

        ArrayAdapter<RSSItem> adapter = new ArrayAdapter<RSSItem>(this,android.R.layout.simple_list_item_1,feed.getAllItems());

        itemlist.setAdapter(adapter);
        
        itemlist.setOnItemClickListener(this);
        
        itemlist.setSelection(0);
        
    }
    
    
     public void onItemClick(AdapterView parent, View v, int position, long id)
     {
    	 Log.i(tag,"item clicked! [" + feed.getItem(position).getTitle() + "]");

    	 Intent itemintent = new Intent(this,ShowDescription.class);
         
    	 Bundle b = new Bundle();
    	 b.putString("title", feed.getItem(position).getTitle());
    	 b.putString("description", feed.getItem(position).getDescription());
    	 b.putString("link", feed.getItem(position).getLink());
    	 b.putString("pubdate", feed.getItem(position).getPubDate());
    	 
    	 itemintent.putExtra("android.intent.extra.INTENT", b);
         
         startActivity(itemintent);
     }
} // end class RSSReader

class DownloadURLTask extends AsyncTask<URL, Integer, InputSource> {
    // Do the long-running work in here
    protected InputSource doInBackground(URL... urls) {
        if (urls.length == 1)
        {
        	try
        	{
        		InputSource is = new InputSource(urls[0].openStream());
        		return is;
        	}
        	catch (Exception ee)
        	{
        		// if we have a problem, simply return null
        		return null;
        	}
        }
		return null;
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(InputSource result) {
    	
    }
}

class ParseTask extends AsyncTask<InputSource, Integer, RSSFeed> {
    // Do the long-running work in here
    protected RSSFeed doInBackground(InputSource... sources) {
        if (sources.length == 1)
        {
        	try
        	{
        		// create the factory
                SAXParserFactory factory = SAXParserFactory.newInstance();
                // create a parser
                SAXParser parser = factory.newSAXParser();
        		// create the reader (scanner)
                XMLReader xmlreader = parser.getXMLReader();
                // instantiate our handler
                RSSHandler theRssHandler = new RSSHandler();
                // assign our handler
                xmlreader.setContentHandler(theRssHandler);
                xmlreader.parse(sources[0]);
                // get the results - should be a fully populated RSSFeed instance, or null on error
                return theRssHandler.getFeed();
        	}
        	catch (Exception ee)
        	{
        		// if we have a problem, simply return null
        		return null;
        	}
        }
		return null;
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(RSSFeed result) {
    	
    }
}


class RSSHandler extends DefaultHandler 
{
	
	RSSFeed _feed;
	RSSItem _item;
	String _lastElementName = "";
	boolean bFoundChannel = false;
	final int RSS_TITLE = 1;
	final int RSS_LINK = 2;
	final int RSS_DESCRIPTION = 3;
	final int RSS_CATEGORY = 4;
	final int RSS_PUBDATE = 5;
	
	int depth = 0;
	int currentstate = 0;
	/*
	 * Constructor 
	 */
	RSSHandler()
	{
	}
	
	/*
	 * getFeed - this returns our feed when all of the parsing is complete
	 */
	RSSFeed getFeed()
	{
		return _feed;
	}
	
	
	public void startDocument() throws SAXException
	{
		// initialize our RSSFeed object - this will hold our parsed contents
		_feed = new RSSFeed();
		// initialize the RSSItem object - we will use this as a crutch 
		// to grab the info from the channel
		// because the channel and items have very similar entries..
		_item = new RSSItem();

	}
	public void endDocument() throws SAXException
	{
	}
	public void startElement(String namespaceURI, String localName,String qName, 
											Attributes atts) throws SAXException
	{
		depth++;
		if (localName.equals("channel"))
		{
			currentstate = 0;
			return;
		}
		if (localName.equals("image"))
		{
			// record our feed data - we temporarily stored it in the item :)
			_feed.setTitle(_item.getTitle());
			_feed.setPubDate(_item.getPubDate());
		}
		if (localName.equals("item"))
		{
			// create a new item
			_item = new RSSItem();
			return;
		}
		if (localName.equals("title"))
		{
			currentstate = RSS_TITLE;
			return;
		}
		if (localName.equals("description"))
		{
			currentstate = RSS_DESCRIPTION;
			return;
		}
		if (localName.equals("link"))
		{
			currentstate = RSS_LINK;
			return;
		}
		if (localName.equals("category"))
		{
			currentstate = RSS_CATEGORY;
			return;
		}
		if (localName.equals("pubDate"))
		{
			currentstate = RSS_PUBDATE;
			return;
		}
		// if we don't explicitly handle the element, make sure we don't wind up erroneously 
		// storing a newline or other bogus data into one of our existing elements
		currentstate = 0;
	}
	
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		depth--;
		if (localName.equals("item"))
		{
			// add our item to the list!
			_feed.addItem(_item);
			return;
		}
	}
	 
	public void characters(char ch[], int start, int length)
	{
		String theString = new String(ch,start,length);
		Log.i("RSSReader","characters[" + theString + "]");
		
		switch (currentstate)
		{
			case RSS_TITLE:
				_item.setTitle(theString);
				currentstate = 0;
				break;
			case RSS_LINK:
				_item.setLink(theString);
				currentstate = 0;
				break;
			case RSS_DESCRIPTION:
				_item.setDescription(theString);
				currentstate = 0;
				break;
			case RSS_CATEGORY:
				_item.setCategory(theString);
				currentstate = 0;
				break;
			case RSS_PUBDATE:
				_item.setPubDate(theString);
				currentstate = 0;
				break;
			default:
				return;
		}
	}
} // end class RSSHandler


class RSSFeed 
{
	private String _title = null;
	private String _pubdate = null;
	private int _itemcount = 0;
	private List<RSSItem> _itemlist;
	
	
	RSSFeed()
	{
		_itemlist = new Vector<RSSItem>(0); 
	}
	int addItem(RSSItem item)
	{
		_itemlist.add(item);
		_itemcount++;
		return _itemcount;
	}
	RSSItem getItem(int location)
	{
		return _itemlist.get(location);
	}
	List<RSSItem> getAllItems()
	{
		return _itemlist;
	}
	int getItemCount()
	{
		return _itemcount;
	}
	void setTitle(String title)
	{
		_title = title;
	}
	void setPubDate(String pubdate)
	{
		_pubdate = pubdate;
	}
	String getTitle()
	{
		return _title;
	}
	String getPubDate()
	{
		return _pubdate;
	}	
} // end class RSSFeed


class RSSItem 
{
	private String _title = null;
	private String _description = null;
	private String _link = null;
	private String _category = null;
	private String _pubdate = null;

	
	RSSItem()
	{
	}
	void setTitle(String title)
	{
		_title = title;
	}
	void setDescription(String description)
	{
		_description = description;
	}
	void setLink(String link)
	{
		_link = link;
	}
	void setCategory(String category)
	{
		_category = category;
	}
	void setPubDate(String pubdate)
	{
		_pubdate = pubdate;
	}
	String getTitle()
	{
		return _title;
	}
	String getDescription()
	{
		return _description;
	}
	String getLink()
	{
		return _link;
	}
	String getCategory()
	{
		return _category;
	}
	String getPubDate()
	{
		return _pubdate;
	}
	public String toString()
	{
		// limit how much text we display
		if (_title.length() > 42)
		{
			return _title.substring(0, 42) + "...";
		}
		return _title;
	}
} // end class RSSItem
