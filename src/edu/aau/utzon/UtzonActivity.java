package edu.aau.utzon;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import edu.aau.utzon.location.LocationAwareActivity;
import edu.aau.utzon.utils.CommonIntents;
import edu.aau.utzon.webservice.RestServiceHelper;
import android.location.Location;
import android.os.Bundle;

public class UtzonActivity extends LocationAwareActivity {
	private static final String TAG = "UtzonActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
	}
	
	boolean firstLocation = true;
	@Override
	public void serviceNewLocationBroadcast(Location location) {
		super.serviceNewLocationBroadcast(location);
		if(firstLocation && location != null) {
			RestServiceHelper.getServiceHelper().getNearestPoints(this, 5, location);
			firstLocation = false;
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
