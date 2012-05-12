package edu.aau.utzon.utils;

import android.content.Context;
import android.content.Intent;
import edu.aau.utzon.PoiListActivity;
import edu.aau.utzon.SettingsActivity;
import edu.aau.utzon.augmented.AugmentedActivity;
import edu.aau.utzon.outdoor.OutdoorActivity;

public class CommonIntents {
	static public Intent startSettingsActivity(Context context) {
		return new Intent(context, SettingsActivity.class);
	}
	
	static public Intent startPoiListActivity(Context context) {
		return new Intent(context, PoiListActivity.class)
			.putExtra(PoiListActivity.COMMAND, PoiListActivity.COMMAND_ALL);
	}
	
	static public Intent startIndoorActivity(Context context) {
		return new Intent(context, SettingsActivity.class);
	}
	
	static public Intent startOutdoorActivity(Context context) {
		return new Intent(context, OutdoorActivity.class);
	}

	public static Intent startAugmentedActivity(Context context) {
		return new Intent(context, AugmentedActivity.class);
	}	
}
