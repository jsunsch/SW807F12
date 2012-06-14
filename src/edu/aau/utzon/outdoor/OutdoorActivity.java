package edu.aau.utzon.outdoor;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

import edu.aau.utzon.R;
import edu.aau.utzon.location.ILocationAware;
import edu.aau.utzon.location.LocationAwareMapActivity;
import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.utils.CommonIntents;
import edu.aau.utzon.webservice.PointModel;

public class OutdoorActivity extends LocationAwareMapActivity {

	//	boolean isFirstLocation = true;
	//	@Override
	//	public void serviceNewLocationBroadcast(Location location) {
	//		getSampleService().getLocationHelper().makeUseOfNewLocation(location);
	//		if(isFirstLocation && location != null && getSampleService().getLocationHelper().getCurrentLocation() != null) {
	//			drawOutdoorPois();
	//			isFirstLocation = false;
	//		}
	//	}

	private static final String TAG = "OutdoorActivity";
	MyLocationOverlay mMyLocationOverlay = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		drawMap();
		drawOutdoorPois();
	}

	TapControlledMapView mMapView;
	private void drawMap() {
		// Display Google maps to the user
		setContentView(R.layout.mapview);
		mMapView = (TapControlledMapView) findViewById(R.id.mapview);
		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMyLocationOverlay.enableMyLocation();
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.setBuiltInZoomControls(true);

	}

	boolean firstLocation = true;
	@Override
	public void serviceNewLocationBroadcast(Location location) {
		super.serviceNewLocationBroadcast(location);

		drawOutdoorPois();
	}

	private void drawOutdoorPois()
	{
		List<Overlay> mapOverlays = mMapView.getOverlays();
		if(mapOverlays.size() > 1) {
			// More overlays than just MyLocationOverlay means we have already drawn the POI's
			return;
		}
		
		// Get the POIs to draw
		List<PointModel> pmlist = PointModel.dbGetAll(this);
		if(pmlist.size() == 0) return;

		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		final BalloonOverlay itemizedoverlay = new BalloonOverlay(drawable, mMapView);

		// Add POI to the overlay
		for(PointModel p : pmlist)
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
		//}
	}




	@Override
	public void onResume()
	{
		super.onResume();
		mMyLocationOverlay.enableMyLocation();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		Log.e(TAG, "onPause");
		mMyLocationOverlay.disableMyLocation();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.e(TAG, "onStop");
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.layout.menu_outdoor, menu);
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
			Location loc = getSampleService().getLocationHelper().getCurrentLocation();
			if(loc != null)
				startActivity(CommonIntents.startAugmentedActivity(this, getSampleService().getLocationHelper().getCurrentLocation()));
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
		case R.id.actionbar_center_location:
			animateToLocation();
			return true;
		case R.id.actionbar_search:
			onSearchRequested();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void animateToLocation()
	{
		if(isBound() && mMyLocationOverlay.getLastFix() != null) {
			MapController mc = mMapView.getController();
			GeoPoint point =  LocationHelper.locToGeo(mMyLocationOverlay.getLastFix());
			mc.animateTo(point);
		}
	}

}
//
//import java.util.List;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.drawable.Drawable;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.Window;
//import android.widget.Toast;
//
//import com.actionbarsherlock.app.SherlockMapActivity;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuItem;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MyLocationOverlay;
//import com.google.android.maps.Overlay;
//import com.google.android.maps.OverlayItem;
//import com.readystatesoftware.maps.TapControlledMapView;
//import com.readystatesoftware.maps.OnSingleTapListener;
//
//
//import edu.aau.utzon.PoiContentActivity;
//import edu.aau.utzon.PoiListActivity;
//import edu.aau.utzon.R;
//import edu.aau.utzon.SettingsActivity;
//import edu.aau.utzon.augmented.AugmentedActivity;
//import edu.aau.utzon.indoor.IndoorActivity;
//import edu.aau.utzon.location.LocationAwareMapActivity;
//import edu.aau.utzon.location.LocationHelper;
//import edu.aau.utzon.location.NearPoiPublisher;
//import edu.aau.utzon.webservice.PointModel;
//
//public class OutdoorActivity extends LocationAwareMapActivity{
//	
//	private static final String TAG = "OutdoorActivity";
//	private static final String PREFS_PROXIMITY = "proximity";
//	private TapControlledMapView mMapView;
//	private MyLocationOverlay mMyLocationOverlay;
//
//	@Override
//	public void onResume()
//	{
//		super.onResume();
//		mMyLocationOverlay.enableMyLocation();
//	}
//
//	@Override
//	public void onPause()
//	{
//		super.onPause();
//		mMyLocationOverlay.disableMyLocation();
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		// Remove title bar
//		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
//			requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}
//
//		// Display Google maps to the user
//		setContentView(R.layout.mapview);
//
//		// Enable built-in map controls
//		mMapView = (TapControlledMapView) findViewById(R.id.mapview);
//		mMapView.setBuiltInZoomControls(true);
//
//		// Draw the user position on map
//		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
//		mMyLocationOverlay.enableMyLocation();
//		mMapView.getOverlays().add(mMyLocationOverlay);
//		
//		// Draw POIs
//		drawOutdoorPois();
//	}
//
//	
//	private int shownAlertId = 0;
//	private int shownToastId = 0;
//	@Override
//	public void makeUseOfNewLocation(Location location) {
//			//mLocationHelper.makeUseOfNewLocation(mMyLocationOverlay.getLastFix());	// Needed?
//		
//			LocationHelper lh = getLocationHelper();
//			
//			// Set some threshold for minimum activation distance
//			SharedPreferences settings = getSharedPreferences(PREFS_PROXIMITY, 0);
//			int proximityTreshold = settings.getInt("proximity", 20);
//			final PointModel closePoi = lh.getCurrentClosePoi();
//			double dist = lh.distToPoi(closePoi);
//			
//			if(dist < proximityTreshold) {	
//				AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				builder.setMessage("You are near a POI. Do you wish to see the content available?")
//				.setCancelable(false)
//				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//						StartPoiContentActivity(closePoi.getId());
//					}
//				})
//				.setNegativeButton("No", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//						dialog.cancel();
//					}
//				});
//				
//				// Avoid spamming the user with alert dialogs
//				if(shownAlertId == 0 || (shownAlertId != closePoi.getId())) {
//					shownAlertId = closePoi.getId();
//					AlertDialog alert = builder.create();
//					alert.show();
//				}
//			}
//			else {
//				// User not close to a POI
//				if(shownToastId == 0 || (shownToastId != closePoi.getId())) {
//					shownToastId = closePoi.getId();
//					CharSequence text = "Distance to nearest POI: " + (int)dist + " meter(s).";
//					Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
//					toast.show();
//				}
//			}
//	}
//
//	private void StartPoiContentActivity(int id) {
//		startActivity(new Intent(this, PoiContentActivity.class).putExtra("_ID", id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//	}
//
//	protected void animateToLocation(Location loc)
//	{
//		// Only animate if its a valid location
//		if(loc != null){
//			MapController mc = mMapView.getController();
//			GeoPoint point =  LocationHelper.locToGeo(loc);
//			mc.animateTo(point);
//		}
//	}
//
//	protected void drawOutdoorPois()
//	{
//		// Setup overlays
//		List<Overlay> mapOverlays = mMapView.getOverlays();
//		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
//		final BalloonOverlay itemizedoverlay = new BalloonOverlay(drawable, mMapView);
//
//		// Add POI to the overlay
//		for(PointModel p : getLocationHelper().getPois())
//		{
//			GeoPoint gp = p.getGeoPoint();
//			int id = p.getId();
//			String n = p.getName();
//			String d = p.getDesc();
//			itemizedoverlay.addOverlay(new OverlayItem(gp, n, d));
//		}
//
//		// Balloon stuff
//		mMapView.setOnSingleTapListener(new OnSingleTapListener() {		
//			@Override
//			public boolean onSingleTap(MotionEvent e) {
//				itemizedoverlay.hideAllBalloons();
//				return true;
//			}
//		});
//
//		// set iOS behavior attributes for overlay (?)
//		itemizedoverlay.setShowClose(false);
//		itemizedoverlay.setShowDisclosure(true);
//		itemizedoverlay.setSnapToCenter(false);
//
//		mapOverlays.add(itemizedoverlay);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.layout.menu_outdoor, menu);
//
//		//MenuItem searchItem = menu.findItem(R.id.actionbar_search); // TODO: Implement
//
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle item selection
//		switch (item.getItemId()) {
//		case R.id.actionbar_center_location:
//			animateToLocation(getLocationHelper().getCurrentLocation());
//			return true;
//		case R.id.actionbar_poi_list:
//			startActivity(new Intent(this, PoiListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_search:
//			// TODO: Implement
//			onSearchRequested();
//			return true;
//		case R.id.actionbar_augmented:
//			startActivity(new Intent(this, AugmentedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_indoor:
//			startActivity(new Intent(this, IndoorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_settings:
//			startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
//}
