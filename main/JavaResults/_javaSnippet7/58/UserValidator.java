package loaders;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lucic.khalique.Runescape.FriendsActivity;
import lucic.khalique.Runescape.HomeActivity;
import lucic.khalique.Runescape.MyXMLHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import utilities.DialogUtility;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class UserValidator extends AsyncTask<String, Void, String>
{
	Exception exception;
	Dialog loadingDialog;
	Context screen;
	String username;
	int activityType = 0; // 0 is registration, 1 is friendsactivity
	public UserValidator(String username, Context con)
	{
		this.username = username;
		screen = con;
		loadingDialog = new Dialog(con);
		if(screen.toString().contains("FriendsActivity"))
		{
			activityType = 1;
		}
	}
	protected void onPreExecute()
	{
		loadingDialog = DialogUtility.createLoadingScreen("Validating " + username + " ..", screen);
		loadingDialog.show();
	}
	protected String doInBackground(String... params)
	{
		while(!isCancelled())
		{
			try 
			{
				//Handle XML
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				String username1 = username.replace(" ","+");
				URL sourceUrl = new URL("http://services.runescape.com/m=adventurers-log/rssfeed?searchName=" + username1); //Send URL to Parse XML Tags
				xr.setContentHandler(new MyXMLHandler());
				xr.parse(new InputSource(sourceUrl.openStream()));
				return "completed";
			} 
			catch(ConnectException ce)
			{
				ce.printStackTrace();
				return "ConnectException";
			}
			catch(FileNotFoundException fnf)
			{
				fnf.printStackTrace();
				return "FileNotFoundException";
			}
			catch(UnknownHostException unh)
			{
				unh.printStackTrace();
				return "UnknownHostException";
			}
			catch (Exception e) 
			{
				exception = e;
				e.printStackTrace();
				return "incomplete: " + e.toString();
			}
		}
		return "cancelled";
	}
	protected void onPostExecute(String result)
	{
		Intent intent;
		Bundle b;
		SharedPreferences settings = screen.getSharedPreferences("SETTINGS",0);
		SharedPreferences.Editor editor = settings.edit();
		loadingDialog.dismiss();
		if(result.equals("completed"))
		{
			if(activityType == 0)
			{
				intent = new Intent(screen, HomeActivity.class);
				b = new Bundle();
				b.putString("memberType","pay2play");
				b.putString("memberName", username);
				intent.putExtras(b);
				editor.putString("memberType", "pay2play");
				editor.putString("memberName", username);
				editor.putString("loginCherryPopped" , "yes");
				editor.putString("registered", "yes");
				editor.commit();
				screen.startActivity(intent);
			}
			else
			{
				String friendsString = settings.getString("friends", null);
	    		if(friendsString == null)//no friends
	    		{
	    			friendsString = "";
	    			friendsString = friendsString.concat(username);
	    		}
	    		else
	    		{
	    			friendsString = friendsString.concat("," + username);
	    		}
	     		editor.putString("friends", friendsString);
	     		editor.commit();
	     		intent = new Intent(screen, FriendsActivity.class);
	     		screen.startActivity(intent);
	     		((Activity) screen).finish();
			}
		}
		else if(result.equals("FileNotFoundException"))
		{
			if(activityType == 0)
			{
				Toast.makeText(screen, "User Validation failed.\nHave you entered your username correctly?\nAre you a member?", Toast.LENGTH_LONG).show();
			}
			else if(activityType == 1)
			{
				Toast.makeText(screen, "Friend Validation failed.\nHave you entered their username correctly?\nAre they a member?", Toast.LENGTH_LONG).show();
			}
		}
		else if(result.equals("UnknownHostException") || result.equals("ConnectException"))
		{
			Toast.makeText(screen, "Please double check your internet connection ..", Toast.LENGTH_LONG).show();
		}
		else if(result.contains("incomplete"))
		{
			if(activityType == 0)
			{
				Toast.makeText(screen, "Unable to validate user, please try again later ..", Toast.LENGTH_LONG).show();
			}
			else if(activityType == 1)
			{
				Toast.makeText(screen, "Unable to validate friend, please try again later ..", Toast.LENGTH_LONG).show();
			}
			DialogUtility.createBugReportDialog(screen, "UserValidator", "UserValidator AsyncTask doInBackground()", exception);
		}
	}
	@Override
	protected void onCancelled()
	{
		loadingDialog.dismiss();
		Toast.makeText(screen, "Validation from www.runescape.com is slow, please try again later ..", Toast.LENGTH_LONG).show();
	}
}
