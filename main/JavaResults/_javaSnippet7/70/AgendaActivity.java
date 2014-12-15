package thesociallions.stdy;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import thesociallions.stdy.agenda.RSSItem;
import thesociallions.stdy.agenda.RSSParser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AgendaActivity extends ListActivity {
    public final static String article_title = "thesociallions.stdy.title_event";
    public final static String article_content = "thesociallions.stdy.content_event";
    public final static String article_date = "thesociallions.stdy.date_event";
    public final static String article_link = "thesociallions.stdy.link_event";

    private ArrayList<RSSItem> itemlist = null;
    private RSSListAdaptor rssadaptor = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        setupActionBar();
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.ic_menu_agenda);

        itemlist = new ArrayList<RSSItem>();

        new RetrieveRSSFeeds().execute();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        RSSItem data = itemlist.get(position);

        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra(article_title, data.title);
        intent.putExtra(article_content, data.description);
        intent.putExtra(article_date, data.date);
        intent.putExtra(article_link, data.link);
        startActivity(intent);
    }

    private void retrieveRSSFeed(String urlToRssFeed,ArrayList<RSSItem> list)
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
        protected Void doInBackground(Void... params) {
            retrieveRSSFeed("http://www.stdy.it/appbackbone/events.xml",itemlist);

            rssadaptor = new RSSListAdaptor(AgendaActivity.this, R.layout.activity_event_itemlayout,itemlist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(
                    AgendaActivity.this, null, "Agenda laden...");

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

    private class RSSListAdaptor extends ArrayAdapter<RSSItem>{
        private List<RSSItem> objects = null;

        public RSSListAdaptor(Context context, int textviewid, List<RSSItem> objects) {
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

            if(null == view)
            {
                LayoutInflater vi = (LayoutInflater)AgendaActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.activity_event_itemlayout, null);
            }

            RSSItem data = objects.get(position);

            if(null != data)
            {
                TextView title = (TextView)view.findViewById(R.id.txtTitle);
                TextView date = (TextView)view.findViewById(R.id.txtDate);

                title.setText(data.title);
                date.setText(data.date);
            }

            return view;
        }
    }
}