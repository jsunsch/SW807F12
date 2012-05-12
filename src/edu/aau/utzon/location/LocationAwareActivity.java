package edu.aau.utzon.location;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import edu.aau.utzon.location.SampleService.SampleBinder;
public abstract class LocationAwareActivity extends SherlockActivity {
	private static final String TAG = "LocationAwareMapActivity";
	protected SampleService mService = null;
	private boolean mBound = false;
	public boolean isBound() { return mBound; }

	abstract public void serviceBoundEvent(SampleService service);
	abstract public void serviceDisconnectedEvent();

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			SampleBinder binder = (SampleBinder) service;
			mService = binder.getService();
			mBound = true;
			serviceBoundEvent(mService);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
			serviceDisconnectedEvent();
		}
	};	

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to LocalService
		Intent intent = new Intent(this, SampleService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}


}

//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//import com.actionbarsherlock.app.SherlockActivity;
//
//public abstract class LocationAwareActivity extends SherlockActivity {
//	private static final String TAG = "LocationAwareActivity";
//	
//	// Handler for broadcast events (Poi notification)
//	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// Get extra data included in the Intent
//			Bundle bundle = intent.getExtras();
//			int command = bundle.getInt(LocationAwareService.COMMAND);
//
//			switch (command) {
//			case LocationAwareService.BROADCAST_ISNEAR_POI:
//				makeUseOfNewNearPoi(bundle.getInt(LocationAwareService.BROADCAST_EXTRAS_POI_ID));
//				break;
//			case LocationAwareService.BROADCAST_LOCATION_UPDATE:
//				makeUseOfNewLocation((Location)bundle.getParcelable(LocationAwareService.BROADCAST_EXTRAS_LOCATION));
//				break;
//			}
//		}
//	};
//
//	public abstract void makeUseOfNewNearPoi(int poi_id);
//	public abstract void makeUseOfNewLocation(Location location);
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		Log.i(TAG, "Sending intent to LocationHelperService");
//		startService(new Intent(this, LocationAwareService.class)
//			.putExtra(LocationAwareService.COMMAND, LocationAwareService.COMMAND_ONCREATE));
//
//		// Register to receive broadcasts from LocationAwareService
//		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, 
//				new IntentFilter(LocationAwareService.BROADCAST_INTENTFILTER));
//	}	
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		startService(new Intent(this, LocationAwareService.class)
//			.putExtra(LocationAwareService.COMMAND, LocationAwareService.COMMAND_ONPAUSE));
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		startService(new Intent(this, LocationAwareService.class)
//			.putExtra(LocationAwareService.COMMAND, LocationAwareService.COMMAND_ONRESUME));
//	}
//
//}
