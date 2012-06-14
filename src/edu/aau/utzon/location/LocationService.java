package edu.aau.utzon.location;

import java.util.Random;

import edu.aau.utzon.utils.CommonIntents;
import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.RestServiceHelper;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationService extends Service implements ILocationAware{
	private static final String TAG = "SampleService";
	// Binder given to clients
	private final IBinder mBinder = new SampleBinder();
	// Random number generator
	private final Random mGenerator = new Random();

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class SampleBinder extends Binder {
		public LocationService getService() {
			return LocationService.this;
		}

		public LocationHelper getLocationHelper() {
			return mLocationHelper;
		}
	}

	LocationHelper mLocationHelper = null;
	LocationListener mLocationListener = null;
	//LocationManager mLocationManager = null;
	private void initState() {
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationHelper = new LocationHelper(this);
		mLocationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			//boolean isFirstLocation = true;

			@Override
			public void onLocationChanged(Location location) {
				if(location != null) {
					mLocationHelper.makeUseOfNewLocation(location);
					if(location == mLocationHelper.getCurrentLocation()) {
						serviceNewLocationBroadcast(location);
					}
					if(mLocationHelper.isNearPoi()) {
						serviceNewPoiBroadcast(mLocationHelper.getCurrentClosePoi());
					}
				}
			}
		};
	}

	@Override
	public void serviceNewPoiBroadcast(PointModel poi) {
		Log.d(TAG, "serviceNewPoiBroadcast()");
		LocalBroadcastManager.getInstance(this)
		.sendBroadcast(CommonIntents.broadcastNearPoi(this, poi));
	}

	boolean isFirstLocation = true;
	@Override
	public void serviceNewLocationBroadcast(Location location) {
		Log.i(TAG, "serviceNewLocationBroadcast()");
		LocalBroadcastManager.getInstance(this)
		.sendBroadcast(CommonIntents.broadcastLocationUpdate(this, mLocationHelper.getCurrentLocation()));
		
		if(isFirstLocation) {
			RestServiceHelper.getServiceHelper().getNearestPoints(this, 15, location);
			isFirstLocation = false;
		}
	}

	private void disableLocationListener() {
		Log.i(TAG, "disableLocationListener()");
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.removeUpdates(mLocationListener);
	}

	private String getBestProvider() {
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return mLocationManager.getBestProvider(c, true);
	}

	private void enableLocationListener() {
		Log.i(TAG, "enableLocationListener()");
		
		if(getBestProvider() != null) {
			LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(getBestProvider(), 0, 0, mLocationListener);
		}
	}

	/** Should only be called in entry activity for the application **/
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		super.onStartCommand(intent, flags, startID);
		Log.i(TAG, "onStartCommand");
		initState();
		return START_STICKY;
	}
//		@Override
//		public int onStartCommand(Intent intent, int flags, int startID) {
//			Log.i(TAG, "onStartCommand(intent, " + flags + startID);
//			initState();
//			//enableLocationListener();
//			// TODO:
//			//while(mLocationHelper.getCurrentLocation() == null) {}
//		    // We want this service to continue running until it is explicitly
//		    // stopped, so return sticky.
//		    return START_NOT_STICKY;
//	
//		}

//	@Override
//	public void onCreate() {
//		super.onCreate();
//		initState();
//		
//	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		enableLocationListener();
		return mBinder;
	}
	
	//Called when all clients have disconnected from a particular interface published by the service.
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind()");
		disableLocationListener();
		return false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w(TAG, "onDestroy()");
		disableLocationListener();
	}

	/** method for clients */
	public int getRandomNumber() {
		Log.d(TAG, "getRandomNumber()");
		return mGenerator.nextInt(100);
	}

	public LocationHelper getLocationHelper() {
		return mLocationHelper;
	}



}