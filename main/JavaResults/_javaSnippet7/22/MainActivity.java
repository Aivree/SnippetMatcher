package com.test.csed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.fima.cardsui.views.CardUI;

public class MainActivity extends ListActivity {
	CardUI mCardView;
	private RSSFeed myRssFeed = null;
	public class MyCustomAdapter extends ArrayAdapter<RSSItem> {

		 public MyCustomAdapter(Context context, int textViewResourceId,
		   List<RSSItem> list) {
		  super(context, textViewResourceId, list);
		 }

		 @Override
		 public View getView(int position, View convertView, ViewGroup parent) {
		  // TODO Auto-generated method stub
		  //return super.getView(position, convertView, parent);
			 
			 View row = convertView;
		 
			 if(row==null){
				 LayoutInflater inflater=getLayoutInflater();
				 row=inflater.inflate(R.layout.card_play, parent, false);
			 }
			 return row;
		}
}


/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
     
    new RetreiveFeedTask().execute("http://testrashwan.blogspot.com/feeds/posts/default?alt=rss");
}
    private class RetreiveFeedTask extends AsyncTask<String, Void, RSSFeed> {

        protected RSSFeed doInBackground(String... urls) {
        	try {
        		URL rssUrl= new URL(urls[0]);
            	SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
        		SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
        		XMLReader myXMLReader = mySAXParser.getXMLReader();
        		RSSHandler myRSSHandler = new RSSHandler();
        		myXMLReader.setContentHandler(myRSSHandler);
        		InputSource myInputSource = new InputSource(rssUrl.openStream());
        		myXMLReader.parse(myInputSource);

        		myRssFeed = myRSSHandler.getFeed();
        		return myRssFeed;
        	} catch (MalformedURLException e) {
        	// TODO Auto-generated catch block
        		e.printStackTrace();
        	} catch (ParserConfigurationException e) {
        	// TODO Auto-generated catch block
        		e.printStackTrace();
        		return null;
        	} catch (SAXException e) {
        	// TODO Auto-generated catch block
        		e.printStackTrace();
        		return null;
        	} catch (IOException e) {
        	// TODO Auto-generated catch block
        		e.printStackTrace();
        		return null;
        	}
    		return myRssFeed;
        }
        protected void onPostExecute(RSSFeed feed) {
        	if (myRssFeed!=null) {
        		mCardView = (CardUI) findViewById(R.id.cardsview);
    		 	mCardView.setSwipeable(true);
        		for (int i = 0; i < myRssFeed.getList().size(); i++) {
        			String title = myRssFeed.getList().get(i).getTitle();
        			String description = myRssFeed.getList().get(i).getDescription();
        			if (title.equalsIgnoreCase("OS")) {
        				mCardView.addCard(new MyPlayCard(title, description, "#ffaa00", "#ffaa00", false, false));
					}
        			else if (title.equals("Programming")) {
        				mCardView.addCard(new MyPlayCard(title, description, "#5dd0c0", "#5dd0c0", false, false));
					}
        			else if (title.equals("Electrical")) {
        				mCardView.addCard(new MyPlayCard(title, description, "#DB0058", "#DB0058", false, false));
					}
        			else if (title.equals("Statistics")) {
        				mCardView.addCard(new MyPlayCard(title, description, "#92ec00", "#92ec00", false, false));
					}
        			else if (title.equals("Control")) {
        				mCardView.addCard(new MyPlayCard(title, description, "#66a1d2", "#66a1d2", false, false));
					}
        			else if (title.equals("Systems Components")) {
        				mCardView.addCard(new MyPlayCard(title, description, "#231A43", "#231A43", false, false));
					}
        			else {
        				mCardView.addCard(new MyPlayCard(title, description, "#000000", "#000000", false, false));
					}
           		 	mCardView.refresh();
				}
			}
        }
    }
 }

    
