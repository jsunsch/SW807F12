package edu.aau.utzon;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Menu;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


import edu.aau.utzon.location.LocationHelper;

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

	private static int THEME = R.style.Theme_Sherlock_Light;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Init locationHelper
		this.mLocationHelper = new LocationHelper(getApplicationContext());
		mLocationHelper.onCreate(savedInstanceState);

		// Set theme for actionbar (required)
		setTheme(THEME);

		// Remove title bar
		//
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
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
		// Display current position on map
		MapView mapView = (MapView) findViewById(R.id.mapview);
		MapController mc = mapView.getController();

		GeoPoint point =  LocationHelper.locToGeo(loc);
		mc.animateTo(point);
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
		inflater.inflate(R.layout.menu, menu);
		return true;
	}
}
