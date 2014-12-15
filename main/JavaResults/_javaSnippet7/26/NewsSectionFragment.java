/**
 * 
 */
package com.seniorproject.stocksign.fragment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.seniorproject.stocksign.R;

/**
 * @author Sean
 * 
 */
public class NewsSectionFragment extends ListFragment {

	private ArrayList<RSSItem> itemlist = null;
	private RSSListAdaptor rssadaptor = null;

	/** Should not be instantiated, empty constructor */
	public NewsSectionFragment() {

	}

	/**
	 * Called when fragment is starting, where most inistialization should go
	 * 
	 * @param
	 * @return rootView The view to be displayed
	 * */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_news, container,
				false);

		itemlist = new ArrayList<RSSItem>();

		TextView newsTextView = (TextView) rootView
				.findViewById(R.id.section_label);
		newsTextView.setText("News");

		new RetrieveRSSFeeds().execute();

		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		RSSItem data = itemlist.get(position);

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.link));

		startActivity(intent);
	}

	private void retrieveRSSFeed(String urlToRssFeed, ArrayList<RSSItem> list) {
		try {
			URL url = new URL(urlToRssFeed);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			RSSParser theRssHandler = new RSSParser(list);

			xmlreader.setContentHandler(theRssHandler);

			InputSource is = new InputSource(url.openStream());

			xmlreader.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class RetrieveRSSFeeds extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(Void... params) {
			// retrieveRSSFeed("http://feeds.marketwatch.com/marketwatch/topstories",itemlist);
			retrieveRSSFeed(
					"http://pipes.yahoo.com/pipes/pipe.run?_id=c6359b90d0d523c8a6098c811747a6a4&_render=rss",
					itemlist);
			rssadaptor = new RSSListAdaptor(getActivity(),
					R.layout.rssitemview, itemlist);

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			// progress = ProgressDialog.show(getActivity(),
			// null,"Loading News Feed...");

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

	private class RSSListAdaptor extends ArrayAdapter<RSSItem> {
		private List<RSSItem> objects = null;

		public RSSListAdaptor(Context context, int textviewid,
				List<RSSItem> objects) {
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

			if (null == view) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.rssitemview, null);
			}

			RSSItem data = objects.get(position);

			if (null != data) {
				TextView title = (TextView) view.findViewById(R.id.txtTitle);
				TextView date = (TextView) view.findViewById(R.id.txtDate);
				TextView description = (TextView) view
						.findViewById(R.id.txtDescription);

				title.setText(data.title);

				date.setText("on " + data.date);

				String[] textplusimage = data.description.toString().split("<");
				data.description = textplusimage[0];
				description.setText(data.description);
			}

			return view;
		}
	}
}
