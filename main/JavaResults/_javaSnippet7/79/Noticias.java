package noticias.serpisapp_alpha;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.example.serpisapp_alpha.MainActivity;
import com.example.serpisapp_alpha.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Noticias extends ListActivity {
	
	public class RssLoadingTask extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog pDialog;
		
		@Override
		protected void onPostExecute(Void result) {
			pDialog.dismiss();
			// TODO Auto-generated method stub
			displayRss();
		}

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(Noticias.this);
			pDialog.setMessage("Cargando las Noticias. Por favor, espere...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			// TODO Auto-generated method stub
			preReadRss();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			//super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			readRss();
			return null;
		}

	}

	private RSSFeed myRssFeed = null;
	
	TextView feedTitle;
	TextView feedDescribtion;
	TextView feedLink;
	
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
				row=inflater.inflate(R.layout.rss_row, parent, false);	
			}
			
			TextView listTitle=(TextView)row.findViewById(R.id.listtitle);
			listTitle.setText(myRssFeed.getList().get(position).getTitle());
			TextView listPubdate=(TextView)row.findViewById(R.id.listpubdate);
			listPubdate.setText(myRssFeed.getList().get(position).getPubdate());
			
			return row;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticias);
                
		feedTitle = (TextView)findViewById(R.id.feedtitle);
		feedDescribtion = (TextView)findViewById(R.id.feeddesc);
		feedLink = (TextView)findViewById(R.id.feedlink);
        
        startReadRss();
    }
    
    private void startReadRss(){
    	new RssLoadingTask().execute();
    }
    
    private void preReadRss()
    {
    	feedTitle.setText("--- Cargando ---");
		feedDescribtion.setText("");
		feedLink.setText("");
		setListAdapter(null);
		
    }
    
    private void readRss(){
    	
        try {
			URL rssUrl = new URL(MainActivity.static_rss);
			SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
			SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
			XMLReader myXMLReader = mySAXParser.getXMLReader();
			RSSHandler myRSSHandler = new RSSHandler();
			myXMLReader.setContentHandler(myRSSHandler);
			InputSource myInputSource = new InputSource(rssUrl.openStream());
			myXMLReader.parse(myInputSource);
			
			myRssFeed = myRSSHandler.getFeed();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    private void displayRss()
    {
		if (myRssFeed!=null)
		{
			Calendar c = Calendar.getInstance();
			int hrs = c.get(Calendar.HOUR_OF_DAY);
			int mnts = c.get(Calendar.MINUTE);
			String horaActual = String.format("%02d:%02d", hrs, mnts);
		    String str_horaActual =  " · (Ultima lectura - "+horaActual+")";

			feedTitle.setText(myRssFeed.getTitle() + str_horaActual);
			feedDescribtion.setText(myRssFeed.getDescription());
			feedLink.setText(myRssFeed.getLink());
			
			MyCustomAdapter adapter =
				new MyCustomAdapter(this, R.layout.rss_row, myRssFeed.getList());
			setListAdapter(adapter);
			
		}
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,DetallesNoticia.class);
		Bundle bundle = new Bundle();
		bundle.putString("keyTitle", myRssFeed.getItem(position).getTitle());
		bundle.putString("keyDescription", myRssFeed.getItem(position).getDescription());
		bundle.putString("keyLink", myRssFeed.getItem(position).getLink());
		bundle.putString("keyPubdate", myRssFeed.getItem(position).getPubdate());
		intent.putExtras(bundle);
        startActivity(intent);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
		 if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			 Intent intent = new Intent(getApplicationContext(),com.example.serpisapp_alpha.MainActivity.class);
			 finish();
			 startActivity(intent);
	        }
		 return false;
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_rss, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.m_rss:
        	Intent intent = new Intent(getApplicationContext(), PreferenciasRss.class);
        	finish();
        	startActivity(intent);
        	break;
		}
		
		return true;
	}
    
    
}