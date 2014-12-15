package com.example.rss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;




import com.example.rss.MainActivity;
import com.example.rss.DatabaseHandler;



import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    final String TAG = "MainActivity.java";
    int alarmcheck = 0;
    private PendingIntent pendingIntent;
 private RSSFeed myRssFeed = null;
 String onlyonce = null;

 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
  final Button buttonStart = (Button)findViewById(R.id.startalarm);
  final Button buttonCancel = (Button)findViewById(R.id.cancelalarm);
  final Button invisButton = (Button)findViewById(R.id.invisbutt);
  final Global globalVariable = (Global) getApplicationContext();
  new MyTask().execute();

  buttonStart.setOnClickListener(new Button.OnClickListener(){

      @Override
      public void onClick(View arg0) {
    	  alarmcheck = 0;

           Intent myIntent = new Intent(MainActivity.this, MyAlarmService.class);

           pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);

           AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

           Calendar calendar = Calendar.getInstance();

           calendar.setTimeInMillis(System.currentTimeMillis());

           calendar.add(Calendar.SECOND, 1);

           alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
           while (onlyonce == null) {
        	   onlyonce = "bloop";
               Toast.makeText(MainActivity.this, "Service has started. Click Cancel to stop.", Toast.LENGTH_LONG).show();
           };
           Handler handler = new Handler();
           handler.postDelayed(new Runnable() {
               public void run() {
              	   if (alarmcheck == 0){
                      	Log.i(TAG, "Choosing to rerun without stopping service. Alarm value =" +alarmcheck);
                       buttonStart.performClick();

              	        if (globalVariable.getName() == "true") {
                            invisButton.performClick();
              	        	} else {

              	        	}
              	   
              	   }

                	   else{
                		  
                       	Log.i(TAG, "Alarm has been canceled before it was able to rerun. Alarm value =" +alarmcheck);
                	   }
               }
           }, 30000);

       }});
  buttonCancel.setOnClickListener(new Button.OnClickListener(){

      @Override

     public void onClick(View arg0) {
    	  alarmcheck = 1;
          // TODO Auto-generated method stub
   	   onlyonce = null;
          AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

          alarmManager.cancel(pendingIntent);

          Toast.makeText(MainActivity.this, "Cancel!", Toast.LENGTH_LONG).show();

      }});

  invisButton.setOnClickListener(new Button.OnClickListener(){

      @Override

     public void onClick(View arg0) {
    	  Timer timer = new Timer();
    	  timer.schedule(new TimerTask() {
    	      @Override
    	     public void run() {
    	         Notification("You have a new update!", "Your RSS news feed has an update. Please relaunch the app to update the feed.");
    	     }
    	  }, 0);
      }});}

 private void Notification(String notificationTitle, String notificationMessage) {
     NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
     android.app.Notification notification = new android.app.Notification(R.drawable.ic_launcher, "You have a new update!",
    System.currentTimeMillis());

    Intent notificationIntent = new Intent(this, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
     notification.setLatestEventInfo(MainActivity.this, notificationTitle, notificationMessage, pendingIntent);
    notificationManager.notify(10001, notification);
 }
 
 @Override
 protected void onResume() {

    super.onResume();
    this.onCreate(null);
 }
 
 public class MyTask extends AsyncTask<Void, Void, Void>{
	 
     DatabaseHandler databaseHandler;
     String type;

      
     protected MyTask(){

         this.databaseHandler = new DatabaseHandler(MainActivity.this);
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
    TextView feedTitle = (TextView)findViewById(R.id.feedtitle);
    TextView feedDescribtion = (TextView)findViewById(R.id.feeddescribtion);
    TextView feedPubdate = (TextView)findViewById(R.id.feedpubdate);
    TextView feedLink = (TextView)findViewById(R.id.feedlink);
    feedTitle.setText(myRssFeed.getTitle());
    feedDescribtion.setText(myRssFeed.getDescription());
    feedPubdate.setText(myRssFeed.getPubdate());
    feedLink.setText(myRssFeed.getLink());   
    ArrayAdapter<RSSItem> adapter =
      new ArrayAdapter<RSSItem>(getApplicationContext(),
        android.R.layout.simple_list_item_1,myRssFeed.getList());
    setListAdapter(adapter); 
    // empty the table
    databaseHandler.deleteRecords();

    databaseHandler.insertname(myRssFeed.getItem(0).getTitle(),myRssFeed.getItem(0).getPubdate());
 
   }else{
  
    TextView textEmpty = (TextView)findViewById(android.R.id.empty);
    textEmpty.setText("No Feed Found!");
   }
   
   super.onPostExecute(result);
  }
  
 }
 
 


 
 
 
 
 
 
 @Override
 protected void onListItemClick(ListView l, View v, int position, long id) {
	// TODO Auto-generated method stub
	 Intent intent = new Intent(this,ShowDetails.class);
	 Bundle bundle = new Bundle();
	 bundle.putString("keyTitle", myRssFeed.getItem(position).getTitle());
	 bundle.putString("keyDescription", myRssFeed.getItem(position).getDescription());
	 bundle.putString("keyLink", myRssFeed.getItem(position).getLink());
	 bundle.putString("keyPubdate", myRssFeed.getItem(position).getPubdate());
	 intent.putExtras(bundle);
	      startActivity(intent);

 }
 



}

