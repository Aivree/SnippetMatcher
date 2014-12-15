package de.daniel_brueggemann.rssreadertest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class RssReaderActivity extends ListActivity implements OnClickListener
{
	public static String urltest;
	private ArrayList<RSSItem> itemlist = null;
	private RSSListAdaptor rssadaptor = null;
	public Button test;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if(android.os.Build.VERSION.SDK_INT > 9)
		{
			final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			        .permitNetwork().build();
			StrictMode.setThreadPolicy(policy);
		}
		urltest="http://forum.nullcraft.de/forums/neuigkeiten.4/index.rss";
		test=(Button)findViewById(R.id.button1);
		test.setOnClickListener(this);
		itemlist = new ArrayList<RSSItem>();
		
		new RetrieveRSSFeeds().execute();
	}
	@Override
    public void onClick(View v)
    {
	    if(v==test)
	    {
	    	itemlist = new ArrayList<RSSItem>();
			
			new RetrieveRSSFeeds().execute();
	    }
	    
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		RSSItem data = itemlist.get(position);
		String URL=data.link;
		Bundle urltransfer = new Bundle();
		urltransfer.putString("datenpaket1", URL);
		Intent in = new Intent(this, Newsreader.class);
		in.putExtras(urltransfer);
		//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.link));
		
		startActivity(in);
	}
	
	private void retrieveRSSFeed(String urlToRssFeed, ArrayList<RSSItem> list)
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
		protected Void doInBackground(Void... params)
		{
			retrieveRSSFeed(urltest,itemlist);
			rssadaptor = new RSSListAdaptor(RssReaderActivity.this,
			        R.layout.rssitemview, itemlist);
			return null;
		}
		
		@Override
		protected void onCancelled()
		{
			super.onCancelled();
		}
		
		@Override
		protected void onPreExecute()
		{
			progress = ProgressDialog.show(RssReaderActivity.this, null,
			        "Lade RSS Feed...");
			
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			setListAdapter(rssadaptor);
			
			progress.dismiss();
			
			super.onPostExecute(result);
		}
		
		@Override
		protected void onProgressUpdate(Void... values)
		{
			super.onProgressUpdate(values);
		}
	}
	
	private class RSSListAdaptor extends ArrayAdapter<RSSItem>
	{
		private List<RSSItem> objects = null;
		
		public RSSListAdaptor(Context context, int textviewid,
		        List<RSSItem> objects)
		{
			super(context, textviewid, objects);
			
			this.objects = objects;
		}
		
		@Override
		public int getCount()
		{
			return ((null != objects) ? objects.size() : 0);
		}
		
		@Override
		public long getItemId(int position)
		{
			return position;
		}
		
		@Override
		public RSSItem getItem(int position)
		{
			return ((null != objects) ? objects.get(position) : null);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			
			if(null == view)
			{
				LayoutInflater vi = (LayoutInflater) RssReaderActivity.this
				        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.rssitemview, null);
			}
			
			RSSItem data = objects.get(position);
			
			if(null != data)
			{
				TextView title = (TextView) view.findViewById(R.id.txtTitle);
				TextView date = (TextView) view.findViewById(R.id.txtDate);
				TextView description = (TextView) view
				        .findViewById(R.id.txtDescription);
				
				title.setText(data.title);
				date.setText("on " + data.date);
				description.setText(data.description);
			}
			
			return view;
		}
	}
}
