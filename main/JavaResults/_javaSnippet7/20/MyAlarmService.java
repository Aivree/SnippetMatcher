package com.example.rss;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
public class MyAlarmService extends Service {
	 private RSSFeed myRssFeed = null;
	 private static final String TAG = "MyApp";

@Override
public void onCreate() {


}
@Override
public IBinder onBind(Intent intent) {

return null;
}
@Override
public void onDestroy() {
super.onDestroy();

}
@Override
public void onStart(Intent intent, int startId) {
	
super.onStart(intent, startId);
new MyTask().execute();

}

public class MyTask extends AsyncTask<Void, Void, Void>{
	final Global globalVariable = (Global) getApplicationContext();
    DatabaseHandler databaseHandler;
    String type;

     
    protected MyTask(){

        this.databaseHandler = new DatabaseHandler(MyAlarmService.this);
    }
    
 @Override
 protected Void doInBackground(Void... arg0) {
  try {
	   
   URL rssUrl = new URL("http://feeds.gawker.com/kotaku/full");
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

      if (databaseHandler.countRecords(myRssFeed.getItem(0).getTitle(),myRssFeed.getItem(0).getPubdate()) == "false") {
    	  globalVariable.setName("false");
        	Log.i(TAG, "Since both are the same, nothing will be done. global variable value =" + globalVariable.getName());

        	} else {
          	  globalVariable.setName("true");
              databaseHandler.insertname(myRssFeed.getItem(0).getTitle(),myRssFeed.getItem(0).getPubdate());
            	Log.i(TAG, "Both are different therefore notification will be sent soon.  global variable value =" + globalVariable.getName());
        	}
	  
	  
  }else{

  }
  
  super.onPostExecute(result);
 }
 
}








@Override
public boolean onUnbind(Intent intent) {

return super.onUnbind(intent);
}
}

