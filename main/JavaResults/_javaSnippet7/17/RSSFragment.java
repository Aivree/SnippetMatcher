package info.androidhive.StockEProject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RSSFragment extends Fragment {
	
	public RSSFragment(){}
	private RSSFeed myRssFeed = null;
	public ListView listView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_rss, container, false);

		listView = (ListView) rootView.findViewById(android.R.id.list);
		new MyTask().execute();
		
        return rootView;
    }
	
	private class MyTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				URL rssUrl = new URL("http://articlefeeds.nasdaq.com/nasdaq/categories?category=Stocks.xml");
				SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
				SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
				XMLReader myXMLReader = mySAXParser.getXMLReader();
				RSSHandler myRSSHandler = new RSSHandler();
				myXMLReader.setContentHandler(myRSSHandler);
				InputSource myInputSource = new InputSource(rssUrl.openStream());
				myXMLReader.parse(myInputSource);
				
				myRssFeed = myRSSHandler.getFeed();	
			} catch (MalformedURLException e) {
				e.printStackTrace();	
			} catch (ParserConfigurationException e) {
				e.printStackTrace();	
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();	
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (myRssFeed!=null)
			{


				
				ArrayAdapter<RSSItem> adapter =
						new ArrayAdapter<RSSItem>(getActivity().getApplicationContext(),
								R.layout.custom_layout,myRssFeed.getList());
				
				
				listView.setAdapter(adapter);
				TextView textEmpty = (TextView) getView().findViewById(android.R.id.empty);
				textEmpty.setText("");
				
			}else{
				
				TextView textEmpty = (TextView) getView().findViewById(android.R.id.empty);
				textEmpty.setText("No Feed Found!");
			}
			
			super.onPostExecute(result);
		}
		
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		Uri feedUri = Uri.parse(myRssFeed.getItem(position).getLink());
		Intent myIntent = new Intent(Intent.ACTION_VIEW, feedUri);
		startActivity(myIntent);
	}
}