package edu.aau.utzon;

import edu.aau.utzon.location.LocationAwareActivity;
import edu.aau.utzon.location.SampleService;
import edu.aau.utzon.utils.CommonIntents;
import edu.aau.utzon.webservice.RestServiceHelper;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class UtzonActivity extends LocationAwareActivity {
	private static final String TAG = "UtzonActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView tv = (TextView)findViewById(R.id.main_text);
		tv.setText(R.string.main_screen_text);
		
		startService(new Intent(this, SampleService.class));
	}	
}
