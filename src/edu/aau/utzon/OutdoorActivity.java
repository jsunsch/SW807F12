package edu.aau.utzon;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.aau.utzon.location.LocTool;

public class OutdoorActivity extends MapActivity {
	
	// Tool for handling most location oriented operations
	private LocTool mLocTool;

	@Override
	public void onResume()
	{
		super.onResume();
		mLocTool.onResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		mLocTool.onPause();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize location tool with the context of this context
        mLocTool = new LocTool(getApplicationContext());
        mLocTool.onCreate();
        
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
    	animateToLocation(mLocTool.getCurrentLocation());
    }
    
    protected void animateToLocation(Location loc)
    {
    	// Display current position on map
    	MapView mapView = (MapView) findViewById(R.id.mapview);
    	MapController mc = mapView.getController();
    	
    	GeoPoint point =  mLocTool.locToGeo(loc);
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
    	GeoPoint point = new GeoPoint(19240000,-99120000);
    	OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
    	itemizedoverlay.addOverlay(overlayitem);
    	
    	GeoPoint point2 = new GeoPoint(17000000,-90000000);
    	OverlayItem overlayitem2 = new OverlayItem(point2, "Hola, Mundo!", "I'm in Mexico City!");
    	itemizedoverlay.addOverlay(overlayitem2);
    	
    	List<Location> pois = mLocTool.getPOIs();
    	for(Location l : pois)
    	{
    		// TODO: Draw the real POI's, not just dummies
    	}
    	mapOverlays.add(itemizedoverlay);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
