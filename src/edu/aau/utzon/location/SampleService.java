package edu.aau.utzon.location;

import java.util.List;
import java.util.Random;

import android.app.IntentService;
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
		SampleService getService() {
			return SampleService.this;
		}

		LocationHelper getLocationHelper() {
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
				mLocationHelper.makeUseOfNewLocation(location);				
			}
		};
	}

	private void disableLocationListener() {
		mLocationManager.removeUpdates(mLocationListener);
	}
	
	private String getBestProvider() {
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		return mLocationManager.getBestProvider(c, true);
	}
	
	private void enableLocationListener() {
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(getBestProvider(), 0, 0, mLocationListener);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		initState(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		enableLocationListener();
		
		return mBinder;
	}

	/** method for clients */
	public int getRandomNumber() {
		return mGenerator.nextInt(100);
	}

	public LocationHelper getLocationHelper() {
		Log.d(TAG, "getLocationHelper()");
		return mLocationHelper;
	}

}