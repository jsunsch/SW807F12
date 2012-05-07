package edu.aau.utzon.outdoor;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
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
import edu.aau.utzon.augmented.AugmentedActivity;
import edu.aau.utzon.indoor.IndoorActivity;
import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.location.NearPoiPublisher;
import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestServiceHelper;

public class OutdoorActivity extends SherlockMapActivity implements NearPoiPublisher {

	private LocationHelper mLocationHelper;
	private Cursor mOutdoorPois;
	private TapControlledMapView mMapView;
	private MyLocationOverlay mMyLocationOverlay;

//	public final static  String[] mProjectionAll = {ProviderContract.Points.ATTRIBUTE_ID, 
//		ProviderContract.Points.ATTRIBUTE_X, 
//		ProviderContract.Points.ATTRIBUTE_Y, 
//		ProviderContract.Points.ATTRIBUTE_DESCRIPTION,
//		ProviderContract.Points.ATTRIBUTE_NAME};

	@Override
	public void onResume()
	{
		super.onResume();
		mMyLocationOverlay.enableMyLocation();
		mLocationHelper.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mMyLocationOverlay.disableMyLocation();
		mLocationHelper.onPause();
	}
	
	private final static  String[] ProjectionAllPOI = {
		ProviderContract.Points.ATTRIBUTE_ID, 
		ProviderContract.Points.ATTRIBUTE_X, 
		ProviderContract.Points.ATTRIBUTE_Y, 
		ProviderContract.Points.ATTRIBUTE_DESCRIPTION,
		ProviderContract.Points.ATTRIBUTE_LAST_MODIFIED,
		ProviderContract.Points.ATTRIBUTE_NAME,
		ProviderContract.Points.ATTRIBUTE_STATE};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Init locationHelper
		this.mLocationHelper = new LocationHelper(this);
		this.mLocationHelper.setNearPoiPublisher(this);
		mLocationHelper.onCreate(savedInstanceState);
		
		// Get available POIs
		mOutdoorPois = getContentResolver().query(ProviderContract.Points.CONTENT_URI, ProjectionAllPOI, null, null, null);

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

		// Draw POI
		drawOutdoorPois(PointModel.asPointModel(mOutdoorPois));
	}
	
	public void userIsNearPoi(PointModel poi) {
		Log.e("TACO", "IS NEAR POI");
		StartPoiContentActivity();
	}
	
	private void StartPoiContentActivity() {
		startActivity(new Intent(getApplicationContext(), PoiContentActivity.class));
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

	public void updateOutdoorPois(ArrayList<PointModel> pois) {
		drawOutdoorPois(pois);
	}

	protected void drawOutdoorPois(List<PointModel> list)
	{
		//MapView mapView = (MapView) findViewById(R.id.mapview);

		// Setup overlays
		List<Overlay> mapOverlays = mMapView.getOverlays();
		//mapOverlays.clear(); ?
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		final BalloonOverlay itemizedoverlay = new BalloonOverlay(drawable, mMapView);
		//		GMapsOverlay itemizedover)lay = new GMapsOverlay(drawable, this);

		// Add POI to the overlay
		for(PointModel p : list)
		{
			itemizedoverlay.addOverlay(new OverlayItem(p.mGeoPoint, p.mName, p.mDesc));
		}

		// Ballon stuff
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
			Intent poiListIntent = new Intent(this, PoiListActivity.class);
			startActivity(poiListIntent);
			return true;
		case R.id.actionbar_search:
			// TODO: Implement
			onSearchRequested();
			return true;
		case R.id.actionbar_augmented:
			Intent augmentedIntent = new Intent(this, AugmentedActivity.class);
			startActivity(augmentedIntent);
			return true;
		case R.id.actionbar_indoor:
			Intent indoorIntent = new Intent(this, IndoorActivity.class);
			startActivity(indoorIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public List<PointModel> getPois() {
		return PointModel.asPointModel(mOutdoorPois);
	}
}
