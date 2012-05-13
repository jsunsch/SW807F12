package edu.aau.utzon.location;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import edu.aau.utzon.R;
import edu.aau.utzon.location.SampleService.SampleBinder;
import edu.aau.utzon.utils.CommonIntents;
import edu.aau.utzon.webservice.PointModel;
public abstract class LocationAwareActivity extends SherlockActivity {
	private static final String TAG = "LocationAwareMapActivity";
	private SampleService mService = null;
	private boolean mBound = false;
	public boolean isBound() { return mBound; }
	

//	abstract public void serviceBoundEvent(SampleService service);
//	abstract public void serviceDisconnectedEvent();
//	abstract public void serviceNewPoiBroadcast(PointModel poi);
//	abstract public void serviceNewLocationBroadcast(Location location);

	private int shownAlertId = 0;
	//private int shownToastId = 0;
	
	public void serviceNewPoiBroadcast(final PointModel poi) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You are near a POI. Do you wish to see the content available?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				startActivity(CommonIntents.startPoiContentActivity(getBaseContext(), mService.getLocationHelper().getCurrentLocation(), poi));
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		// Avoid spamming the user with alert dialogs
		if(shownAlertId == 0 || (shownAlertId != mService.getLocationHelper().getCurrentClosePoi().getId())) {
			shownAlertId = mService.getLocationHelper().getCurrentClosePoi().getId();
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	private Location old = null;
	public void serviceNewLocationBroadcast(Location location) {
		mService.getLocationHelper().makeUseOfNewLocation(location);
	}
//		if(location != null &&  ) {
//			shownToastId = mService.getLocationHelper().getCurrentClosePoi().getId();
//			double dist = mService.getLocationHelper().distToPoi(mService.getLocationHelper().getCurrentClosePoi());
//			CharSequence text = "Distance to nearest POI: " + (int)dist + " meter(s).";
//			Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
//			toast.show();
//		}
	//}	
	
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			SampleBinder binder = (SampleBinder) service;
			mService = binder.getService();
			mBound = true;
			//serviceBoundEvent(mService);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
			//serviceDisconnectedEvent();
		}
	};	
	
	// Handler for broadcast events (Poi notification)
		private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// Get extra data included in the Intent
				//String message = intent.getStringExtra("message");
				Location loc = intent.getParcelableExtra(CommonIntents.EXTRA_LOCATION);
				PointModel poi = intent.getParcelableExtra(CommonIntents.EXTRA_NEAR_POI);
				//TextView tv1 = (TextView) findViewById(R.id.main_text);
				if(loc != null) {
					Log.i(TAG, "Got location update");
					serviceNewLocationBroadcast(loc);
					//tv1.setText("Got location update");
				}
				else if(poi != null) {
					//tv1.setText("Got nearPOI update");
					Log.i(TAG, "Got near POI update");
					serviceNewPoiBroadcast(poi);
				}
				else {
					throw new IllegalArgumentException("Passing intent to onReceive must provide EXTRA_LOCATION or EXTRA_NEAR_POI");
				}
			}
		};

	@Override
	public void onStart() {
		super.onStart();
		// Bind to LocalService
		Intent intent = new Intent(this, SampleService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		// Register to receive broadcasts from LocationAwareService
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, 
				new IntentFilter(CommonIntents.POI_INTENTFILTER));
	}

	@Override
	public void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Intent intent = new Intent(this, SampleService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.layout.menu_main, menu);
		return true;
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.actionbar_outdoor:
			startActivity(CommonIntents.startOutdoorActivity(this));
			return true;
		case R.id.actionbar_augmented:
			startActivity(CommonIntents.startAugmentedActivity(this));
			return true;
		case R.id.actionbar_indoor:
			startActivity(CommonIntents.startIndoorActivity(this));
			return true;
		case R.id.actionbar_poi_list:
			startActivity(CommonIntents.startPoiListActivity(this));
			return true;
		case R.id.actionbar_settings:
			startActivity(CommonIntents.startSettingsActivity(this));
			return true;
		case R.id.actionbar_search:
			// TODO: Implement
			onSearchRequested();
			return true;
		case R.id.actionbar_refresh:
			//queryWebService();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
