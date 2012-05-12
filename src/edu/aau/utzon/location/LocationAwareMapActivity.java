package edu.aau.utzon.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.SherlockMapActivity;

public abstract class LocationAwareMapActivity extends SherlockMapActivity{
	private static final String TAG = "LocationAwareMapActivity";
	private LocationManager mLocationManager = null;
	private LocationListener mLocationListener = null;
	private LocationHelper mLocationHelper = null;
	
	private String getBestProvider() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );
		String provider = mLocationManager.getBestProvider( criteria, true );
		if ( provider == null ) {
			Log.e(TAG, "No location provider found!" );
			return null;
		}
		return provider;
	}
	
	public LocationHelper getLocationHelper() {
		return mLocationHelper;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Init location manager
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Init location helper
		mLocationHelper = new LocationHelper(this);

		// Init location listener
		mLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				mLocationHelper.makeUseOfNewLocation(location);
				makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};
		
		// Request updates for the location listener
		mLocationManager.requestLocationUpdates(getBestProvider(), 0, 0, mLocationListener);
	}
	
	/** Clients should override this method to receive updates **/
	public abstract void makeUseOfNewLocation(Location location);

	@Override
	public void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(mLocationListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mLocationManager.requestLocationUpdates(getBestProvider(), 0, 0, mLocationListener);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// Required by MapActivity
		return false;
	}
}
