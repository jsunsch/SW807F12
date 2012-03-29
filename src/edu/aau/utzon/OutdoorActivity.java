package edu.aau.utzon;

import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.widget.SearchView;

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


import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.location.PointOfInterest;

public class OutdoorActivity extends SherlockMapActivity {

	private LocationHelper mLocationHelper;

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

		// Draw POIs
		drawPOI();

		// Do fancy fancy animation to our current position :P
		//animateToLocation(mLocTool.getCurrentLocation());
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

	protected void drawPOI()
	{
		MapView mapView = (MapView) findViewById(R.id.mapview);

		// Setup overlays
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		GMapsOverlay itemizedoverlay = new GMapsOverlay(drawable, this);

		// Add POI to the overlay
		List<GeoPoint> pois = getPOIs();
		for(GeoPoint l : pois)
		{
			// TODO: Draw real items instead of dummies
			itemizedoverlay.addOverlay(new OverlayItem(l, "Hi, title", "This is longer..."));
		}
		mapOverlays.add(itemizedoverlay);
	}

	public List<GeoPoint> getPOIs()
	{
		ArrayList<GeoPoint> pois = new ArrayList<GeoPoint>();

		// Query webservice?
		// FIXME: Dummy POIs
		GeoPoint dummy = new GeoPoint(19240000,-99120000);
		GeoPoint dummy2 = new GeoPoint(29240000,-89120000);
		GeoPoint dummy3 = new GeoPoint(39240000,-79120000);
		GeoPoint dummy4 = new GeoPoint(2240000,-2120000);

		pois.add(dummy);
		pois.add(dummy2);
		pois.add(dummy3);
		pois.add(dummy4);

		return pois;
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
}
