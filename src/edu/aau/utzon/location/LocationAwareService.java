//package edu.aau.utzon.location;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import edu.aau.utzon.R;
//import edu.aau.utzon.SettingsActivity;
//import edu.aau.utzon.webservice.JRestMethod;
//import edu.aau.utzon.webservice.PointModel;
//import edu.aau.utzon.webservice.RestServiceHelper;
//import android.app.IntentService;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Messenger;
//import android.os.PowerManager.WakeLock;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//import android.widget.TextView;
//
//public class LocationAwareService {
//
//	private static final String TAG = "LocationHelperService";
//	public static final String COMMAND = "COMMAND";
//
//	/** Use RestService instead **/
//	//public static final int COMMAND_GET_POI_ALL = 0;
//
//
//	//public static final int COMMAND_GET_POI_ID = 1;
//
//	/** Use RestService instead **/
//	//public static final int COMMAND_GET_POI_K = 2;
//
//	/** Clients must supply Location of user location update **/
//	public static final int COMMAND_UPDATE_LOCATION = 3;
//	public static final int COMMAND_ONCREATE = 4;
//	public static final int COMMAND_ONRESUME = 5;
//	public static final int COMMAND_ONPAUSE = 6;
//	static final int COMMAND_REGISTER_CLIENT = 7;
//	static final int COMMAND_UNREGISTER_CLIENT = 8;
//
//	/** Clients must provide (Int) BROADCAST_EXTRAS_POI_ID and/or (Location) BROADCAST_EXTRAS_LOCATION **/
//	public static final String BROADCAST_INTENTFILTER= "poi_notification";
//	public static final int BROADCAST_ISNEAR_POI = 9;
//	public static final int BROADCAST_LOCATION_UPDATE = 10;
//	public static final String BROADCAST_EXTRAS_LOCATION = "EXTRAS_LOCATION";
//	public static final String BROADCAST_EXTRAS_POI_ID = "EXTRAS_POI_ID";
//	//public static final int BROADCAST_EXTRAS_LOCATION = "LOCATION";
//	//	public static final String BROADCAST_EXTRAS_POI_ID = "_ID";
//
//	LocationHelper mLocationHelper = null;
//	LocationListener mLocationListener = null;
//	private Handler mMainThreadHandler = null;
//	
//
//
//	public LocationAwareService() {
//		super(TAG);
//		mMainThreadHandler  = new Handler();
//		
//
//	}	
//
//	boolean isFirstRun = true;
//	@Override
//	protected void onHandleIntent(Intent intent) {
//		Bundle bundle = intent.getExtras();
//		int command = bundle.getInt(COMMAND);
//
//		if(isFirstRun) {
//			mLocationHelper = new LocationHelper(this);
//			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			Criteria criteria = new Criteria();
//			criteria.setAccuracy( Criteria.ACCURACY_FINE );
//			provider = mLocationManager.getBestProvider( criteria, true );
//			if ( provider == null ) {
//				Log.e(TAG, "No location provider found!" );
//			}
//			mLocationListener = new LocationListener() {
//				public void onLocationChanged(Location location) {
//					makeUseOfNewLocation(location);
//				}
//
//				public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//				public void onProviderEnabled(String provider) {}
//
//				public void onProviderDisabled(String provider) {}
//			};
//			isFirstRun = false;
//		}
//		switch (command) {
//		/* Activity lifecycle commands */
//		case COMMAND_ONCREATE:
//			Log.i(TAG, "COMMAND_ONCREATE");
//			enableLocationListener();
//			break;
//		case COMMAND_ONPAUSE:
//			Log.i(TAG, "COMMAND_ONPAUSE");
//			disableLocationListener();
//			break;
//		case COMMAND_ONRESUME:
//			Log.i(TAG, "COMMAND_ONRESUME");
//			enableLocationListener();
//			break;
//		default:
//			Log.e(TAG, "Invalid command");
//		}
//	}
//
//	/* Location Listener logic */
//	private LocationManager mLocationManager = null;
//	//private LocationListener mLocationListener = null;
//
//	private String getBestProvider() {
//		return provider;
//	}
//
//	private void disableLocationListener() {
//		mLocationManager.removeUpdates(mLocationListener);
//	}
//
//	private void enableLocationListener() {
//		mLocationManager.requestLocationUpdates(getBestProvider(), 0, 0, mLocationListener);
//	}
//
//	public static final String PREFS_POI_PROXIMITY = "proximity";
//	private void makeUseOfNewLocation(Location location) {
//		mLocationHelper.makeUseOfNewLocation(location);
//		SharedPreferences preferenceMngr = getSharedPreferences(PREFS_POI_PROXIMITY, MODE_PRIVATE);
//		int proximity = preferenceMngr.getInt("proximity", 20);
//		double distance = mLocationHelper.distToPoi(mLocationHelper.getCurrentClosePoi());
//		// User is near a POI, send a broadcast
//		if(distance < proximity) {
//			userIsNearPoi(mLocationHelper.getCurrentClosePoi());
//		}
//		else {
//			userLocUpdated(mLocationHelper.getCurrentLocation());
//		}
//	}
//
//	public void userLocUpdated(Location currentLocation) {	
//		// Send broadcast
//		LocalBroadcastManager.getInstance(this)
//		.sendBroadcast(new Intent(BROADCAST_INTENTFILTER)
//		.putExtra(COMMAND, BROADCAST_LOCATION_UPDATE)
//		.putExtra(BROADCAST_EXTRAS_LOCATION, mLocationHelper.getCurrentLocation()));
//
//	}
//
//	public void userIsNearPoi(PointModel poi) {
//		// Send broadcast
//		LocalBroadcastManager.getInstance(this)
//		.sendBroadcast(new Intent(BROADCAST_INTENTFILTER)
//		.putExtra(COMMAND, BROADCAST_ISNEAR_POI)
//		.putExtra(BROADCAST_EXTRAS_POI_ID, mLocationHelper.getCurrentClosePoi().getId()));
//	}
//}
