package com.and.netease;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.and.netease.MyListView.OnRefreshListener;
import com.and.netease.rss.RSSHandler;
import com.and.netease.rss.RSSItem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class TabNewsTopActivity extends Activity {

	MyListView listView;

	List<RSSItem> list;
	RSSHandler rssHandler;

	MyAdapter adapter;

	ViewSwitcher viewSwitcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_news_top);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);

		initViews();

		rssHandler = new RSSHandler();
		requestRSSFeed();

	}

	private void initViews() {
		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewswitcher_news_top);
		listView = new MyListView(this);
		listView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		ImageView testView = new ImageView(this);
		testView.setImageResource(R.drawable.temp);
		listView.addHeaderView(testView);
		listView.setonRefreshListener(refreshListener);
		
		viewSwitcher.addView(listView);
		viewSwitcher.addView(getLayoutInflater().inflate(R.layout.layout_progress_page, null));
		viewSwitcher.showNext();
		listView.setOnItemClickListener(listener);

	}
	
	private OnRefreshListener refreshListener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			new AsyncTask<Void, Void, Void>() {
				protected Void doInBackground(Void... params) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					adapter.notifyDataSetChanged();
					listView.onRefreshComplete();
				}

			}.execute(null);
		}
	};

	private void requestRSSFeed() {
		Thread t = new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					URL url = new URL(CONST.URL_NEWS_TOP);
					URLConnection con = url.openConnection();
					con.connect();

					InputStream input = con.getInputStream();

					SAXParserFactory fac = SAXParserFactory.newInstance();
					SAXParser parser = fac.newSAXParser();
					XMLReader reader = parser.getXMLReader();
					reader.setContentHandler(rssHandler);
					Reader r = new InputStreamReader(input, Charset.forName("GBK"));
					reader.parse(new InputSource(r));
					list = rssHandler.getData();
//					for (RSSItem rss : list) {
//						System.out.println(rss);
//					}
					if (list.size() == 0) {
						handler.sendEmptyMessage(-1);
					} else {
						handler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				adapter = new MyAdapter();
				listView.setOnItemClickListener(listener);
				listView.setAdapter(adapter);
				viewSwitcher.showPrevious();
				
				listView.onRefreshComplete();
			}
		};
	};

	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(position==1){
				return;
			}
			Intent intent = new Intent(TabNewsTopActivity.this, NewsContentActivity.class);
			intent.putExtra("content_url", list.get(position-2).getLink());
			TabNewsTopActivity.this.startActivityForResult(intent, position);
		}
	};

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.layout_news_top_item, null);
				holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date_news_top_item);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title_news_top_item);
				holder.tv_Description = (TextView) convertView.findViewById(R.id.tv_description_news_top_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_date.setText(list.get(position).getPubDate());
			holder.tv_title.setText(list.get(position).getTitle());
			holder.tv_Description.setText(list.get(position).getDescription());

			return convertView;
		}

	}

	public static class ViewHolder {
		TextView tv_date;
		TextView tv_title;
		TextView tv_Description;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("·µ»Ø");
		super.onActivityResult(requestCode, resultCode, data);
		
	}
}
