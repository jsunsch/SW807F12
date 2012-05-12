package edu.aau.utzon.location;


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
