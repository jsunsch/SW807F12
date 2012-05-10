package edu.aau.utzon.webservice;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.sax.StartElementListener;
import android.util.Log;

public class RestServiceHelper {
	
	private static RestServiceHelper ref;

	private RestServiceHelper() {
    }

    public static RestServiceHelper getServiceHelper() {
      if (ref == null)
          ref = new RestServiceHelper();
      return ref;
    }
      
    
    public void getLocationPoint(Context context, int id) {
    	Intent intent = new Intent(context, RestService.class);
    	intent.putExtra(RestService.COMMAND, RestService.COMMAND_GET_POI_ID);
    	intent.putExtra(RestService.POI_ID, id);
    	context.startService(intent);
    }
    
	public void getLocationPoints(Context context) {
		Intent intent = new Intent(context, RestService.class);
		intent.putExtra(RestService.COMMAND, RestService.COMMAND_GET_POI_ALL);
		context.startService(intent);
	}
	
	public void getNearestPoints(Context context, int k, Location l) {
		context.startService(new Intent(context, RestService.class)
			.putExtra(RestService.COMMAND, RestService.COMMAND_GET_POI_K)
			.putExtra(RestService.POI_K, k)
			.putExtra(RestService.LOCATION_LAT, l.getLatitude())
			.putExtra(RestService.LOCATION_LONG, l.getLongitude())
			);
	}
}
