package edu.aau.utzon;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import edu.aau.utzon.augmented.AugmentedActivity;
import edu.aau.utzon.indoor.IndoorActivity;
import edu.aau.utzon.outdoor.OutdoorActivity;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestServiceHelper;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

			TextView tv = (TextView) findViewById(R.id.main_text);
			tv.setText("Received " + ++poiCounter + " points of interest from server");

			Log.e("TACO", "Cos them hoes is bitches!");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// Remove title bar
		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		
		setContentView(R.layout.main);
		TextView tv = (TextView) findViewById(R.id.main_text);
		tv.setText("Synchronizing POI's");
		
		// Asynchornously start a REST method
		RestServiceHelper.getServiceHelper()
			.getLocationPoints(getBaseContext());	
		
		RestContentObserver mContentObserver = new RestContentObserver(new Handler());
		getContentResolver().registerContentObserver(ProviderContract.Points.CONTENT_URI, true, mContentObserver);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.actionbar_outdoor:
			startActivity(new Intent(this, OutdoorActivity.class));
			return true;
		case R.id.actionbar_augmented:
			startActivity(new Intent(this, AugmentedActivity.class));
			return true;
		case R.id.actionbar_indoor:
			startActivity(new Intent(this, IndoorActivity.class));
			return true;
		case R.id.actionbar_settings:
			startActivity(new Intent(this, SettingsActivity.class));
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
