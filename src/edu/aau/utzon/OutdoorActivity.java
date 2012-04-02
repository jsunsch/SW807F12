package edu.aau.utzon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


import edu.aau.utzon.WebserviceActivity.RestContentObserver;
import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.location.PointOfInterest;
import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestServiceHelper;

public class OutdoorActivity extends SherlockMapActivity implements Serializable {

	private LocationHelper mLocationHelper;
	private ArrayList<PointModel> mOutdoorPois;

	public final static  String[] mProjectionAll = {ProviderContract.Points.ATTRIBUTE_ID, 
		ProviderContract.Points.ATTRIBUTE_X, 
		ProviderContract.Points.ATTRIBUTE_Y, 
		ProviderContract.Points.ATTRIBUTE_DESCRIPTION};

	@Override
	public void onResume()
	{
		super.onResume();
		mLocationHelper.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mLocationHelper.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Init locationHelper
		mOutdoorPois = new ArrayList<PointModel>();
		this.mLocationHelper = new LocationHelper(getApplicationContext());
		mLocationHelper.onCreate(savedInstanceState);

		// Remove title bar
		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		// Display Google maps to the user
		setContentView(R.layout.mapview);

		// Enable built-in map controls
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		// Draw the user position on map
		MyLocationOverlay locationOverlay = new MyLocationOverlay(this, mapView);
		locationOverlay.enableMyLocation();
		mapView.getOverlays().add(locationOverlay);

		registerContentObserver();
		getAllOutdoorPois();

		//PoiService.StartService(this);
		startPoiNotificationService();



		// Register to receive broadcasts (from the PoiNotificationService)
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter(PoiService.POI_INTENTFILTER));

	}

	// Handler for broadcast events (Poi notification)
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String message = intent.getStringExtra("message");
			Log.d("receiver", "Got message: " + message);
		}
	};


	private void getAllOutdoorPois() {
		RestServiceHelper.getServiceHelper()
		.getLocationPoints(this);
	}

	private void registerContentObserver() {
		RestContentObserver mContentObserver = new RestContentObserver(new Handler());
		this.getApplicationContext()
		.getContentResolver()
		.registerContentObserver(ProviderContract.Points.CONTENT_URI, true, mContentObserver);
	}

	protected void animateToLocation(Location loc)
	{
		// Only animate if its a valid location
		if(loc != null){
			// Display current position on map
			MapView mapView = (MapView) findViewById(R.id.mapview);
			MapController mc = mapView.getController();

			GeoPoint point =  LocationHelper.locToGeo(loc);
			mc.animateTo(point);
		}
	}

	public void updateOutdoorPois(ArrayList<PointModel> pois) {
		mOutdoorPois = pois;
		drawOutdoorPois();
	}

	protected void drawOutdoorPois()
	{
		MapView mapView = (MapView) findViewById(R.id.mapview);

		// Setup overlays
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.clear();
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		BalloonOverlay itemizedoverlay = new BalloonOverlay(drawable, mapView);
		//		GMapsOverlay itemizedover)lay = new GMapsOverlay(drawable, this);

		// Add POI to the overlay
		for(PointModel p : mOutdoorPois)
		{
			itemizedoverlay.addOverlay(new OverlayItem(p.geoPoint, "Title", p.description));
		}
		mapOverlays.add(itemizedoverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.layout.menu_outdoor, menu);

		MenuItem searchItem = menu.findItem(R.id.actionbar_search);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.actionbar_center_location:
			animateToLocation(mLocationHelper.getCurrentLocation());
			return true;
		case R.id.actionbar_poi_list:
			ArrayList<PointOfInterest> pois = new ArrayList<PointOfInterest>();
			pois.add(new PointOfInterest("Tyren ved vejen", 500));
			pois.add(new PointOfInterest("Limfjordbroen", 2000));

			Intent i = new Intent(this, PoiListActivity.class);
			i.putExtra("pois", pois);
			startActivity(i);

			return true;
		case R.id.actionbar_search:
			//
			onSearchRequested();
			return true;
		case R.id.actionbar_augmented:
			startActivity(new Intent(this, AugmentedActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startPoiNotificationService() {

		Intent intent = new Intent(this, PoiService.class);
		intent.putExtra(PoiService.COMMAND, PoiService.COMMAND_START_SERVICE);
		intent.putExtra(PoiService.USER_LOCATION_X, mLocationHelper.getCurrentLocation().getLatitude());
		intent.putExtra(PoiService.USER_LOCATION_Y, mLocationHelper.getCurrentLocation().getLongitude());
		startService(intent);
		//bindService(intent, networkServiceConnection, Context.BIND_AUTO_CREATE);
	}

	class RestContentObserver extends ContentObserver{
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
			// New content is available
			Cursor c = managedQuery(ProviderContract.Points.CONTENT_URI,   	// The content URI of the points table
					mProjectionAll,                        	// The columns to return for each row
					null,                    				// Selection criteria
					null,                     				// Selection criteria
					null);                        			// The sort order for the returned rows

			ArrayList<PointModel> points = new ArrayList<PointModel>();

			c.moveToFirst();
			do
			{
				int colIndexId = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_ID);
				int colIndexDesc = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_DESCRIPTION);
				int colIndexX = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_X);
				int colIndexY = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_Y);

				int id = c.getInt(colIndexId);
				String desc = c.getString(colIndexDesc);
				float x = c.getFloat(colIndexX);
				float y = c.getFloat(colIndexY);

				PointModel p = new PointModel();
				p.description = desc;
				p.id = id;
				p.geoPoint = new GeoPoint((int)x,(int)y);

				points.add(p);

			} while (c.moveToNext() == true);

			updateOutdoorPois(points);

			Log.e("TACO", "Cos them hoes is bitches!");
		}
	}
}
