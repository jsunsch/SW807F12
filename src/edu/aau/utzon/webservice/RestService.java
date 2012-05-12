package edu.aau.utzon.webservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class RestService extends IntentService {

	public static final String COMMAND = "COMMAND";
	
	public static final int COMMAND_GET_POI_ALL = 0;
	
	/** Client must supply (int)POI_ID **/
	public static final int COMMAND_GET_POI_ID = 1;
	public static final String EXTRAS_POI_ID = "_ID";
	
	/** Client must supply Location and (int)POI_K **/
	public static final int COMMAND_GET_POI_K = 2;
	public static final String EXTRAS_LOCATION = "_LOCATION";
	public static final String EXTRAS_LOCATION_LAT = "_LOCATION_LAT";
	public static final String EXTRAS_LOCATION_LONG = "_LOCATION_LONG";
	public static final String EXTRAS_POI_K = "_K";
	
	
	public RestService() {
		super("RestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		int command = bundle.getInt(COMMAND);

		switch (command) {
			case COMMAND_GET_POI_ALL:
				GetLocationPoints();
				break;
			case COMMAND_GET_POI_ID:
				GetLocationPoint(bundle.getInt(EXTRAS_POI_K));
				break;
			case COMMAND_GET_POI_K:
				//Location l 	= bundle.getParcelable(EXTRAS_LOCATION);
				int k 		= bundle.getInt(EXTRAS_POI_K);
				double lat	= bundle.getDouble(EXTRAS_LOCATION_LAT);
				double lg	= bundle.getDouble(EXTRAS_LOCATION_LONG);
				getNearestPoints(lg, lat, k);
				break;
		}
	}

	private static final int THIRTY_MINUTES = 1000 * 60 * 30;
	private void GetLocationPoint(int id) {
		JRestMethod.getPoint(this, id);		
	}

	private void GetLocationPoints() {
		JRestMethod.getAllPoints(this); 
	}

	private void getNearestPoints(double longitude, double latitude, int numberOfNearestNeighbours) {
		JRestMethod.getNearestPoints(this, longitude, latitude, numberOfNearestNeighbours);
	}
	
	/*
	 * @return boolean return true if the application can access the internet
	 */
//	private boolean haveInternet(){
//		NetworkInfo info = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
//		if (info==null || !info.isConnected()) {
//			return false;
//		}
//		if (info.isRoaming()) {
//			// here is the roaming option you can change it if you want to disable internet while roaming, just return false
//			return true;
//		}
//		return true;
//	}
	// see http://androidsnippets.com/have-internet
}
