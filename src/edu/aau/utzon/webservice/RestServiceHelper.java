package edu.aau.utzon.webservice;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
		Log.e("Service Example", "Niggar niggar niggar niggar niggar niggar niggar! Why you call them hoes bitches?"); 
		Intent intent = new Intent(context, RestService.class);
		
		intent.putExtra(RestService.COMMAND, RestService.COMMAND_GET_LOCATION_POINTS);
		context.startService(intent);
	}
}
