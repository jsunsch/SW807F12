package edu.aau.utzon.webservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class RestServiceHelper {
	
	private static RestServiceHelper ref;
	private static RestService rs;

	private RestServiceHelper()
    {
		rs = new RestService();
    }

    public static RestServiceHelper getServiceHelper()
    {
      if (ref == null)
          ref = new RestServiceHelper();
      return ref;
    }
	
	public void getLocationPoints(Context context) {
		GlobalTester.TEST = "Yo";
		Log.e("Service Example", "Niggar niggar niggar niggar niggar niggar niggar! Why you call them hoes bitches?"); 
		Intent intent = new Intent(context, RestService.class);
		
		
		//intent.putExtra(RestService.COMMAND, RestService.COMMAND_GET_LOCATION_POINTS);
		
		context.startService(intent);
		//rs.startService(intent);
		
	}
}
