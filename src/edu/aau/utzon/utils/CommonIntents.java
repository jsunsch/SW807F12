package edu.aau.utzon.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import edu.aau.utzon.PoiContentActivity;
import edu.aau.utzon.PoiListActivity;
import edu.aau.utzon.SettingsActivity;
import edu.aau.utzon.augmented.AugmentedActivity;
import edu.aau.utzon.indoor.IndoorActivity;
import edu.aau.utzon.outdoor.OutdoorActivity;
import edu.aau.utzon.webservice.PointModel;

public class CommonIntents {
	public static String POI_INTENTFILTER = "POI_INTENTFILTER";
	static public String EXTRA_LOCATION = "EXTRA_LOCATION";
	static public String EXTRA_NEAR_POI = "EXTRA_NEAR_POI";

	static public Intent startSettingsActivity(Context context) {
		return new Intent(context, SettingsActivity.class);
	}
	
	static public Intent startPoiListActivity(Context context) {
		return new Intent(context, PoiListActivity.class)
			.putExtra(PoiListActivity.COMMAND, PoiListActivity.COMMAND_ALL)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	
	static public Intent startPoiListActivityQuery(Context context, String query) {
		return new Intent(context, PoiListActivity.class)
			.putExtra(PoiListActivity.COMMAND, PoiListActivity.COMMAND_QUERY)
			.putExtra(PoiListActivity.EXTRAS_QUERY, query)		
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	
	static public Intent startPoiContentActivity(Context context, int id) {
		Intent intent = new Intent(context, PoiContentActivity.class);
		intent.putExtra(PoiContentActivity.COMMAND, PoiContentActivity.COMMAND_NORMAL_ID);
		intent.putExtra(PoiContentActivity.EXTRAS_NORMAL_ID, id);
		return intent;
	}
	
	static public Intent startPoiContentActivityFromBubbleTap(Context context, int bubbleID) {
		return new Intent(context, PoiContentActivity.class)
			.putExtra(PoiContentActivity.COMMAND, PoiContentActivity.COMMAND_BUBBLETAP_ID)
			.putExtra(PoiContentActivity.EXTRAS_BUBBLETAP_ID, bubbleID);
	}
	
	static public Intent startIndoorActivity(Context context) {
		return new Intent(context, IndoorActivity.class);
	}
	
	static public Intent startOutdoorActivity(Context context) {
		return new Intent(context, OutdoorActivity.class);
	}

	static public  Intent startAugmentedActivity(Context context) {
		return new Intent(context, AugmentedActivity.class);
	}
	
	
	
	static public Intent broadcastNearPoi(Context context, PointModel nearPOI) {
		Intent intent = new Intent(POI_INTENTFILTER);
		intent.putExtra(EXTRA_NEAR_POI, nearPOI);
		return intent;
	}
	
	static public Intent broadcastLocationUpdate(Context context, Location location) {
		Intent intent = new Intent(POI_INTENTFILTER);
		intent.putExtra(EXTRA_LOCATION, location);
		return intent;
	}
}
