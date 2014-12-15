package ru.ifmo.rain.tkachenko.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import ru.ifmo.rain.tkachenko.rssreader.RSSFeed;
import ru.ifmo.rain.tkachenko.rssreader.RSSHandler;
import ru.ifmo.rain.tkachenko.rssreader.RSSItem;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.net.URL;
import java.util.ArrayList;

public class LinksDbAdapter {
    public static final String KEY_URL = "url";
    public static final String KEY_TITLE = "title";
    public static final String KEY_FAV = "fav";
    public static final String KEY_FEED = "feed";

    private Cursor linkCursor;
    private Context myContext;

    public LinksDbAdapter(Context ctx) {
        this.myContext = ctx;

    }


    public void createLink(String url, String title, String fav, String feed) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_FAV, fav);
        initialValues.put(KEY_FEED, feed);


        myContext.getContentResolver().insert(
                LinkContentProvider.CONTENT_LINK_URI, initialValues);
    }

    public void deleteLink(long rowId) {
        Uri uri = ContentUris.withAppendedId(
                LinkContentProvider.CONTENT_LINK_URI, rowId);
        myContext.getContentResolver().delete(uri, null, null);
    }

    public void deleteLink(String link) {
        deleteLink(getIdByLink(link));
    }

    public Cursor fetchAllLinks() {
        return myContext.getContentResolver().query(
                LinkContentProvider.CONTENT_LINK_URI, null, null, null, null);
    }

    public Cursor fetchLink(long rowId) throws SQLException {
        Uri uri = Uri.withAppendedPath(LinkContentProvider.CONTENT_LINK_URI,
                Long.toString(rowId));
        Cursor mCursor = myContext.getContentResolver().query(uri, null, null,
                null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void updateLink(long rowId, String url) {
        ContentValues args = new ContentValues();
        args.put(KEY_URL, url);

        Uri uri = Uri.withAppendedPath(LinkContentProvider.CONTENT_LINK_URI,
                Long.toString(rowId));
        myContext.getContentResolver().update(uri, args, null, null);
    }

    public void updateLink(String oldUrl, String url) {
        updateLink(getIdByLink(oldUrl), url);
    }

    public void updateTitle(String oldTitle, String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        long rowId = getIdByTitle(oldTitle);

        Uri uri = Uri.withAppendedPath(LinkContentProvider.CONTENT_LINK_URI,
                Long.toString(rowId));
        myContext.getContentResolver().update(uri, args, null, null);
    }

    public void updateFav(String title, String fav) {
        ContentValues args = new ContentValues();
        args.put(KEY_FAV, fav);
        long rowId = getIdByTitle(title);

        Uri uri = Uri.withAppendedPath(LinkContentProvider.CONTENT_LINK_URI,
                Long.toString(rowId));
        myContext.getContentResolver().update(uri, args, null, null);
    }

    public boolean isFavorite(String title) {
        Cursor link = fetchLink(getIdByTitle(title));
        return link.getString(link
                .getColumnIndex("fav")).equals("true");
    }


    public void updateFeed(String title, String feed) {
        ContentValues args = new ContentValues();
        args.put(KEY_FEED, feed);
        long rowId = getIdByTitle(title);

        Uri uri = Uri.withAppendedPath(LinkContentProvider.CONTENT_LINK_URI,
                Long.toString(rowId));
        myContext.getContentResolver().update(uri, args, null, null);
    }

    public void updateAllFeeds() {
        curAdapter = this;
        MyTask mt = new MyTask();
        mt.execute();
    }

    public volatile boolean f = true, isRefresh = false, isCalced = true;
    public LinksDbAdapter curAdapter = null;

    class MyTask extends AsyncTask<Void, Void, Void> {
        private RSSFeed feed = null;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            linkCursor = curAdapter.fetchAllLinks();
            if (linkCursor.moveToFirst()) {
                do {
                    String cur = "http://" + linkCursor.getString(linkCursor
                            .getColumnIndex("url"));

                    try {
                        URL url = new URL(cur);

                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser parser = factory.newSAXParser();

                        XMLReader xmlreader = parser.getXMLReader();
                        RSSHandler theRssHandler = new RSSHandler();
                        xmlreader.setContentHandler(theRssHandler);
                        InputSource is = new InputSource(url.openStream());
                        xmlreader.parse(is);

                        feed = theRssHandler.getFeed();
                        ArrayList<RSSItem> data = feed.getAllItems();
                        String rssFeedString = "";
                        for (int i = 0; i < data.size(); i++) {
                            rssFeedString += data.get(i).getTitle() + " # " + data.get(i).getDescription() + " # " + data.get(i).getPubDate() + " ## ";
                        }
                        rssFeedString += "###";
                        curAdapter.updateFeed(linkCursor.getString(linkCursor
                                .getColumnIndex("title")), rssFeedString);
                        Log.i("url", "OK");

                    } catch (Exception ee) {
                    }
                } while (linkCursor.moveToNext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }


    }


    private long getIdByLink(String link) {
        linkCursor = this.fetchAllLinks();
        long id = 0;
        if (linkCursor.moveToFirst()) {
            do {
                String cur = linkCursor.getString(linkCursor
                        .getColumnIndex("url"));

                if (cur.equals(link)) {
                    return linkCursor.getLong(linkCursor.getColumnIndex("_id"));
                }
            } while (linkCursor.moveToNext());
        }
        return 0;
    }

    private long getIdByTitle(String title) {
        linkCursor = this.fetchAllLinks();
        long id = 0;
        if (linkCursor.moveToFirst()) {
            do {
                String cur = linkCursor.getString(linkCursor
                        .getColumnIndex("title"));

                if (cur.equals(title)) {
                    return linkCursor.getLong(linkCursor.getColumnIndex("_id"));
                }
            } while (linkCursor.moveToNext());
        }
        return 0;
    }
    private boolean haveTitle(String title) {
        linkCursor = this.fetchAllLinks();
        if (linkCursor.moveToFirst()) {
            do {
                String cur = linkCursor.getString(linkCursor
                        .getColumnIndex("title"));

                if (cur.equals(title)) {
                    return true;
                }
            } while (linkCursor.moveToNext());
        }
        return false;
    }
    public String getFeed(String title) {
        if (!haveTitle(title)) {
            return "";
        }
        linkCursor = this.fetchAllLinks();
        if (linkCursor.moveToFirst()) {
            do {
                String cur = linkCursor.getString(linkCursor
                        .getColumnIndex("title"));

                if (cur.equals(title)) {
                    return linkCursor.getString(linkCursor
                            .getColumnIndex("feed"));
                }
            } while (linkCursor.moveToNext());
        }

        return null;
    }


}
