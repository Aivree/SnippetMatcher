package planets.position;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import planets.position.database.LocationTable;
import planets.position.database.PlanetsContentProvider;
import planets.position.lunar.LunarEclipse;
import planets.position.lunar.LunarOccultation;
import planets.position.solar.SolarEclipse;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PlanetsMain extends ActionBarActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, FragmentListener,
		LocationDialog.LocationDialogListener {

	private static final int LOC_LOADER = 1;
	public static final String PREFS_NAME = "MyPrefsFile";

	private DrawerLayout mDrawerLayout;
	private ScrollView mDrawerExtra;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle, mTitle, navTitle = "";
	private TextView navHome, navSE, navLE, navLO, navSky, navUp, navLocation,
			navSettings, navAbout;
	private Toolbar toolbar;
	private FragmentManager fm;
	private FileCopyTask copyTask;
	private LocationTask locationTask;
	private int ioffset = -1, navPosition = 0;
	private double latitude, longitude, elevation, offset;
	private List<String> gmtValues;
	private ContentResolver cr;
	private String[] projection = { LocationTable.COLUMN_ID,
			LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE,
			LocationTable.COLUMN_ELEVATION, LocationTable.COLUMN_OFFSET,
			LocationTable.COLUMN_IOFFSET };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planets_main);
		mDrawerTitle = getTitle();
		navTitle = getTitle();

		navHome = (TextView) findViewById(R.id.navHome);
		navSE = (TextView) findViewById(R.id.navSE);
		navLE = (TextView) findViewById(R.id.navLE);
		navLO = (TextView) findViewById(R.id.navLO);
		navSky = (TextView) findViewById(R.id.navSky);
		navUp = (TextView) findViewById(R.id.navUp);
		navLocation = (TextView) findViewById(R.id.navLocation);
		navSettings = (TextView) findViewById(R.id.navSettings);
		navAbout = (TextView) findViewById(R.id.navAbout);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerExtra = (ScrollView) findViewById(R.id.listview_extra);
		toolbar = (Toolbar) findViewById(R.id.toolbar);

		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		navHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(0, false, true);
			}
		});

		navSE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(1, false, true);
			}
		});

		navLE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(3, false, true);
			}
		});
		navLO.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(4, false, true);
			}
		});

		navSky.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(5, false, true);
			}
		});
		navUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(6, false, true);
			}
		});

		navLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(7, false, true);
			}
		});
		navSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(8, false, true);
			}
		});

		navAbout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectItem(9, false, true);
			}
		});
		gmtValues = Arrays.asList(getResources().getStringArray(
				R.array.gmt_values));

		cr = this.getContentResolver();
		getSupportLoaderManager().initLoader(LOC_LOADER, null, this);

		PreferenceManager.setDefaultValues(this, R.xml.pref_display, false);

		// This gets the top fragment on the back stack and calls its onResume
		getSupportFragmentManager().addOnBackStackChangedListener(
				new FragmentManager.OnBackStackChangedListener() {
					public void onBackStackChanged() {
						FragmentManager manager = getSupportFragmentManager();
						Fragment fragment = null;
						if (manager != null) {
							int backStackEntryCount = manager
									.getBackStackEntryCount();
							if (backStackEntryCount == 0) {
								return;
							}
							List<Fragment> fragments = manager.getFragments();
							if (backStackEntryCount < fragments.size())
								fragment = fragments.get(backStackEntryCount);
							if (fragment != null)
								fragment.onResume();
						}
					}
				});

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				// Set the title on the action when drawer open
				getSupportActionBar().setTitle(mDrawerTitle);
				super.onDrawerOpened(drawerView);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			mTitle = mDrawerTitle;
			selectItem(0, false, false);
		}

		loadLocation();

		fm = getSupportFragmentManager();
		copyTask = (FileCopyTask) fm.findFragmentByTag("copyTask");
		locationTask = (LocationTask) fm.findFragmentByTag("locationTask");

		if (!(checkFiles("semo_18.se1") && checkFiles("sepl_18.se1"))) {
			if (copyTask == null) {
				// copy files task
				copyTask = new FileCopyTask();
				copyTask.setTask(copyTask.new CopyFilesTask());
				copyTask.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
				copyTask.show(fm, "copyTask");
			}
		} else {
			if (latitude < -90) {
				startLocationTask();
			}
		}

		// opens navigation drawer
		// mDrawerLayout.openDrawer(mDrawerList);
	}

	private void loadLocation() {
		int col;
		Cursor locCur = cr.query(Uri.withAppendedPath(
				PlanetsContentProvider.LOCATION_URI, String.valueOf(0)),
				projection, null, null, null);
		locCur.moveToFirst();
		col = locCur.getColumnIndex(LocationTable.COLUMN_LATITUDE);
		if (col > 0) {
			latitude = locCur.getDouble(col);
		} else {
			latitude = -91.0;
		}
		col = locCur.getColumnIndex(LocationTable.COLUMN_LONGITUDE);
		if (col > 0) {
			longitude = locCur.getDouble(col);
		} else {
			longitude = 0.0;
		}
		col = locCur.getColumnIndex(LocationTable.COLUMN_ELEVATION);
		if (col > 0) {
			elevation = locCur.getDouble(col);
		} else {
			elevation = 0.0;
		}
		col = locCur.getColumnIndex(LocationTable.COLUMN_IOFFSET);
		if (col > 0) {
			ioffset = locCur.getInt(col);
		} else {
			ioffset = -1;
		}
		col = locCur.getColumnIndex(LocationTable.COLUMN_OFFSET);
		if (col > 0) {
			offset = locCur.getDouble(col);
		} else {
			offset = 0.0;
		}
		locCur.close();
	}

	// Save location to database
	private boolean saveLocation() {

		long date = Calendar.getInstance().getTimeInMillis();

		ContentValues values = new ContentValues();
		values.put(LocationTable.COLUMN_LATITUDE, latitude);
		values.put(LocationTable.COLUMN_LONGITUDE, longitude);
		values.put(LocationTable.COLUMN_TEMP, 0.0);
		values.put(LocationTable.COLUMN_PRESSURE, 0.0);
		values.put(LocationTable.COLUMN_ELEVATION, elevation);
		values.put(LocationTable.COLUMN_DATE, date);
		values.put(LocationTable.COLUMN_OFFSET, offset);
		values.put(LocationTable.COLUMN_IOFFSET, ioffset);

		int rows = cr.update(Uri.withAppendedPath(
				PlanetsContentProvider.LOCATION_URI, String.valueOf(0)),
				values, null, null);
		if (rows == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putCharSequence("title", mTitle);
		outState.putInt("nav", navPosition);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		mTitle = savedInstanceState.getCharSequence("title");
		navPosition = savedInstanceState.getInt("nav");
		setActionNav(navPosition, mTitle);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case LocationLib.CONNECTION_FAILURE_RESOLUTION_REQUEST:
			// If the result code is Activity.RESULT_OK, try to connect again
			switch (resultCode) {
			case Activity.RESULT_OK:
				// Try the request again
				break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Open the menu if closed or close it if open
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mDrawerExtra)) {
				mDrawerLayout.closeDrawer(mDrawerExtra);
			} else {
				mDrawerLayout.openDrawer(mDrawerExtra);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void navigate(int position, boolean edit, boolean back) {
		selectItem(position, edit, back);
	}

	private void selectItem(int position, boolean edit, boolean back) {
		CharSequence title = "";
		Bundle args = new Bundle();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		// Locate Position
		switch (position) {
		case 0:
			title = "Planet\'s Position";
			ft.replace(R.id.content_frame, new Navigation());
			if (back)
				ft.addToBackStack(null);
			ft.commit();
			break;
		case 1:
			if (longitude == 0)
				loadLocation();
			title = "Solar Eclipse";
			SolarEclipse se = new SolarEclipse();
			args.putDouble("latitude", latitude);
			args.putDouble("longitude", longitude);
			args.putDouble("elevation", elevation);
			args.putDouble("offset", offset);
			se.setArguments(args);
			ft.replace(R.id.content_frame, se);
			if (back)
				ft.addToBackStack(null);
			ft.commit();
			break;
		// case 2:
		// title = "Solar Transit";
		// ft.replace(R.id.content_frame, new SolarTransit());
		// if (back)
		// ft.addToBackStack(null);
		// ft.commit();
		// break;
		case 3:
			title = "Lunar Eclipse";
			LunarEclipse le = new LunarEclipse();
			args.putDouble("latitude", latitude);
			args.putDouble("longitude", longitude);
			args.putDouble("elevation", elevation);
			args.putDouble("offset", offset);
			le.setArguments(args);
			ft.replace(R.id.content_frame, le);
			if (back)
				ft.addToBackStack(null);
			ft.commit();
			break;
		case 4:
			title = "Lunar Occultation";
			LunarOccultation lo = new LunarOccultation();
			args.putDouble("latitude", latitude);
			args.putDouble("longitude", longitude);
			args.putDouble("elevation", elevation);
			args.putDouble("offset", offset);
			lo.setArguments(args);
			ft.replace(R.id.content_frame, lo);
			if (back)
				ft.addToBackStack(null);
			ft.commit();
			break;
		case 5:
			if (longitude == 0)
				loadLocation();
			title = "Sky Position";
			ft.replace(R.id.content_frame, new SkyPosition());
			if (back)
				ft.addToBackStack(null);
			ft.commit();
			break;
		case 6:
			if (longitude == 0)
				loadLocation();
			title = "What's Up Now";
			WhatsUpNow whatsUp = new WhatsUpNow();
			args.putDouble("latitude", latitude);
			args.putDouble("longitude", longitude);
			args.putDouble("elevation", elevation);
			args.putDouble("offset", offset);
			whatsUp.setArguments(args);
			ft.replace(R.id.content_frame, whatsUp);
			if (back)
				ft.addToBackStack(null);
			ft.commit();
			break;
		case 7:
			title = "User Location";
			UserLocation userLoc = new UserLocation();
			args.putBoolean("edit", edit);
			userLoc.setArguments(args);
			ft.replace(R.id.content_frame, userLoc);
			if (back)
				ft.addToBackStack(null);
			ft.commit();
			break;
		case 8:
			title = "Settings";
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			break;
		case 9:
			title = navTitle;
			DialogFragment newFragment = new About(title, navPosition);
			newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			newFragment.show(getSupportFragmentManager(), "aboutDialog");
			break;
		}
		navPosition = position;
		navTitle = title;
		setActionNav(position, title);
		mDrawerLayout.closeDrawer(mDrawerExtra);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	/**
	 * Sets the title in the ActionBar and sets which item is selected.
	 * 
	 * @param index
	 *            the index of the item selected.
	 * @param title
	 *            the title for the ActionBar.
	 */
	public void setActionNav(int index, CharSequence title) {
		// reset styles of the nav drawer TextViews
		navHome.setTextAppearance(getApplicationContext(),
				R.style.NavDrawerMain);
		navHome.setBackgroundResource(R.color.drawer_background);
		navSE.setTextAppearance(getApplicationContext(), R.style.NavDrawerMain);
		navSE.setBackgroundResource(R.color.drawer_background);
		navLE.setTextAppearance(getApplicationContext(), R.style.NavDrawerMain);
		navLE.setBackgroundResource(R.color.drawer_background);
		navLO.setTextAppearance(getApplicationContext(), R.style.NavDrawerMain);
		navLO.setBackgroundResource(R.color.drawer_background);
		navSky.setTextAppearance(getApplicationContext(), R.style.NavDrawerMain);
		navSky.setBackgroundResource(R.color.drawer_background);
		navUp.setTextAppearance(getApplicationContext(), R.style.NavDrawerMain);
		navUp.setBackgroundResource(R.color.drawer_background);
		navLocation.setBackgroundResource(R.color.drawer_background);
		navSettings.setBackgroundResource(R.color.drawer_background);
		navAbout.setBackgroundResource(R.color.drawer_background);
		switch (index) {
		case 0:
			navHome.setTextAppearance(getApplicationContext(),
					R.style.NavDrawerMain_Selected);
			navHome.setBackgroundResource(R.color.drawer_background2);
			break;
		case 1:
			navSE.setTextAppearance(getApplicationContext(),
					R.style.NavDrawerMain_Selected);
			navSE.setBackgroundResource(R.color.drawer_background2);
			break;
		case 3:
			navLE.setTextAppearance(getApplicationContext(),
					R.style.NavDrawerMain_Selected);
			navLE.setBackgroundResource(R.color.drawer_background2);
			break;
		case 4:
			navLO.setTextAppearance(getApplicationContext(),
					R.style.NavDrawerMain_Selected);
			navLO.setBackgroundResource(R.color.drawer_background2);
			break;
		case 5:
			navSky.setTextAppearance(getApplicationContext(),
					R.style.NavDrawerMain_Selected);
			navSky.setBackgroundResource(R.color.drawer_background2);
			break;
		case 6:
			navUp.setTextAppearance(getApplicationContext(),
					R.style.NavDrawerMain_Selected);
			navUp.setBackgroundResource(R.color.drawer_background2);
			break;
		case 7:
			navLocation.setBackgroundResource(R.color.drawer_background2);
			break;
		case 8:
			navSettings.setBackgroundResource(R.color.drawer_background2);
			break;
		case 9:
			navAbout.setBackgroundResource(R.color.drawer_background2);
			break;
		default:
			break;
		}
		mTitle = title;
		getSupportActionBar().setTitle(title);
	}

	// ********************************
	// ***** Location dialog code *****
	// ********************************

	@Override
	public void onDialogPositiveClick() {
		// GPS
		if (locationTask == null) {
			locationTask = new LocationTask();
			locationTask.setTask(locationTask.new GetLocationTask());
			locationTask.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			locationTask.show(fm, "locationTask");
		}
	}

	@Override
	public void onDialogNegativeClick() {
		// Manual
		selectItem(7, true, true);
	}

	// *******************************************
	// ***** Location and File Copy callback *****
	// *******************************************

	@Override
	public void onTaskFinished(Location location, int index) {
		switch (index) {
		case 0:
			// Location Task
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				elevation = location.getAltitude();
				offset = Calendar.getInstance().getTimeZone()
						.getOffset(location.getTime()) / 3600000.0;
				if (gmtValues.contains(offset + "")) {
					ioffset = gmtValues.indexOf(offset + "");
				} else {
					ioffset = -1;
				}
				saveLocation();
			} else {
				Toast.makeText(getApplicationContext(), "No Location found.",
						Toast.LENGTH_LONG).show();
			}
			locationTask = null;
			break;
		case 1:
			// File Copy Task
			copyTask = null;
			startLocationTask();
			break;
		}
	}

	private void startLocationTask() {
		if (locationTask == null || !locationTask.isRunning()) {
			// No location
			DialogFragment newFragment = new LocationDialog();
			newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			newFragment.show(getSupportFragmentManager(), "locationDialog");
		}
	}

	/**
	 * Checks to see if the given file exists on the sdcard.
	 * 
	 * @param name
	 *            file name to check
	 * @return true if exists, false otherwise
	 */
	private boolean checkFiles(String name) {
		File sdCard = getApplicationContext().getExternalFilesDir(null);
		File f = new File(sdCard.getAbsolutePath() + "/ephemeris/" + name);
		return f.exists();
	}

	// ********************************
	// ***** SQLite database code *****
	// ********************************
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(this,
				PlanetsContentProvider.LOCATION_URI, projection, null, null,
				null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

}
