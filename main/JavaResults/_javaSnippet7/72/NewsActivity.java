package mediaApp.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import mediaApp.RSS.RSSItem;
import mediaApp.RSS.RSSParser;
import mediaApp.compatible.ActionBarListActivity;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class NewsActivity extends ActionBarListActivity
{

	private ArrayList<RSSItem>	itemlist	= null;
	private RSSListAdapter		rssadapter	= null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			int StartUpSelection = sharedPreferences.getInt(SettingsActivity.SELECTION_KEY, R.id.RBNews);
			if (StartUpSelection != R.id.RBNews)
				this.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		itemlist = new ArrayList<RSSItem>();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		new RetrieveRSSFeeds().execute();
		
		GoogleAnalyticsTracker tracker = ((MediaApplication) getApplication()).getTracker();
		tracker.trackPageView("android/news");
		tracker.dispatch();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.news, menu);
		return true;// super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
				int StartUpSelection = sharedPreferences.getInt(SettingsActivity.SELECTION_KEY, R.id.RBNews);
				Intent intent;
				switch (StartUpSelection)
				{
					case R.id.RBSearch:
						intent = new Intent(this, SearchActivity.class);
						break;
					case R.id.RBNews:
						intent = new Intent(this, NewsActivity.class);
						break;
					case R.id.RBContact:
						intent = new Intent(this, ContactActivity.class);
						break;
					default:
						intent = new Intent(this, NewsActivity.class);
						break;
				}
				startActivity(intent);
				break;

			case R.id.menu_news:
				new RetrieveRSSFeeds().execute();
				break;

			case R.id.menu_search:
				final Intent search = new Intent(this, SearchActivity.class);
				startActivity(search);
				break;

			case R.id.menu_contact:
				final Intent contact = new Intent(this, ContactActivity.class);
				startActivity(contact);
				break;

			case R.id.menu_settings:
				final Intent settings = new Intent(this, SettingsActivity.class);
				startActivity(settings);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		RSSItem data = itemlist.get(position);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.link));
		startActivity(intent);
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

		private ProgressDialog	progress	= null;

		@Override
		protected Void doInBackground(Void... params)
		{
			retrieveRSSFeed("http://www.mediatheek.hu.nl/Rss.ashx?ID={971F23C4-AB46-4386-BD4C-6343799B7281}&parentID={98653ABB-C8A4-4848-B883-87EE3C7B28FA}",
							itemlist);
			rssadapter = new RSSListAdapter(NewsActivity.this, R.layout.rssitemview, itemlist);
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
			progress = ProgressDialog.show(NewsActivity.this, null, 
        			getResources().getString(R.string.dialogLoadingRSS));
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result)
		{
			setListAdapter(rssadapter);
			progress.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values)
		{
			super.onProgressUpdate(values);
		}
	}

	private class RSSListAdapter extends ArrayAdapter<RSSItem>
	{
		private List<RSSItem>	objects	= null;
		public RSSListAdapter(Context context, int textviewid, List<RSSItem> objects)
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

			if (null == view)
			{
				LayoutInflater vi = (LayoutInflater) NewsActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.rssitemview, null);
			}

			RSSItem data = objects.get(position);

			if (null != data)
			{
				TextView title = (TextView) view.findViewById(R.id.txtTitle);
				TextView date = (TextView) view.findViewById(R.id.txtDate);
				TextView description = (TextView) view.findViewById(R.id.txtDescription);
				title.setText(data.title);
				date.setText(data.date);
				description.setText(data.description);
			}
			return view;
		}
	}
}