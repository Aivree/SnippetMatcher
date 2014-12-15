package com.example.frag;

import java.net.URL;
import java.util.ArrayList;




import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.*;
//import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Tab3Fragment extends android.support.v4.app.ListFragment {
	private ArrayList<RSSItem> itemlist = null;
    private RSSListAdaptor rssadaptor = null;
    
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
		itemlist = new ArrayList<RSSItem>();
	     Log.d("AAA", "Before execute");
	    new RetrieveRSSFeeds().execute();
	    Log.d("AAA", "Aftere execute");
		return inflater.inflate(R.layout.tab_frag3_layout, container, false);
	}

	 public void onListItemClick(ListView l, View v, int position, long id) {
         super.onListItemClick(l, v, position, id);
          
         RSSItem data = itemlist.get(position);
          
         Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(data.link));
          
         startActivity(intent);
 }

 private void retrieveRSSFeed(String urlToRssFeed,ArrayList<RSSItem> list)
{
 try
 {
    URL url = new URL(urlToRssFeed);
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    XMLReader xmlreader = parser.getXMLReader();
    RSSParser theRssHandler = new RSSParser(list);

    xmlreader.setContentHandler(theRssHandler);

    InputSource is = new InputSource(url.openStream());

    xmlreader.parse(is);
 }
 catch (Exception e)
 {
     e.printStackTrace();
 }
}

private class RetrieveRSSFeeds extends AsyncTask<Void, Void, Void>
{
 private ProgressDialog progress = null;
  
         @Override
         protected Void doInBackground(Void... params) {
                 retrieveRSSFeed("http://www.krvarma.com/feed",itemlist);
                  
                 rssadaptor = new RSSListAdaptor(getActivity(), R.layout.postitem,itemlist);
                  
                 return null;
         }
  
         @Override
         protected void onCancelled() {
                 super.onCancelled();
         }
          
         @Override
         protected void onPreExecute() {
                // progress = ProgressDialog.show(
                  //       getActivity(), null, "Loading RSS Feeds...");
                  
                 super.onPreExecute();
         }
          
         @Override
         protected void onPostExecute(Void result) {
                 setListAdapter(rssadaptor);
                  
                // progress.dismiss();
                  
                 super.onPostExecute(result);
         }
          
         @Override
         protected void onProgressUpdate(Void... values) {
                 super.onProgressUpdate(values);
         }
}

private class RSSListAdaptor extends ArrayAdapter<RSSItem>{
 private List<RSSItem> objects = null;
  
         public RSSListAdaptor(Context context, int textviewid, List<RSSItem> objects) {
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
         public RSSItem getItem(int position) {
                 return ((null != objects) ? objects.get(position) : null);
         }
          
         public View getView(int position, View convertView, ViewGroup parent) {
                 View view = convertView;
                  
                 if(null == view)
                 {
                         LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                         view = vi.inflate(R.layout.postitem, null);
                 }
                  
                 RSSItem data = objects.get(position);
                  
                 if(null != data)
                 {
                         TextView title = (TextView)view.findViewById(R.id.txtTitle);
                         TextView date = (TextView)view.findViewById(R.id.txtDate);
                         TextView description = (TextView)view.findViewById(R.id.txtDescription);
                          
                         title.setText(data.title);
                         date.setText("on " + data.date);
                         description.setText(data.description);
                 }
                  
                // SlidingDrawer sd2=(SlidingDrawer) getActivity().findViewById(R.id.slidingDrawer2);
                 
                // sd2.lock();
                 
                 
                 return view;
         }
}

}