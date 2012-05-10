package edu.aau.utzon;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import edu.aau.utzon.augmented.AugmentedActivity;
import edu.aau.utzon.indoor.IndoorActivity;
import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.outdoor.OutdoorActivity;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestServiceHelper;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class UtzonActivity extends SherlockActivity {
	private static final String TAG = "UtzonActivity";
	private int poiCounter = 0;
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

			// Getting 2 onChange events for each inserted item. Not sure why
			TextView tv3 = (TextView)findViewById(R.id.main_text3);
			tv3.setText("Fetched " + ++poiCounter + " point(s) of interest.");
		}
	}

	RestContentObserver mContentObserver = new RestContentObserver(new Handler());
	private LocationManager mLocationManager = null;
	private Location mLocation = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		//		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
		//			requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		}

		mLocationManager  = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );
		String provider = mLocationManager.getBestProvider( criteria, true );

		if ( provider == null ) {
			Log.e( TAG, "No location provider found!" );
			return;
		}

		mLocationManager.requestLocationUpdates(provider, 0, 1, new LocationListener() {
			public void onLocationChanged(Location location) {
				// Asynchornously start a REST method
				// 25 should be a setting?
				RestServiceHelper.getServiceHelper().getNearestPoints(getBaseContext(), 25, location);
				// Only need a single location
				mLocation = location;
				mLocationManager.removeUpdates(this);
				TextView tv3 = (TextView)findViewById(R.id.main_text3);
				tv3.setText("Connecting to server...");
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		});

		setContentView(R.layout.main);
		TextView tv1 = (TextView) findViewById(R.id.main_text);
		tv1.setText("Loading... Done!");

		// Asynchornously start a REST method
		TextView tv2 = (TextView) findViewById(R.id.main_text2);
		tv2.setText("Synchronizing POI's...");

		getContentResolver().registerContentObserver(ProviderContract.Points.CONTENT_URI, false, mContentObserver);
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
			if(mLocation != null ) {
				TextView tv3 = (TextView)findViewById(R.id.main_text3);
				tv3.setText("Connecting to server...");
				poiCounter = 0;
				RestServiceHelper.getServiceHelper().getNearestPoints(getBaseContext(), 25, mLocation);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
