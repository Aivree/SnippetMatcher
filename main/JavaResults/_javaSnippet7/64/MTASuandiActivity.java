package com.tege.blogmtasuandi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class MTASuandiActivity extends ListActivity {
	/** Called when the activity is first created. */

	private ArrayList<MTASuandiItem> itemlist = null;
	private RSSListAdaptor rssadaptor = null;

	String url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.news);

		Bundle extras = getIntent().getExtras();
		String userName;

		if (extras != null) {
			userName = extras.getString("url");
			// and get whatever type user account id is
			url = userName;

		}

		itemlist = new ArrayList<MTASuandiItem>();

		new RetrieveRSSFeeds().execute();

		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					// @Override
					public void onRefresh() {
						// Do work to refresh the list here.
						new GetDataTask().execute();
					}
				});

	}

	private class GetDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// Simulates a background job.

			// try {
			// Thread.sleep(2000);
			//
			// } catch (InterruptedException e) {
			// ;
			// }
			// return null;
			retrieveRSSFeed(url, itemlist);

			rssadaptor = new RSSListAdaptor(MTASuandiActivity.this,
					R.layout.rssitemview, itemlist);
			// setListAdapter(rssadaptor);
			return null;

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {

			((PullToRefreshListView) getListView()).onRefreshComplete();
			setListAdapter(rssadaptor);
			// super.onPostExecute(result);
			// Call onRefreshComplete when the list has been refreshed.
			// ((PullToRefreshListView) getListView()).onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		MTASuandiItem data = itemlist.get(position);

		Intent i = new Intent(this, MTASuandiViewPost.class);
		i.putExtra("url", data.link);
		i.putExtra("title", data.title);
		i.putExtra("date", data.date);
		i.putExtra("desc", data.desc);
		this.startActivity(i);

	}

	private void retrieveRSSFeed(String urlToRssFeed,
			ArrayList<MTASuandiItem> list) {
		try {
			URL url = new URL(urlToRssFeed);
			URLConnection urlConn = url.openConnection();
			InputStream inputStream = urlConn.getInputStream();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			MTASuandiParser theRssHandler = new MTASuandiParser(list);
			        
			InputSource is = new InputSource(inputStream);
			System.out.println(is.getEncoding());
			xmlreader.parse(is);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class RetrieveRSSFeeds extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(Void... params) {
			retrieveRSSFeed(url, itemlist);

			rssadaptor = new RSSListAdaptor(MTASuandiActivity.this,
					R.layout.rssitemview, itemlist);

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(MTASuandiActivity.this, null,
					"Loading...");

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

	private class RSSListAdaptor extends ArrayAdapter<MTASuandiItem> {
		private List<MTASuandiItem> objects = null;

		public RSSListAdaptor(Context context, int textviewid,
				List<MTASuandiItem> objects) {
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
		public MTASuandiItem getItem(int position) {
			return ((null != objects) ? objects.get(position) : null);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;

			if (null == view) {
				LayoutInflater vi = (LayoutInflater) MTASuandiActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.rssitemview, null);
			}

			MTASuandiItem data = objects.get(position);

			if (null != data) {
				TextView title = (TextView) view.findViewById(R.id.txtTitle);
				TextView date = (TextView) view.findViewById(R.id.txtDate);
				TextView description = (TextView) view
						.findViewById(R.id.txtDescription);
				// System.out.println(data.title);
				title.setText(data.title);
				date.setText("on " + data.date);
				description.setText(data.description);
			}

			return view;
		}
	}
}