//package edu.aau.utzon;
//
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;
//import edu.aau.utzon.augmented.AugmentedActivity;
//import edu.aau.utzon.indoor.IndoorActivity;
//import edu.aau.utzon.location.LocationPoller;
//import edu.aau.utzon.location.LocationPollerService;
//import edu.aau.utzon.outdoor.OutdoorActivity;
//import edu.aau.utzon.utils.CommonIntents;
//import edu.aau.utzon.webservice.ProviderContract;
//import edu.aau.utzon.webservice.RestService;
//import edu.aau.utzon.webservice.RestServiceHelper;
//
//import android.content.Intent;
//import android.database.ContentObserver;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Handler;
//import android.widget.TextView;
//
//public class UtzonActivity extends SherlockActivity {
//	private static final String TAG = "UtzonActivity";
//	private int poiCounter = 0;
//
//	private RestContentObserver mContentObserver;
//	private LocationPoller mLocationPoller; 
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
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//		// Register content observer so that we are notified when new POIs are available
//		mContentObserver = new RestContentObserver(new Handler());
//		getContentResolver().registerContentObserver(ProviderContract.Points.CONTENT_URI, false, mContentObserver);
//		
//		
//		RestServiceHelper.getServiceHelper()
//		.getLocationPoints(this);
//		
//		//LocationPollerService.requestLocation(getBaseContext(), new LocationPoller());
//		
//		TextView tv1 = (TextView) findViewById(R.id.main_text);
//		tv1.setText("Acquiring location...");
//	}
//
//	@Override
//	public void onPause()	{
//		super.onPause();
//		getContentResolver().unregisterContentObserver(mContentObserver);
//	}
//
//	@Override
//	public void onResume()	{
//		super.onResume();
//		getContentResolver().registerContentObserver(ProviderContract.Points.CONTENT_URI, true, mContentObserver);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.layout.menu_main, menu);
//		return true;
//	}	
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle item selection
//		switch (item.getItemId()) {
//		case R.id.actionbar_outdoor:
//			startActivity(new Intent(this, OutdoorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_augmented:
//			startActivity(new Intent(this, AugmentedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_indoor:
//			startActivity(new Intent(this, IndoorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_poi_list:
//			startActivity(new Intent(this, PoiListActivity.class)
//			.putExtra(PoiListActivity.COMMAND, PoiListActivity.COMMAND_ALL)
//			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_settings:
//			startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_search:
//			// TODO: Implement
//			onSearchRequested();
//			return true;
//		case R.id.actionbar_refresh:
//			firstRun = true; // force refresh
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
//
//
//	public void makeUseOfNewNearPoi(int poi_id) {
//		// Ignore		
//	}
//
//	boolean firstRun = true;
//	Location mLocation = null;
//
//	public void makeUseOfNewLocation(Location location) {
//		if(firstRun) {
//			mLocation = location;
//			TextView tv1 = (TextView)findViewById(R.id.main_text);
//			tv1.setText("Connecting to server...");
//			RestServiceHelper.getServiceHelper()
//				.getNearestPoints(this, 5, location);
//			firstRun = false;
//		}
//	}	
//}
