package ca.cbc.mobile.android.cbcdroid;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;





import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class World extends ListActivity  
{
    ProgressDialog myProgress;
    
    public static final String TAG = "news";

    // declare button for home
    private Button mHomeButton;

    // declare button for news
    private Button mNewsButton;

    // declare button for schedule
    private Button mScheduleButton;
    
    // declare button for video
    private Button mVideoButton;
    

    // declare button for more
    private Button mMoreButton;

    /* private String _siteId = "1429"; */

    private String _siteId = "9063";

    /* private String _partnerId = "1dd21b33bd603c95"; */

    private String _partnerId = "387131ac6a1aa80d";

    public class RssLoadingTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPostExecute(Void result)
        {
            // TODO Auto-generated method stub
            myProgress.cancel();
            displayRss();

        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            myProgress = ProgressDialog.show(World.this, "...retrieving...",
                    "...please wait...", true, false);
            preReadRss();

        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            // TODO Auto-generated method stub
            // super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            // TODO Auto-generated method stub
            readRss();
            return null;
        }

    }

    private RSSFeed myRssFeed = null;

    TextView feedTitle;

    TextView feedDescribtion;

    TextView feedPubdate;

    TextView feedLink;

    public class MyCustomAdapter extends ArrayAdapter<RSSItem>
    {

        public MyCustomAdapter(Context context, int textViewResourceId,
                List<RSSItem> list)
        {
            super(context, textViewResourceId, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            View row = convertView;

            if (row == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.row, parent, false);
            }

            TextView listTitle = (TextView) row.findViewById(R.id.listtitle);
            listTitle.setText(myRssFeed.getList().get(position).getTitle());
            TextView listPubdate = (TextView) row
                    .findViewById(R.id.listpubdate);
            listPubdate.setText(myRssFeed.getList().get(position).getPubdate());

            if (position % 2 == 0)
            {
                // listTitle.setBackgroundColor(0xff101010);
                
                // listTitle.setBackgroundColor(R.color.white);
                listTitle.setTextColor(R.color.black);
                
                // listPubdate.setBackgroundColor(0xff101010);
                // listPubdate.setBackgroundColor(R.color.white);
                listPubdate.setTextColor(R.color.black);
                
            } else
            {
                // listTitle.setBackgroundColor(0xff080808);
                //listTitle.setBackgroundColor(R.color.white);
                listTitle.setTextColor(R.color.black);
                
                // listPubdate.setBackgroundColor(0xff080808);
                //listPubdate.setBackgroundColor(R.color.white);
                listPubdate.setTextColor(R.color.black);
                
                
            }

            return row;
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_main);

        /*
         * feedTitle = (TextView)findViewById(R.id.feedtitle); feedDescribtion =
         * (TextView)findViewById(R.id.feeddescribtion); feedPubdate =
         * (TextView)findViewById(R.id.feedpubdate); feedLink =
         * (TextView)findViewById(R.id.feedlink);
         */


       


  
        startReadRss();
    }

    private void startReadRss()
    {
        new RssLoadingTask().execute();
    }

    private void preReadRss()
    {
        /*
         * feedTitle.setText("--- wait ---"); feedDescribtion.setText("");
         * feedPubdate.setText(""); feedLink.setText("");
         */
        setListAdapter(null);

        /*
         * Toast.makeText(this, "Reading RSS, Please wait.",
         * Toast.LENGTH_LONG).show();
         */

    }

    private void readRss()
    {

        try
        {

            URL rssUrl = new URL(
                    "http://feeds.bbci.co.uk/news/rss.xml?edition=int");

            SAXParserFactory mySAXParserFactory = SAXParserFactory
                    .newInstance();
            SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
            XMLReader myXMLReader = mySAXParser.getXMLReader();
            RSSHandler myRSSHandler = new RSSHandler();
            myXMLReader.setContentHandler(myRSSHandler);
            InputSource myInputSource = new InputSource(rssUrl.openStream());
            myXMLReader.parse(myInputSource);

            myRssFeed = myRSSHandler.getFeed();

        } catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void displayRss()
    {
        if (myRssFeed != null)
        {
            Calendar c = Calendar.getInstance();
            String strCurrentTiime = "\n(Time of Reading - "
                    + c.get(Calendar.HOUR_OF_DAY) + " : "
                    + c.get(Calendar.MINUTE) + ")\n";

            /*
             * feedTitle.setText(myRssFeed.getTitle() + strCurrentTiime);
             * 
             * 
             * 
             * 
             * feedDescribtion.setText(myRssFeed.getDescription());
             * 
             * 
             * feedPubdate.setText(myRssFeed.getPubdate());
             * 
             * feedLink.setText(myRssFeed.getLink());
             */

            MyCustomAdapter adapter = new MyCustomAdapter(this, R.layout.row,
                    myRssFeed.getList());
            setListAdapter(adapter);

        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        // TODO Auto-generated method stub
        Intent intent = new Intent(this, RSSShowDetails.class);
        Bundle bundle = new Bundle();
        bundle.putString("keyTitle", myRssFeed.getItem(position).getTitle());
        bundle.putString("keyDescription", myRssFeed.getItem(position)
                .getDescription());
        bundle.putString("keyLink", myRssFeed.getItem(position).getLink());
        bundle
                .putString("keyPubdate", myRssFeed.getItem(position)
                        .getPubdate());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO Auto-generated method stub
        menu.add(0, 0, 0, "Reload");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        switch (item.getItemId())
        {
        case (0):
            startReadRss();
            break;
        default:
            break;
        }

        return true;
    }

 

    

}