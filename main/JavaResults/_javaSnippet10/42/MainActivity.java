package com.musicsweeperpro;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.musicsweeperpro.file.FileHelper;
import com.musicsweeperpro.file.FilePathComparator;

public class MainActivity extends Activity implements OnItemClickListener  {

	  private View viewContainer;
		private static final int APP_ID = 0;
		private static final String TAG = "MainActivity";
		public static final String LIST_POS = "list_position";
		public static final String FILE_PATH = "file_path";
		public static List<String> groups = new ArrayList<String>();
		private FileHelper fileHelper = new FileHelper(getPrefs());
		
		private TextView percentField;
		private View progressBar;
		private Button cancelButton;
		private Button deleteButton;
		
		private int previousAudioListSize; 
		private ArrayAdapter<String> adapter = null;
		private boolean buildList = true;
		private boolean wasRotated = false;
		private ListView listView = null;
		private FilePathComparator fileComparator = new FilePathComparator();

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);

	    if (savedInstanceState == null && !wasRotated && buildList) {
			percentField = (TextView) findViewById(R.id.percent_field);
			percentField.setText(" Working!");
			percentField.setTextColor(android.graphics.Color.CYAN);
			progressBar = findViewById(R.id.progress);
			progressBar.setVisibility(View.VISIBLE);
		    
		    listView = (ListView) findViewById(R.id.listView);
		    listView.setEmptyView(findViewById(android.R.id.empty));
		    listView.setOnItemClickListener(
				new android.widget.AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
	//					Toast.makeText(getBaseContext(), "In the onclick event", Toast.LENGTH_SHORT).show();
						Intent fileListIntent = new Intent(getBaseContext(), FileListActivity.class);
						fileListIntent.putExtra(LIST_POS, position);
						startActivity(fileListIntent);
					}
			});
		    
	
			
		    FindFilesTask findFileTask = new FindFilesTask();
			findFileTask.execute(Environment.getExternalStorageDirectory().toURI(), new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/external_sd").toURI());	    

	    }
	  }
	  
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        	getActionBar().setSubtitle("Click folder to see files");
	        }
	    }
	    
	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        Log.d(TAG, "in onConfigurationChanged" );
	        wasRotated = true;
	        super.onConfigurationChanged(newConfig);
	    }	    

	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.activity_main, menu);
	    return true;
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    showUndo(viewContainer);
	    return true;
	  }

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			Toast.makeText(this, "In the onclick event", Toast.LENGTH_SHORT).show();
			Intent fileListIntent = new Intent(this, FileListActivity.class);
			fileListIntent.putExtra(LIST_POS, position);
			startActivity(fileListIntent);			
		}  
	  
/*	  @Override
	  public void onClick(View view) {
		  Toast.makeText(this, "In the onclick event", Toast.LENGTH_SHORT).show();
			Intent fileListIntent = new Intent(this, FileListActivity.class);
			fileListIntent.putExtra(FileListActivity.LIST_POS, view.getId());
			startActivity(fileListIntent);		  
//	    Toast.makeText(this, "Deletion undone", Toast.LENGTH_LONG).show();
//	    viewContainer.setVisibility(View.GONE);
	  } */ 

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public static void showUndo(final View viewContainer) {
		  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			viewContainer.setVisibility(View.VISIBLE);
			viewContainer.setAlpha(1);
			viewContainer.animate().alpha(0.4f).setDuration(5000)
					.withEndAction(new Runnable() {

						@Override
						public void run() {
							viewContainer.setVisibility(View.GONE);
						}
					});
		  }
	  }

	    private SharedPreferences getPrefs() {
	    	SharedPreferences prefs = null;
	    	try {
	    	// Get the xml/settings.xml preferences
			prefs = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
	    	} catch(NullPointerException e) {
	    		//Not sure what to do or why this is happening
	    		//Log.e(TAG, "Could not get preferences", e);
	    	}
		
			return prefs;
		}

	    
	    @Override
	    public void onSaveInstanceState(Bundle savedInstanceState) {
	      super.onSaveInstanceState(savedInstanceState);
	      // Save UI state changes to the savedInstanceState.
	      // This bundle will be passed to onCreate if the process is
	      // killed and restarted.
/*	      savedInstanceState.putBoolean("MyBoolean", true);
	      savedInstanceState.putDouble("myDouble", 1.9);
	      savedInstanceState.putInt("MyInt", 1);*/
	      savedInstanceState.putStringArrayList("groups", (ArrayList<String>) groups);
	      savedInstanceState.putBoolean("buildList", false);
	      // etc.
	    }

	    @Override
	    public void onRestoreInstanceState(Bundle savedInstanceState) {
	      super.onRestoreInstanceState(savedInstanceState);
	      // Restore UI state from the savedInstanceState.
	      // This bundle has also been passed to onCreate.
/*	      boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
	      double myDouble = savedInstanceState.getDouble("myDouble");
	      int myInt = savedInstanceState.getInt("MyInt");
	      String myString = savedInstanceState.getString("MyString");*/
	      groups = savedInstanceState.getStringArrayList("groups");
	      buildList = savedInstanceState.getBoolean("buildList");
	    } 
	    
	    
		public class FindFilesTask extends AsyncTask<URI, Integer, Long> {
					
		    private static final String STOP_SPINNER = "stopSpinner";
			private static final String START_SPINNER = "startSpinner";
			private static final String UPDATE_SPINNER = "updateSpinner";
			private static final String CANCEL_SPINNER = "cancelSpinner";

			protected Long doInBackground(URI... uris) {
		    	updateUI(START_SPINNER, null);
		        int count = uris.length;
		        long totalSize = 0;
		        Set<String> folderSet = new HashSet<String>();
		        for (int i = 0; i < count; i++) {
		        	folderSet.addAll(fileHelper.getAudioFolderPaths(uris[i]));
		            totalSize += folderSet.size();
		            publishProgress((int) ((i / (float) count) * 100));
		            // Escape early if cancel() is called
		            if (isCancelled()) break;
		        }
		        groups.clear();
		        groups.addAll(folderSet);
		        return totalSize;
		    }

		    protected void onProgressUpdate(Integer... values) {
		        super.onProgressUpdate(values);
//				leftFlingEnabeled = false;
//				progressBar.setVisibility(View.VISIBLE);
//				cancelButton.setVisibility(View.VISIBLE);
//				deleteButton.setVisibility(View.INVISIBLE);
				try {
					if (values != null) {
						updateUI(UPDATE_SPINNER, new Long(values[0]));
					}
				} catch(Exception e) {
					Log.e(TAG, "Failure trying to update percentField", e);
				}
//				refreshView();Don't think I want to do this anymore. Instead only show number of files being retreived.

			}
		    
			// -- called if the cancel button is pressed
			@Override
			protected void onCancelled() {
				super.onCancelled();
				updateUI(CANCEL_SPINNER, null);
				Log.d(TAG, "onCancelled()");
			}

			// -- called as soon as doInBackground method completes
			// -- notice that the third param gets passed to this method
			@Override
			protected void onPostExecute(Long result) {
				super.onPostExecute(result);
				Log.d(TAG, "onPostExecute(): " + result);
				updateUI(STOP_SPINNER, result);
				/*	
				cancelButton.setVisibility(View.INVISIBLE);			
				deleteButton.setVisibility(View.VISIBLE);*/
//				removeFilesNotFoundFromFileMap();//Has to be here for hardrefresh or deleted items reappear TODO: figure out why!
//				refreshView();
			}

			@SuppressLint("NewApi")
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			protected void startProgressSpinner() {
				//Variables used by MusicFileTask
				percentField = (TextView) findViewById(R.id.percent_field);
				percentField.setText(" Working!");
				percentField.setTextColor(android.graphics.Color.CYAN);	
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					getActionBar().setCustomView(percentField);
				}
			}

			@SuppressLint("NewApi")
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			protected void stopProgressSpinner(Long result) {				
				try {
//					Collections.sort(groups, fileComparator);
					previousAudioListSize = groups.size();
					percentField.setText(" " + previousAudioListSize + " " + result);
				} catch(Exception e) {
					percentField.setText(" " + result);
				}
				
				if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
				percentField = (TextView) findViewById(R.id.percent_field);
				percentField.setTextColor(0xFF69adea);
				percentField.setText("Done");
				adapter.notifyDataSetChanged();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					getActionBar().setCustomView(percentField);
					getActionBar().setSubtitle("Done! Click folder to see files");
				}
			}

			@SuppressLint("NewApi")
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			protected void cancelProgressSpinner() {
				percentField.setText(" Cancelled!");
				percentField.setTextColor(0xFFFF0000);
				progressBar.setVisibility(View.INVISIBLE);
//				cancelButton.setVisibility(View.INVISIBLE);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					getActionBar().setSubtitle("Click folder to see files");
				}
			}
			
			protected void updateProgressSpinner(Long size) {
				previousAudioListSize = size.intValue();
				percentField.setText(previousAudioListSize + " files");
				percentField.setTextColor(android.graphics.Color.GREEN);	
				Log.i(TAG, "onProgressUpdate(): " + String.valueOf(previousAudioListSize));
//				getActionBar().setCustomView(percentField);
			}			
			
			private void updateUI(final String action, final Long result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	if (START_SPINNER.equals(action)) {
                    		startProgressSpinner();
                    	} else if (STOP_SPINNER.equals(action)) {
                    		stopProgressSpinner(result);
                    	} else if (UPDATE_SPINNER.equals(action)) {
                     		updateProgressSpinner(result);
                    	} else if (CANCEL_SPINNER.equals(action)) {
                     		cancelProgressSpinner();
                     	}
                    	
                    	//create the listview
            		    Collections.sort(groups, fileComparator);
            			adapter = new ArrayAdapter<String>(getBaseContext(),
            		        android.R.layout.simple_list_item_1, groups);
            		    viewContainer = findViewById(R.id.undobar);
            		    listView.setAdapter(adapter);                    	
                    }
                });
			}
		}	  
/*	  private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		    // Called when the action mode is created; startActionMode() was called
		    @Override
		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		        // Inflate a menu resource providing context menu items
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.activity_main, menu);
		        return true;
		    }

		    // Called each time the action mode is shown. Always called after onCreateActionMode, but
		    // may be called multiple times if the mode is invalidated.
		    @Override
		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		        return false; // Return false if nothing is done
		    }

		    // Called when the user selects a contextual menu item
		    @Override
		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		        switch (item.getItemId()) {
		            case R.id.menu_share:
		                shareCurrentItem();
		                mode.finish(); // Action picked, so close the CAB
		                return true;
		            default:
		                return false;
		        }
		    }

		    // Called when the user exits the action mode
		    @Override
		    public void onDestroyActionMode(ActionMode mode) {
		        mActionMode = null;
		    }
		};	*/


}
