package edu.aau.utzon.location;

import java.util.Random;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
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
    private void initState(Context context) {
    	mLocationHelper = new LocationHelper(context);
    }

	@Override
	public IBinder onBind(Intent intent) {
		initState(this);
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