package com.soynerdito.app;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class RSSFeedAct extends Activity {

	public void setLoteria(Loteria value) {
		if (value != null) {
			ListView lvItems = (ListView) findViewById(R.id.listView1);
			lvItems.setAdapter(new ItemAdapter(this, value.items));
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		RetreiveFeedTask task = new RetreiveFeedTask();
		task.execute("http://www.loteriaelectronicapr.com/Games/rss2.aspx");
	}

	/**
	 * public String removeSpaces(String s) { StringTokenizer st = new
	 * StringTokenizer(s," ",false); String t=""; while (st.hasMoreElements()) t
	 * += st.nextElement(); return t; }
	 */
	private class RSSHandler extends DefaultHandler {
		private Loteria rssLoteria = null;
		private Item currItem = null;
		private EnumStat currStat = EnumStat.NONE;
		private boolean inItems = false;

		public void startElement(String uri, String localName, String qName,
				Attributes attrs) throws SAXException {
			//just initialize Loteria instance
			if (rssLoteria == null) {
				rssLoteria = new Loteria();
			}
			
			currStat = EnumStat.fromString(localName);
			//Starting a new Item
			if (currStat == EnumStat.ITEM ) {
				inItems = true;
				currItem = new Item();
			}
		}

		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
			System.out.print("STATU end " + currStat.caption );
			if (EnumStat.fromString(localName) == EnumStat.ITEM && currItem != null) {
				currItem.loadExternalData( RSSFeedAct.this);
				rssLoteria.items.add(currItem);
				currItem = null;
			}
			//clear current status
			currStat = EnumStat.NONE;
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (!inItems) {
				rssLoteria.addData(currStat, new String(ch, start, length));
			} else {
				currItem.addData(currStat, new String(ch, start, length));
			}
		}

		public Loteria getFeed() {
			return rssLoteria;
		}

	}

	class RetreiveFeedTask extends AsyncTask<String, Void, Loteria> {

		private Exception exception;
		private Loteria result = null;

		protected Loteria doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader xmlreader = parser.getXMLReader();
				RSSHandler theRSSHandler = new RSSHandler();
				xmlreader.setContentHandler(theRSSHandler);
				InputSource is = new InputSource(url.openStream());
				xmlreader.parse(is);
				result = theRSSHandler.getFeed();
				return result;
			} catch (Exception e) {
				this.exception = e;
				return null;
			}
		}

		protected void onPostExecute(Loteria feed) {
			// TODO: check this.exception
			// TODO: do something with the feed
			RSSFeedAct.this.setLoteria(result);
		}
	}
}