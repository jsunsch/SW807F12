package edu.aau.utzon.webservice;

import android.content.Context;
import android.content.Intent;
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
    	intent.putExtra(RestService.COMMAND, RestService.COMMAND_GET_LOCATION_POINT);
    	intent.putExtra(RestService.LOCATION_ID, id);
    	context.startService(intent);
    }
	
	public void getLocationPoints(Context context) {
		Intent intent = new Intent(context, RestService.class);
		
		intent.putExtra(RestService.COMMAND, RestService.COMMAND_GET_LOCATION_POINTS);
		context.startService(intent);
	}
}
