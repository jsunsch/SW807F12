package edu.aau.utzon;

import edu.aau.utzon.location.LocationAwareActivity;
import edu.aau.utzon.webservice.RestServiceHelper;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class UtzonActivity extends LocationAwareActivity {
	private static final String TAG = "UtzonActivity";
	private static final int PRELOAD_COUNT = 20;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView tv = (TextView)findViewById(R.id.main_text);
		tv.setText(R.string.main_screen_text);
	}
	
	boolean firstLocation = true;
	@Override
	public void serviceNewLocationBroadcast(Location location) {
		super.serviceNewLocationBroadcast(location);
		if(firstLocation && location != null) {
			RestServiceHelper.getServiceHelper().getNearestPoints(this, PRELOAD_COUNT, location);
			firstLocation = false;
		}		
	}

	
}
