package app.velor.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import app.velor.ui.base.ActivityLifecycleListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Tracker implementation that tracks user location and moves camera map
 * accordingly.
 * 
 * @author Glenn Hall
 * 
 * 
 */
public class MyLocationTracker implements LocationListener,
		ActivityLifecycleListener {
	private static final String RECENTER_ON_TRACKING = "recenter.on.tracking";
	private static final String CAMERA_ZOOM = "camera.zoom";
	private static final String CAMERA_LONGITUDE = "camera.longitude";
	private static final String CAMERA_LATITUDE = "camera.latitude";

	public interface OnMyLocationListener {
		void onTrackingChanged(MyLocationTracker tracker, boolean tracking);
	}

	private int myLocationId;
	private int myLocationOnId;
	private LocationManager locationService;
	private Marker myLocation;
	private boolean recenterMap = false;
	private GoogleMap map = null;
	private List<OnMyLocationListener> listeners;
	private boolean tracking;
	private String provider;

	public MyLocationTracker(int myLocationId, int myLocationOnId,
			LocationManager locationService, GoogleMap map, String title) {
		super();
		this.myLocationId = myLocationId;
		this.myLocationOnId = myLocationOnId;
		this.locationService = locationService;
		this.map = map;

		MarkerOptions opts = new MarkerOptions().title(title)
				.position(new LatLng(0, 0))
				.icon(BitmapDescriptorFactory.fromResource(myLocationId))
				.anchor(0.5f, 1.0f);

		myLocation = map.addMarker(opts);
		myLocation.setVisible(false);
		listeners = new ArrayList<OnMyLocationListener>();
	}

	public void start() {
		Criteria criteria = new Criteria();
		provider = locationService.getBestProvider(criteria, false);

		locationService.requestLocationUpdates(provider, 10, 1, this);

		// when we ask for the location updates, if the provider is disabled the
		// onProviderDisabled event is raised, but if the provider is enabled,
		// no event is raised. add event manually.
		if (locationService.isProviderEnabled(provider)) {
			onProviderEnabled(provider);
		}

	}

	public void stop() {
		locationService.removeUpdates(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * app.velor.map.MyLocationTracker#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState, Activity activity) {
		CameraPosition position = map.getCameraPosition();
		outState.putDouble(CAMERA_LATITUDE, position.target.latitude);
		outState.putDouble(CAMERA_LONGITUDE, position.target.longitude);
		outState.putFloat(CAMERA_ZOOM, position.zoom);
		outState.putBoolean(RECENTER_ON_TRACKING, recenterMap);
	}

	/**
	 * Get the last know location from gps
	 * 
	 * @return
	 */
	protected LatLng getMyPosition() {
		LatLng result = null;

		Criteria criteria = new Criteria();
		String provider = locationService.getBestProvider(criteria, false);
		Location location = locationService.getLastKnownLocation(provider);

		if (location != null) {
			result = new LatLng(location.getLatitude(), location.getLongitude());
		}

		return result;
	}

	public void setMapRencenterOnTracking(boolean b) {
		recenterMap = b;
		if (recenterMap) {
			LatLng pos = getMyPosition();
			if (pos != null) {
				CameraUpdate update = CameraUpdateFactory.newLatLng(pos);
				map.animateCamera(update);
				myLocation.setIcon(BitmapDescriptorFactory
						.fromResource(myLocationOnId));
			}

		} else {
			myLocation.setIcon(BitmapDescriptorFactory
					.fromResource(myLocationId));
		}
	}

	public void toggleMapRencenterOnTracking() {
		setMapRencenterOnTracking(!recenterMap);
	}

	public boolean isMapRencenterOnTracking() {
		return recenterMap;
	}

	public void onLocationChanged(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		myLocation.setPosition(latLng);

		if (recenterMap) {
			CameraUpdate update = CameraUpdateFactory.newLatLng(latLng);
			map.moveCamera(update);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * app.velor.map.MyLocationTracker#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState,
			Activity activity) {
		CameraUpdate update = null;

		// maybe the app is route_id time run, then go to the last known
		// location
		LatLng myPos = getMyPosition();

		if (savedInstanceState != null && myPos != null) {
			recenterMap = savedInstanceState.getBoolean(RECENTER_ON_TRACKING,
					false);

			double latitude = savedInstanceState.getDouble(CAMERA_LATITUDE,
					myPos.latitude);
			double longitude = savedInstanceState.getDouble(CAMERA_LONGITUDE,
					myPos.longitude);
			float zoom = savedInstanceState.getFloat(CAMERA_ZOOM, 11);

			LatLng last = new LatLng(latitude, longitude);
			update = CameraUpdateFactory.newLatLngZoom(last, zoom);
		} else if (myPos != null) {
			// FIXME some configs for zoom here
			update = CameraUpdateFactory.newLatLngZoom(myPos, 11);
		}

		if (update != null) {
			map.animateCamera(update);
			myLocation.setPosition(myPos);

			if (recenterMap) {
				myLocation.setIcon(BitmapDescriptorFactory
						.fromResource(myLocationOnId));
			} else {
				myLocation.setIcon(BitmapDescriptorFactory
						.fromResource(myLocationId));
			}
			myLocation.setVisible(true);
		}

		else {
			// FIXME some configs for latlong here
			update = CameraUpdateFactory.newLatLngZoom(new LatLng(48.57106,
					-72.00211), 9);
			map.animateCamera(update);
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		myLocation.setVisible(false);
		tracking = false;
		for (OnMyLocationListener listener : listeners) {
			listener.onTrackingChanged(this, false);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		myLocation.setVisible(true);
		tracking = true;
		for (OnMyLocationListener listener : listeners) {
			listener.onTrackingChanged(this, true);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see app.velor.map.ActivityLifecycle#onResume()
	 */
	@Override
	public void onResume(Activity activity) {
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see app.velor.map.ActivityLifecycle#onPause()
	 */
	@Override
	public void onPause(Activity activity) {
		stop();
	}

	public void addOnMyLocationListener(OnMyLocationListener listener) {
		listeners.add(listener);
	}

	public void removeOnMyLocationListener(OnMyLocationListener listener) {
		listeners.remove(listener);
	}

	public boolean isTracking() {
		return tracking;
	}

	@Override
	public void onCreate(Bundle savedInstanceState, Activity activity) {
	}

	@Override
	public boolean onBackPressed(Activity activity) {
		return false;
	}

	public LatLng getLocation() {
		return myLocation.getPosition();
	}
}
