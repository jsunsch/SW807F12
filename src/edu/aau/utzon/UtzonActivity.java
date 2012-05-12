package edu.aau.utzon;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import edu.aau.utzon.location.LocationAwareActivity;
import edu.aau.utzon.location.SampleService;
import edu.aau.utzon.utils.CommonIntents;
import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestServiceHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class UtzonActivity extends LocationAwareActivity {
	private static final String TAG = "UtzonActivity";
	private int poiCounter = 0;

//	private RestContentObserver mContentObserver;
//	private class RestContentObserver extends ContentObserver{
//		public RestContentObserver(Handler handler) {
//			super(handler);
//		}
//
//		@Override
//		public boolean deliverSelfNotifications() {
//			return true;
//		}
//
//		@Override
//		public void onChange(boolean selfChange) {
//			super.onChange(selfChange);
//			TextView tv1 = (TextView)findViewById(R.id.main_text);
//			tv1.setText("Fetched " + ++poiCounter + " point(s) of interest.");
//		}
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Register content observer so that we are notified when new POIs are available
//		mContentObserver = new RestContentObserver(new Handler());
//		getContentResolver().registerContentObserver(ProviderContract.Points.CONTENT_URI, false, mContentObserver);
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


	@Override
	public void serviceBoundEvent(SampleService service) {
		mService = service;
		//queryWebService();
	}

	@Override
	public void serviceDisconnectedEvent() {
		mService = null;

	}

	
	//@Override
//	public void serviceNewPoiBroadcast(final PointModel poi) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage("You are near a POI. Do you wish to see the content available?")
//		.setCancelable(false)
//		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				startActivity(CommonIntents.startPoiContentActivity(getBaseContext(), mService.getLocationHelper().getCurrentLocation(), poi));
//			}
//		})
//		.setNegativeButton("No", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				dialog.cancel();
//			}
//		});
//
//		// Avoid spamming the user with alert dialogs
//		if(shownAlertId == 0 || (shownAlertId != mService.getLocationHelper().getCurrentClosePoi().getId())) {
//			shownAlertId = mService.getLocationHelper().getCurrentClosePoi().getId();
//			AlertDialog alert = builder.create();
//			alert.show();
//		}
//	}

	

//	@Override
//	public void serviceNewLocationBroadcast(Location location) {
//		if(shownToastId == 0 || (shownToastId != mService.getLocationHelper().getCurrentClosePoi().getId())) {
//			shownToastId = mService.getLocationHelper().getCurrentClosePoi().getId();
//			double dist = mService.getLocationHelper().distToPoi(mService.getLocationHelper().getCurrentClosePoi());
//			CharSequence text = "Distance to nearest POI: " + (int)dist + " meter(s).";
//			Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
//			toast.show();
//			
//			if(firstLocationUpdate) {
//				RestServiceHelper.getServiceHelper().getNearestPoints(this, 10, location);
//				firstLocationUpdate = false;
//				TextView tv = (TextView)findViewById(R.id.main_text);
//				tv.setText("Synchronization done.");
//			}
//		}
//	}	
}
