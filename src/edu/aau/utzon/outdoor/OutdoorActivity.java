package edu.aau.utzon.outdoor;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.TapControlledMapView;
import com.readystatesoftware.maps.OnSingleTapListener;


import edu.aau.utzon.PoiContentActivity;
import edu.aau.utzon.PoiListActivity;
import edu.aau.utzon.R;
import edu.aau.utzon.SettingsActivity;
import edu.aau.utzon.augmented.AugmentedActivity;
import edu.aau.utzon.indoor.IndoorActivity;
import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.location.NearPoiPublisher;
import edu.aau.utzon.webservice.PointModel;

public class OutdoorActivity extends SherlockMapActivity {
	private static final String TAG = null;
	private static final String PREFS_PROXIMITY = "proximity";
	private TapControlledMapView mMapView;
	private MyLocationOverlay mMyLocationOverlay;
	private LocationHelper mLocationHelper;
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;

	@Override
	public void onResume()
	{
		super.onResume();
		mMyLocationOverlay.enableMyLocation();
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mMyLocationOverlay.disableMyLocation();
		mLocationManager.removeUpdates(mLocationListener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		// Display Google maps to the user
		setContentView(R.layout.mapview);

		// Enable built-in map controls
		mMapView = (TapControlledMapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);

		// Draw the user position on map
		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMyLocationOverlay.enableMyLocation();
		mMapView.getOverlays().add(mMyLocationOverlay);

		// Enable location manager
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );
		String provider = mLocationManager.getBestProvider( criteria, true );

		if ( provider == null ) {
			Log.e( TAG, "No location provider found!" );
			return;
		}
		
		// Define a listener that responds to location updates
		mLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found.
				makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};
		
		// Register the listener with the Location Manager to receive location updates
		mLocationManager.requestLocationUpdates(provider, 0, 0, mLocationListener);
		
		// Init LocationHelper
		mLocationHelper = new LocationHelper(this);
		
		// Draw POIs
		drawOutdoorPois();
	}

	protected void makeUseOfNewLocation(Location location) {
		
		if(location != null) {
			mLocationHelper.makeUseOfNewLocation(location);
			// Set some threshold for minimum activation distance
			SharedPreferences settings = getSharedPreferences(PREFS_PROXIMITY, 0);

			int proximityTreshold = settings.getInt("proximity", 20);
			double debug = mLocationHelper.distToPoi(mLocationHelper.getCurrentClosePoi());
			
			Context context = getApplicationContext();
			CharSequence text = "Distance to nearest POI: " + debug;
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			
			
			if(mLocationHelper.distToPoi(mLocationHelper.getCurrentClosePoi()) < proximityTreshold) {
				//StartPoiContentActivity(mLocationHelper.getCurrentClosePoi().getId());
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("You are near a POI. Do you wish to see the content available?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                StartPoiContentActivity(mLocationHelper.getCurrentClosePoi().getId());
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
		
	}

	private void StartPoiContentActivity(int id) {
		startActivity(new Intent(this, PoiContentActivity.class).putExtra("_ID", id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	protected void animateToLocation(Location loc)
	{
		// Only animate if its a valid location
		if(loc != null){
			MapController mc = mMapView.getController();
			GeoPoint point =  LocationHelper.locToGeo(loc);
			mc.animateTo(point);
		}
	}
	
	protected void drawOutdoorPois()
	{
		// Setup overlays
		List<Overlay> mapOverlays = mMapView.getOverlays();
		//mapOverlays.clear();
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		final BalloonOverlay itemizedoverlay = new BalloonOverlay(drawable, mMapView);

		// Add POI to the overlay
		for(PointModel p : mLocationHelper.getPois())
		{
			GeoPoint gp = p.getGeoPoint();
			int id = p.getId();
			String n = p.getName();
			String d = p.getDesc();
			itemizedoverlay.addOverlay(new OverlayItem(gp, n, d));
		}

		// Balloon stuff
		mMapView.setOnSingleTapListener(new OnSingleTapListener() {		
			@Override
			public boolean onSingleTap(MotionEvent e) {
				itemizedoverlay.hideAllBalloons();
				return true;
			}
		});

		// set iOS behavior attributes for overlay (?)
		itemizedoverlay.setShowClose(false);
		itemizedoverlay.setShowDisclosure(true);
		itemizedoverlay.setSnapToCenter(false);

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

		MenuItem searchItem = menu.findItem(R.id.actionbar_search); // TODO: Implement

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
			startActivity(new Intent(this, PoiListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		case R.id.actionbar_search:
			// TODO: Implement
			onSearchRequested();
			return true;
		case R.id.actionbar_augmented:
			startActivity(new Intent(this, AugmentedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		case R.id.actionbar_indoor:
			startActivity(new Intent(this, IndoorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		case R.id.actionbar_settings:
			startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
