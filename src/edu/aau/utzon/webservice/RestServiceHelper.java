package edu.aau.utzon.webservice;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.sax.StartElementListener;
import android.util.Log;

public class RestServiceHelper {
	
	private static final String TAG = "RestServiceHelper";
	private static RestServiceHelper ref;

	private RestServiceHelper() {
    }

    public static RestServiceHelper getServiceHelper() {
      if (ref == null)
          ref = new RestServiceHelper();
      return ref;
    }      
    
    public void getLocationPoint(Context context, int id) {
    	Log.i(TAG, "getLocationPoint() called");
    	context.startService(new Intent(context, RestService.class)
    		.putExtra(RestService.COMMAND, RestService.COMMAND_GET_POI_ID)
    		.putExtra(RestService.EXTRAS_POI_ID, id));
    }
    
	public void getLocationPoints(Context context) {
		Log.i(TAG, "getLocationPoints() called");
		context.startService(new Intent(context, RestService.class)
			.putExtra(RestService.COMMAND, RestService.COMMAND_GET_POI_ALL));
	}
	
	public void getNearestPoints(Context context, int k, Location l) {
		Log.i(TAG, "getNearestPoints() called");
		context.startService(new Intent(context, RestService.class)
			.putExtra(RestService.COMMAND, RestService.COMMAND_GET_POI_K)
			.putExtra(RestService.EXTRAS_POI_K, k)
			.putExtra(RestService.EXTRAS_LOCATION_LAT, l.getLatitude())
			.putExtra(RestService.EXTRAS_LOCATION_LONG, l.getLongitude())
			);
	}
}
