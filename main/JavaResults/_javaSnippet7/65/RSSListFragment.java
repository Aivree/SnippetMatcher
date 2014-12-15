package be.toodee.android.alternews;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RSSListFragment extends Fragment{
	
	private ArticlesListsActivity contextActivity;
	
	private ListView articleListView;
	private ImageView refreshMenuIcon;
	private ProgressBar pbRefresh;
	private Animation smallAnimation;
	private ArrayList<Article> articleArray;
	
	// OVERRIDES OF METHODS FROM android.support.v4.app.Fragment
	
	@Override
	public void onAttach(Activity activity) {
		this.contextActivity = (ArticlesListsActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		smallAnimation = AnimationUtils.loadAnimation(contextActivity, R.anim.alpha_anim);
		this.refreshMenuIcon = contextActivity.getRefreshMenuIcon();
		this.pbRefresh = contextActivity.getPbRefresh();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_layout, container, false);
		this.articleListView = (ListView) view.findViewById(R.id.list);
		System.out.println(this.articleListView);
		articleArray = ((InvestigactionApp)contextActivity.getApplication()).getListeRSSArticles();
		if (articleArray != null){
			ArticleArrayAdapter listAdapter = new ArticleArrayAdapter(contextActivity, R.layout.affichageitem, android.R.layout.simple_list_item_1);
			for (Article a : articleArray){
				listAdapter.add(a);
			}
			this.articleListView.setAdapter(listAdapter);
			this.articleListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				   Intent intent = new Intent(contextActivity, ArticleActivity.class);
				   intent.putExtra("be.toodee.position", position);
				   startActivityForResult(intent, 01);
				}});
		}else{
			if (((InvestigactionApp)contextActivity.getApplication()).isNetworkAvailable()){
				new RefreshFromRSSFeed().execute("http://feeds.feedburner.com/michelcollon/rss");
			}else{
				showNoInternetAlertDialog();
			}	
		}
		
		this.refreshMenuIcon.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
				refreshMenuIcon.startAnimation(smallAnimation);
				if (((InvestigactionApp)contextActivity.getApplication()).isNetworkAvailable()){
					new RefreshFromRSSFeed().execute("http://feeds.feedburner.com/michelcollon/rss");
				}else{
					showNoInternetAlertDialog();
				}
		    }
		});

		return view;
	}
	
	public void showNoInternetAlertDialog(){
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextActivity);
	alertDialogBuilder.setTitle(R.string.no_net_title);
	alertDialogBuilder.setMessage(R.string.no_net_text);
	alertDialogBuilder.setCancelable(false);
	alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,int id) {
			startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
		}
	});
	alertDialogBuilder.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,int id) {dialog.cancel();}});
	AlertDialog alertDialog = alertDialogBuilder.create();
	alertDialog.show();
}
	
	
	// ASYNC TASK TO DOWNLOAD RSS FEED
	
	private class RefreshFromRSSFeed extends AsyncTask<String, Void, ArrayList<Article>> {
		
		@Override
		protected void onPreExecute() {
			refreshMenuIcon.setVisibility(View.INVISIBLE);
			pbRefresh.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<Article> doInBackground(String... params) {
			
			ArrayList<Article> resultat = new ArrayList<Article>();
			try {
				URL rssUrl = new URL(params[0]);
				SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
				SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
				XMLReader myXMLReader = mySAXParser.getXMLReader();
				ArticlesRSSHandler myRSSHandler = new ArticlesRSSHandler();
				myXMLReader.setContentHandler(myRSSHandler);
				InputSource myInputSource = new InputSource(rssUrl.openStream());
				myXMLReader.parse(myInputSource);
				resultat = myRSSHandler.getListItems();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			InvestigactionApp.getInstance().setListeRSSArticles(resultat);
			return resultat;
		}

		@Override
		protected void onPostExecute(ArrayList<Article> result) {
			super.onPostExecute(result);
			ArticleArrayAdapter listAdapter = new ArticleArrayAdapter(contextActivity, R.layout.affichageitem, android.R.layout.simple_list_item_1);
			for (Article a : result){
				listAdapter.add(a);
			}
			articleListView.setAdapter(listAdapter);
			articleListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(contextActivity, ArticleActivity.class);
					intent.putExtra("be.toodee.position", position);
					startActivity(intent);
				}});
			pbRefresh.setVisibility(View.INVISIBLE);
			refreshMenuIcon.setVisibility(View.VISIBLE);
			Toast toast = Toast.makeText(contextActivity, R.string.refresh_toast, Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
}
