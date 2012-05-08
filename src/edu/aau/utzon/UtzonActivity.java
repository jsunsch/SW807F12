package edu.aau.utzon;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.aau.utzon.augmented.AugmentedActivity;
import edu.aau.utzon.indoor.IndoorActivity;
import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.outdoor.OutdoorActivity;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestServiceHelper;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class UtzonActivity extends SherlockActivity {

	private class RestContentObserver extends ContentObserver{
		public RestContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

		private int poiCounter = 0;
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			TextView tv3 = (TextView) findViewById(R.id.main_text3);
			tv3.setText("Fetched " + ++poiCounter + " points of interest.");
		}
	}
	
	private LocationHelper mLocationHelper = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		setContentView(R.layout.main);
		
		// Energize the GPS (location manager)
		TextView tv = (TextView) findViewById(R.id.main_text);
		tv.setText("Energizing sensors...");
		mLocationHelper = new LocationHelper(this);
		mLocationHelper.onCreate(savedInstanceState);
		
		// Asynchornously start a REST method
		TextView tv2 = (TextView) findViewById(R.id.main_text2);
		tv2.setText("Synchronizing POI's...");
		RestServiceHelper.getServiceHelper()
			.getLocationPoints(getBaseContext());
		
		TextView tv3 = (TextView) findViewById(R.id.main_text3);
		RestContentObserver mContentObserver = new RestContentObserver(new Handler());
		getContentResolver().registerContentObserver(ProviderContract.Points.CONTENT_URI, true, mContentObserver);
	}
	
	@Override
	public void onPause()	{
		super.onPause();
		mLocationHelper.onPause();
	}
	
	@Override
	public void onResume()	{
		super.onResume();
		mLocationHelper.onResume();
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
