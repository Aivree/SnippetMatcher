package uk.ac.software.blogreader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import uk.ac.software.parser.DOMParser;
import uk.ac.software.parser.DiffRSSItemParser;
import uk.ac.software.parser.RSSFeed;
import uk.ac.software.parser.RSSItem;
import uk.ac.software.parser.RSSItemParser;
import uk.ac.software.parser.SAXSSIParser;

public class LoadingActivity extends Activity{
	private String RSSFEEDURL = "http://www.software.ac.uk/blog/rss-all";
	RSSFeed feed = null;
	RSSFeed rem_oldfeed = null;
	String cache_date;
	String RSS_date;
	RSSFeed first_feed = null;
	RSSFeed diff_feed = null;
	//RSSFeed final_feed = null;
	String fileName;
	String fileNew;
    DOMParser myParser = new DOMParser();
    SAXSSIParser mySAXParser = new SAXSSIParser();
    RSSItemParser myRSSItemParser = new RSSItemParser();
    DiffRSSItemParser mydiff = new DiffRSSItemParser();
    private static final String TAG = "MyActivity";
 
	
	//Progress Bar Functionality
		private ProgressBar mProgress;
		private int mProgressStatus=0;
		private Handler mHandler = new Handler();
		private TextView textView;	
		
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		//Intent that will return to previous activity.
				Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(myIntent,0);
				return super.onOptionsItemSelected(item);
			}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    
		    setContentView(R.layout.activity_blogs_display_list);
	        
	        //Progress bar on Loading activity screen
	        mProgress = (ProgressBar)findViewById(R.id.progress_bartop);
	        textView = (TextView)findViewById(R.id.textview1);
      	
	        //Executing background task for fetching RSS feed and parsing XML to load list of blogs
	        new AsyncCheckRSSFeed().execute(); 

      	}
	
	private void startListActivity(RSSFeed feed){
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("feed", feed);
		
		//launch List Activity
		Intent intent = new Intent(LoadingActivity.this,ListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		
		//Kill this activity
		finish();
		
	}
	
	private class AsyncCheckRSSFeed extends AsyncTask<Void, Void, Void>{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			fileName = "SSIRSSFeed.blog";
			fileNew = "RSSFeedFinal.blog";

			File feedFile = getBaseContext().getFileStreamPath(fileName);
			File feed_diff = getBaseContext().getFileStreamPath(fileNew);

			ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


			if ((conMgr.getActiveNetworkInfo() == null) && (!feedFile.exists()))

			{
			     // No connectivity & Feed file doesn't exist: Show alert to exit & check for connectivity
			      runOnUiThread(new Runnable(){
			      @Override
			      public void run() {
		          // TODO Auto-generated method stub
			      AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);
			      builder.setMessage(

			      "Unable to reach server, \nPlease check your connectivity.")

			      .setTitle("SSI Blog")

			      .setCancelable(false)

			      .setPositiveButton("Exit",

			      new DialogInterface.OnClickListener() {

			      @Override

			      public void onClick(DialogInterface dialog,

			      int id) {

			      finish();

			      }

			      });

			      AlertDialog alert = builder.create();

			      alert.show();

			     }


			     });


			 }


			else if((conMgr.getActiveNetworkInfo() == null) && feedFile.exists()){


			//No connectivity and file exists: Read feed from the File


			runOnUiThread(new Runnable(){



			@Override


			public void run() {


			// TODO Auto-generated method stub


			Toast toast = Toast.makeText(LoadingActivity.this, "No connectivity! Reading last update...",Toast.LENGTH_LONG);


			toast.show();


			}


			});

			      feed = ReadFeed(fileName);

			      startListActivity(feed);


			}


			else if((conMgr.getActiveNetworkInfo() != null) && feedFile.exists()){


			//Checking for cached file even if there is network connectivity so as to reduce the overload of

			//fetching data from server even if content on web site is unchanged.

			try{
			                HttpURLConnection.setFollowRedirects(false);

			                HttpURLConnection con = (HttpURLConnection) new URL(RSSFEEDURL).openConnection();

			                con.setRequestMethod("HEAD");

			                long date = con.getLastModified();
		                   //Last modified cached file date

			                long cdate = feedFile.lastModified();
			                Log.i(TAG,"Cached file last modified date is:"+new Date(cdate));

			                Log.i(TAG,"Web server RSS feed last modified date is:"+new Date(date)); //always returns the time of request even if web site content is not updated
		                   //This is the workaround for above.
			                first_feed = myRSSItemParser.parseXml(RSSFEEDURL);


			                Log.i(TAG,"Last published blog post:"+ first_feed.getItem(0).getDate().substring(0, 25));


			                RSS_date = first_feed.getItem(0).getDate().substring(0, 25);

			                feed = ReadFeed(fileName);


			                Log.i(TAG,"Last cached file:"+ feed.getItem(0).getDate().substring(0, 25));


			                cache_date = feed.getItem(0).getDate().substring(0, 25);

			                if(cache_date.equals(RSS_date)){


			                 runOnUiThread(new Runnable(){
                              @Override
                              public void run() {

                            	  // TODO Auto-generated method stub

                            	  Toast toast = Toast.makeText(LoadingActivity.this,"Cached file is in sync with last server update",Toast.LENGTH_LONG);
                            	  toast.show();
			    }

		       });

			     startListActivity(feed);
			       }


			       else{

			       runOnUiThread(new Runnable(){

			    @Override
			    public void run() {
			          // TODO Auto-generated method stub
			          Toast toast = Toast.makeText(LoadingActivity.this,"Updating cache file",Toast.LENGTH_LONG);
			          toast.show();
			    }
			       });
		           //new AsyncLoadXMLFeed().execute(); //Logic needs to be changed to just update those which are added.
			       
			       //List Activity update Logic based on just fetching updated Blog posts
                   diff_feed = mydiff.parseXml(RSSFEEDURL, cache_date);
                   rem_oldfeed = ReadFeed(fileName);
                  
                  for (int i=0;i<rem_oldfeed.getItemCount();i++){
                	   
                	diff_feed.addItem(rem_oldfeed.getItem(i));
                	
                  }
                	   
                   //AppendFeed(diff_feed,rem_oldfeed);
                   
                   //Storing the final appended feed into cache file or updating cache file
                   WriteFeed(diff_feed);
                   
                   startListActivity(diff_feed);
                   
			       }
	            }
			                catch(Exception e){

			             e.printStackTrace();

			    }

			}


			else

			   new AsyncLoadXMLFeed().execute();
			
      		return null;
	
		}
             
	}
	
      	private class AsyncLoadXMLFeed extends AsyncTask<Void, Void, Void> {

      		@Override
      		protected Void doInBackground(Void... params) {
      			
      			//Progress bar populating list view thread
      			new Thread(new Runnable(){

      				@Override
      				public void run() {
      					// TODO Auto-generated method stub    				
      					while(mProgressStatus < 100){
      						
      						try{
      							Thread.sleep(1000);
      						}
      						catch(InterruptedException e){
      							e.printStackTrace();
      						}	
      					    mProgressStatus += 1;
      						
      						//Update Progress Bar
      						mHandler.post(new Runnable(){

      							@Override
      							public void run() {
      								// TODO Auto-generated method stub
      								mProgress.setProgress(mProgressStatus);
      								textView.setText(mProgressStatus + "%");
 
      								
      							}
      							
      							
      						});
      						
      					}
      					
      				}
      				
      			}).start();
      		 //Simulating time-consuming task of accessing data from Internet and XML parsing to render application activity 
			    feed = myParser.parseXml(RSSFEEDURL);
				//mySAXParser.get(RSSFEEDURL);
				// Log.i(TAG, "TAG: " + mySAXParser.feeds.size());
				
				/*for (int i=0;i<mySAXParser.feeds.size();i++){
					RSSItem item = mySAXParser.feeds.get(i);
				    feed.addItem(item);
				   
				}*/
			    //Log.i(TAG, "TAG: " + feed.getItemCount());
      			/*URL url = null;
      			
      			try {

      				SAXParserFactory spf = SAXParserFactory.newInstance();
      				SAXParser sp = spf.newSAXParser();
      				XMLReader xr = sp.getXMLReader();

      				url = new URL(RSSFEEDURL);
      				SAXSSIParser mySp = new SAXSSIParser();

      				xr.setContentHandler(mySp);
      				xr.parse(new InputSource(url.openStream()));


      				Log.e("ASYNC", "PARSING FINISHED");
      				
      				for(RSSItem item: mySp.getBlogList()){
      					feed.addItem(item);
      				}

      			} catch (IOException e) {
      				Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
      			} catch (SAXException e) {
      				Log.e("RSS Handler SAX", e.toString());
      				e.printStackTrace();
      			} catch (ParserConfigurationException e) {
      				Log.e("RSS Handler Parser Config", e.toString());
      			}*/

    	      	if (feed != null && feed.getItemCount() > 0)
    	      		WriteFeed(feed);		
      	return null;

      		}
      		
      		
  		
   	@Override
      		protected void onPostExecute(Void result) {
      			super.onPostExecute(result);

      			startListActivity(feed);
      		}
      	}
      	
      	//Method to append the current updated feed on top of already existing feedFile
      	
      	private void AppendFeed(RSSFeed data,RSSFeed data_old){
      		FileOutputStream fOut = null;
      		ObjectOutputStream osw = null;

      		try {
      			fOut = openFileOutput(fileNew, MODE_PRIVATE);
      			osw = new ObjectOutputStream(fOut);
      			
      		    osw.writeObject(data);
      		    osw.writeObject(data_old);
      		    
      			osw.flush();
      		}

      		catch (Exception e) {
      			e.printStackTrace();
      		}

      		finally {
      			try {
      				fOut.close();
      			} catch (IOException e) {
      				e.printStackTrace();
      			}
      		}
      		
      		
      		
      	}	
      	

      	// Method to write the feed to the File
      	private void WriteFeed(RSSFeed data) {

      		FileOutputStream fOut = null;
      		ObjectOutputStream osw = null;

      		try {
      			fOut = openFileOutput(fileName, MODE_PRIVATE);
      			osw = new ObjectOutputStream(fOut);
      			
      		    osw.writeObject(data);
      			osw.flush();
      		}

      		catch (Exception e) {
      			e.printStackTrace();
      		}

      		finally {
      			try {
      				fOut.close();
      			} catch (IOException e) {
      				e.printStackTrace();
      			}
      		}
      	}

      	// Method to read the feed from the File
      	private RSSFeed ReadFeed(String fName) {

      		FileInputStream fIn = null;
      		ObjectInputStream isr = null;

      		RSSFeed _feed = null;
      		File feedFile = getBaseContext().getFileStreamPath(fileName);
      		if (!feedFile.exists())
      			return null;

      		try {
      			fIn = openFileInput(fName);
      			isr = new ObjectInputStream(fIn);

      			_feed = (RSSFeed) isr.readObject();
      		}

      		catch (Exception e) {
      			e.printStackTrace();
      		}

      		finally {
      			try {
      				fIn.close();
      			} catch (IOException e) {
      				e.printStackTrace();
      			}
      		}

      		return _feed;

      	}

	
}