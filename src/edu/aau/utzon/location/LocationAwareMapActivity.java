package edu.aau.utzon.location;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockMapActivity;
import edu.aau.utzon.location.LocationService.SampleBinder;
import edu.aau.utzon.utils.CommonIntents;
import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.RestServiceHelper;

public abstract class LocationAwareMapActivity extends SherlockMapActivity implements ILocationAware{
	private static final String TAG = "LocationAwareMapActivity";
	private static final int PRELOAD_COUNT = 20;
	private LocationService mService = null;
	private boolean mBound = false;
	public boolean isBound() { return mBound; }

	public LocationService getLocationService() {
		if(mBound) {
			return mService;
		}
		return null;
	}

	
	private void promptUserNearPoi(PointModel poi) {
		final int poiId = poi.getId();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You are near a POI. Do you wish to see the content available?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				startActivity(CommonIntents.startPoiContentActivity(getBaseContext(), poiId));
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
		shownAlertId = mService.getLocationHelper().getCurrentClosePoi().getId();
	}

	private int shownAlertId = 0;
	public void serviceNewPoiBroadcast(PointModel poi) {
		// Avoid spamming the user with alert dialogs
		if(shownAlertId == 0 || (shownAlertId != mService.getLocationHelper().getCurrentClosePoi().getId())) {
			promptUserNearPoi(poi);
		}
	}

	boolean firstLocation = true;
	public void serviceNewLocationBroadcast(Location location) {
		mService.getLocationHelper().makeUseOfNewLocation(location);
		if(firstLocation && location != null) {
			RestServiceHelper.getServiceHelper().getNearestPoints(this, PRELOAD_COUNT, location);
			firstLocation = false;
		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			SampleBinder binder = (SampleBinder) service;
			mService = binder.getService();
			mBound = true;
			//serviceBoundEvent(mService);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
			//serviceDisconnectedEvent();
		}
	};
	

	

	// Handler for broadcast events (Poi notification)
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			Location loc = intent.getParcelableExtra(CommonIntents.EXTRA_LOCATION);
			PointModel poi = intent.getParcelableExtra(CommonIntents.EXTRA_NEAR_POI);
			if(loc != null) {
				Log.i(TAG, "Got location update");
				serviceNewLocationBroadcast(loc);
			}
			else if(poi != null) {
				Log.i(TAG, "Got near POI update");
				serviceNewPoiBroadcast(poi);
			}
			else {
				throw new IllegalArgumentException("Passing intent to onReceive must provide EXTRA_LOCATION or EXTRA_NEAR_POI");
			}
		}
	};

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// Register to receive broadcasts from LocationAwareService
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, 
				new IntentFilter(CommonIntents.POI_INTENTFILTER));
		if(!mBound) {
			// Bind to LocalService
			Intent intent = new Intent(this, LocationService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(!mBound) {
			Intent intent = new Intent(this, LocationService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}




}

//	private static final String TAG = "LocationAwareMapActivity";
//	protected SampleService mService = null;
//	private boolean mBound = false;
//	public boolean isBound() { return mBound; }
//	
//	abstract public void serviceBoundEvent(SampleService service);
//	abstract public void serviceDisconnectedEvent();
//	
//	/** Defines callbacks for service binding, passed to bindService() */
//	private ServiceConnection mConnection = new ServiceConnection() {
//
//		@Override
//		public void onServiceConnected(ComponentName className, IBinder service) {
//			// We've bound to LocalService, cast the IBinder and get LocalService instance
//			SampleBinder binder = (SampleBinder) service;
//			mService = binder.getService();
//			mBound = true;
//			serviceBoundEvent(mService);
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName arg0) {
//			mBound = false;
//			serviceDisconnectedEvent();
//		}
//	};	
//	
//	@Override
//	protected void onStart() {
//		super.onStart();
//		// Bind to LocalService
//		Intent intent = new Intent(this, SampleService.class);
//		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		// Unbind from the service
//		if (mBound) {
//			unbindService(mConnection);
//			mBound = false;
//		}
//	}
//	
//	
//}
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
