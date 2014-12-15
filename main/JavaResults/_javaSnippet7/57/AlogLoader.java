package loaders;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lucic.khalique.Runescape.Alog;
import lucic.khalique.Runescape.MyXMLHandler;
import lucic.khalique.Runescape.XMLList;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import utilities.DialogUtility;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class AlogLoader extends AsyncTask<String,Void, String>
{
	Exception exception;
	XMLList alogList = null;
	Dialog loadingDialog;
	String userName = "";
	Context screen;
	public AlogLoader(String userName, Context con)
	{
		this.userName = userName;
		screen = con;
		loadingDialog = new Dialog(screen);
	}
	protected void onPreExecute()
	{
		String name = userName.replace("+", " ");
		loadingDialog = DialogUtility.createLoadingScreen("Loading " + userName + " Adventurer's Log.. ", screen);
		loadingDialog.show();
	}
	protected String doInBackground(String... params)
	{
		while(!isCancelled())
		{
			try 
			{
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				userName = userName.replace(" ", "+");
				URL sourceUrl = new URL("http://services.runescape.com/m=adventurers-log/rssfeed?searchName=" + userName);
				xr.setContentHandler(new MyXMLHandler());
				xr.parse(new InputSource(sourceUrl.openStream()));

				alogList = MyXMLHandler.xMLList;
				return "completed";
			} 
			catch(ConnectException ce)
			{
				ce.printStackTrace();
				return "ConnectException";
			}
			catch(UnknownHostException unh)
			{
				unh.printStackTrace();
				return "UnknownHostException";
			}
			catch(FileNotFoundException fnf)
			{
				fnf.printStackTrace();
				return "FileNotFoundException";
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				exception = e;
				return "incomplete";
			}
		}
		return "cancelled";
	}
	protected void onPostExecute(String result)
	{
		loadingDialog.dismiss();
		if(result.equals("completed"))
		{
			Intent in = new Intent(screen, Alog.class);
			Bundle b = new Bundle();
			b.putStringArrayList("titleList", alogList.getTitle());
			b.putStringArrayList("linkList", alogList.getLink());
			b.putStringArrayList("descriptionList", alogList.getDescription());
			b.putStringArrayList("dateList",alogList.getDate());
			b.putStringArrayList("categories", alogList.getCategory());
			in.putExtras(b);
			screen.startActivity(in);
		}
		else if(result.equals("UnknownHostException") || result.equals("ConnectException"))
		{
			Toast.makeText(screen, "Please double check your internet connection ..", Toast.LENGTH_LONG).show();
		}
		else if(result.equals("FileNotFoundException"))
		{
			Toast.makeText(screen, "Your Adventurer's Log cannot be found.\nAre you a member?", Toast.LENGTH_LONG).show();
		}
		else if(result.contains("incomplete"))
		{
			Toast.makeText(screen, "Adventurer's Log is unavailable, please try again later ..",Toast.LENGTH_LONG).show();
			DialogUtility.createBugReportDialog(screen, "HomeActivity", "AlogLoader AsyncTask doInBackground()", exception);
		}
	}
	@Override
	protected void onCancelled()
	{
		loadingDialog.dismiss();
		Toast.makeText(screen, "Adventurer's Log response from www.runescape.com is slow, please try again later ..",Toast.LENGTH_LONG).show();
	}
}
