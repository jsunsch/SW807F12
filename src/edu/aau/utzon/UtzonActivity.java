package edu.aau.utzon;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import edu.aau.utzon.augmented.AugmentedActivity;
import edu.aau.utzon.indoor.IndoorActivity;
import edu.aau.utzon.location.LocationAwareActivity;
import edu.aau.utzon.outdoor.OutdoorActivity;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestServiceHelper;

import android.content.Intent;
import android.database.ContentObserver;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class UtzonActivity extends LocationAwareActivity {
	private static final String TAG = "UtzonActivity";
	private int poiCounter = 0;

	private RestContentObserver mContentObserver = new RestContentObserver(new Handler());
	private class RestContentObserver extends ContentObserver{
		public RestContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			TextView tv1 = (TextView)findViewById(R.id.main_text);
			tv1.setText("Fetched " + ++poiCounter + " point(s) of interest.");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		//		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
		//			requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		}

		// Register content observer so that we are notified when new POIs are available
		getContentResolver().registerContentObserver(ProviderContract.Points.CONTENT_URI, false, mContentObserver);
		
		setContentView(R.layout.main);
		TextView tv1 = (TextView) findViewById(R.id.main_text);
		tv1.setText("Acquiring location...");
	}

	
	boolean firstLocation = true;
	@Override
	public void makeUseOfNewLocation(Location loc) {
		if(firstLocation) {
			TextView tv1 = (TextView) findViewById(R.id.main_text);
			tv1.setText("Connecting to server...");
			RestServiceHelper.getServiceHelper()
				.getNearestPoints(this, 5, getLocationHelper().getCurrentLocation());
			firstLocation = false;
		}
	}
	
	@Override
	public void onPause()	{
		super.onPause();
		getContentResolver().unregisterContentObserver(mContentObserver);
	}

	@Override
	public void onResume()	{
		super.onResume();
		getContentResolver().registerContentObserver(ProviderContract.Points.CONTENT_URI, true, mContentObserver);
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
			startActivity(new Intent(this, OutdoorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		case R.id.actionbar_augmented:
			startActivity(new Intent(this, AugmentedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		case R.id.actionbar_indoor:
			startActivity(new Intent(this, IndoorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		case R.id.actionbar_poi_list:
			startActivity(new Intent(this, PoiListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		case R.id.actionbar_settings:
			startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		case R.id.actionbar_search:
			// TODO: Implement
			onSearchRequested();
			return true;
		case R.id.actionbar_refresh:
			TextView tv1 = (TextView)findViewById(R.id.main_text);
			tv1.setText("Location not yet available. Please try again.");
			if(getLocationHelper().getCurrentLocation() != null ) {
				poiCounter = 0;
				tv1.setText("Connecting to server...");
				RestServiceHelper.getServiceHelper()
					.getNearestPoints(this, 5, getLocationHelper().getCurrentLocation());
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
