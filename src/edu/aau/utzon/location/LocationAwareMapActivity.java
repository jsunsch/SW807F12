package edu.aau.utzon.location;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.actionbarsherlock.app.SherlockMapActivity;

import edu.aau.utzon.location.SampleService.SampleBinder;

public abstract class LocationAwareMapActivity extends SherlockMapActivity{
	private static final String TAG = "LocationAwareMapActivity";
	protected SampleService mService = null;
	private boolean mBound = false;
	public boolean isBound() { return mBound; }
	
	abstract public void serviceBoundEvent(SampleService service);
	abstract public void serviceDisconnectedEvent();
	
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			SampleBinder binder = (SampleBinder) service;
			mService = binder.getService();
			mBound = true;
			serviceBoundEvent(mService);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
			serviceDisconnectedEvent();
		}
	};
	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		// Bind to LocalService
		Intent intent = new Intent(this, SampleService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
	
	
}
//
//import android.content.Context;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.util.Log;
//import com.actionbarsherlock.app.SherlockMapActivity;
//
//public abstract class LocationAwareMapActivity extends SherlockMapActivity{
//	private static final String TAG = "LocationAwareMapActivity";
//
////	private String getBestProvider() {
////		Criteria criteria = new Criteria();
////		criteria.setAccuracy( Criteria.ACCURACY_FINE );
////		String provider = mLocationManager.getBestProvider( criteria, true );
////		if ( provider == null ) {
////			Log.e(TAG, "No location provider found!" );
////			return null;
////		}
////		return provider;
////	}
//	
////	public LocationHelper getLocationHelper() {
////		return mLocationHelper;
////	}
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		// Init location manager
//		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		// Init location helper
//		mLocationHelper = new LocationHelper(this);
//
//		// Init location listener
//		mLocationListener = new LocationListener() {
//			public void onLocationChanged(Location location) {
//				mLocationHelper.makeUseOfNewLocation(location);
//				makeUseOfNewLocation(location);
//			}
//
//			public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//			public void onProviderEnabled(String provider) {}
//
//			public void onProviderDisabled(String provider) {}
//		};
//		
//		// Request updates for the location listener
//		mLocationManager.requestLocationUpdates(getBestProvider(), 0, 0, mLocationListener);
//	}
//	
//	/** Clients should override this method to receive updates **/
//	public abstract void makeUseOfNewLocation(Location location);
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		mLocationManager.removeUpdates(mLocationListener);
//	}
//	
//	@Override
//	public void onResume() {
//		super.onResume();
//		mLocationManager.requestLocationUpdates(getBestProvider(), 0, 0, mLocationListener);
//	}
//	
//	@Override
//	protected boolean isRouteDisplayed() {
//		// Required by MapActivity
//		return false;
//	}
//}
