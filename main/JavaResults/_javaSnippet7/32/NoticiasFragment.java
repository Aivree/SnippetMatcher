package br.edsonluis.app.brasileirao.fragment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import br.edsonluis.app.brasileirao.R;
import br.edsonluis.app.brasileirao.adapter.RSSAdapter;
import br.edsonluis.app.brasileirao.model.RSSItem;
import br.edsonluis.app.brasileirao.util.RSSParser;

public class NoticiasFragment extends Fragment implements
		SwipeRefreshLayout.OnRefreshListener {

	private Activity mContext;
	private SwipeRefreshLayout mSwipeLayout;
	private ListView mListView;
	private ArrayList<RSSItem> mListData;
	private RSSAdapter mAdapter = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_noticias, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mContext = getActivity();
		mSwipeLayout = (SwipeRefreshLayout) mContext
				.findViewById(R.id.swipe_container);
		mListView = (ListView) mContext.findViewById(R.id.list_noticias);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {

				RSSItem data = mAdapter.getItem(position);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(data.link)));

			}
		});

		new RSSTask().execute();

		setSwipeLayout();

	}

	private void setSwipeLayout() {

		mSwipeLayout = (SwipeRefreshLayout) mContext
				.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(R.color.holo_green_dark,
				R.color.holo_red_dark, R.color.holo_blue_dark,
				R.color.holo_orange_dark);
	}

	@Override
	public void onRefresh() {
		new RSSTask().execute();
	}

	private void retrieveRSSFeed(String[] urlToRssFeed, ArrayList<RSSItem> list) {

		for (int i = 0; i < urlToRssFeed.length; i++) {
			try {
				URL url = new URL(urlToRssFeed[i]);
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
	}

	private class RSSTask extends AsyncTask<Void, Void, Void> {

		private String[] FEED_URL = {
				"http://globoesporte.globo.com/servico/semantica/editorias/plantao/futebol/brasileirao-serie-a/feed.rss",
				"https://br.esporteinterativo.yahoo.com/futebol/campeonato-brasileiro/?format=rss" };

		@Override
		protected Void doInBackground(Void... params) {

			retrieveRSSFeed(FEED_URL, mListData);
			Collections.sort(mListData);
			Collections.reverse(mListData);
			mAdapter = new RSSAdapter(mContext,
					R.layout.listview_noticias_item, mListData);

			return null;
		}

		@Override
		protected void onPreExecute() {
			mListData = new ArrayList<RSSItem>();
			mSwipeLayout.setRefreshing(true);
			mSwipeLayout.setEnabled(false);
		}

		@Override
		protected void onPostExecute(Void result) {
			mListView.setAdapter(mAdapter);
			mSwipeLayout.setRefreshing(false);
			mSwipeLayout.setEnabled(true);
		}
	}
}
