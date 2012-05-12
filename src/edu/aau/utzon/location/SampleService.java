package edu.aau.utzon.location;

import java.util.List;
import java.util.Random;

import edu.aau.utzon.SettingsActivity;
import edu.aau.utzon.utils.CommonIntents;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SampleService extends Service {
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
		public SampleService getService() {
			return SampleService.this;
		}

		public LocationHelper getLocationHelper() {
			return mLocationHelper;
		}
	}

	LocationHelper mLocationHelper = null;
	LocationListener mLocationListener = null;
	LocationManager mLocationManager = null;
	private void initState(Context context) {
		mLocationHelper = new LocationHelper(context);
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

			@Override

			public void onLocationChanged(Location location) {
				Location old = mLocationHelper.getCurrentLocation();
				mLocationHelper.makeUseOfNewLocation(location);
				if(mLocationHelper.isNearPoi()) {
					broadcastNearPoi();
				}

				if(old != mLocationHelper.getCurrentLocation()) {
					broadcastLocationUpdate();
				}

			}
		};
	}

	protected void broadcastNearPoi() {
		Log.d(TAG, "broadcastNearPoi()");
		LocalBroadcastManager.getInstance(this)
		.sendBroadcast(CommonIntents.broadcastNearPoi(this, mLocationHelper.getCurrentClosePoi()));
	}

	protected void broadcastLocationUpdate() {
		Log.i(TAG, "broadcastLocationUpdate()");
		LocalBroadcastManager.getInstance(this)
		.sendBroadcast(CommonIntents.broadcastLocationUpdate(this, mLocationHelper.getCurrentLocation()));
	}

	private void disableLocationListener() {
		Log.i(TAG, "disableLocationListener()");
		mLocationManager.removeUpdates(mLocationListener);
	}

	private String getBestProvider() {
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		return mLocationManager.getBestProvider(c, true);
	}

	private void enableLocationListener() {
		Log.i(TAG, "enableLocationListener()");
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(getBestProvider(), 0, 0, mLocationListener);
	}

	/** Should only be called in entry activity for the application **/
	//	@Override
	//	public int onStartCommand(Intent intent, int flags, int startID) {
	//		Log.i(TAG, "onStartCommand(intent, " + flags + startID);
	//		enableLocationListener();
	//		// TODO:
	//		while(mLocationHelper.getCurrentLocation() == null) {}
	//	    // We want this service to continue running until it is explicitly
	//	    // stopped, so return sticky.
	//	    return START_STICKY;
	//
	//	}

	@Override
	public void onCreate() {
		super.onCreate();
		initState(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind()");
		enableLocationListener();
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		disableLocationListener();
	}

	/** method for clients */
	public int getRandomNumber() {
		Log.d(TAG, "getRandomNumber()");
		return mGenerator.nextInt(100);
	}

	public LocationHelper getLocationHelper() {
		Log.d(TAG, "getLocationHelper()");
		return mLocationHelper;
	}



}