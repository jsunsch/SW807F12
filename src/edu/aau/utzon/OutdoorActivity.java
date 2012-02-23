package edu.aau.utzon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class OutdoorActivity extends MapActivity {
	
	private Location mCurrentLoc;
	// Define a listener that responds to location updates
	private LocationListener mLocationListener  = new LocationListener() {
        public void onLocationChanged(Location location) {
          // Called when a new location is found.
          makeUseOfNewLocation(location);
        }

		public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
      };
	
	@Override
	public void onResume()
	{
		super.onResume();
    	
		// Resume getting location updates
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		// Save batterylife by not aquiring location data when paused
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(mLocationListener);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        
    	// Enable built-in map controls
    	MapView mapView = (MapView) findViewById(R.id.mapview);
    	mapView.setBuiltInZoomControls(true);
    	
    	// Draw the user position on map
    	MyLocationOverlay locationOverlay = new MyLocationOverlay(this, mapView);
    	locationOverlay.enableMyLocation();
    	mapView.getOverlays().add(locationOverlay);

        // Enable location manager
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        
        // Set the current position to the last known position, until we have a better fixpoint
    	mCurrentLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
          
    	// Draw POIs
    	drawPOI(getPOIs());
    }
    
    protected void gotoPointOnMap(Location loc)
    {
    	// Display current position on map
    	MapView mapView = (MapView) findViewById(R.id.mapview);
    	MapController mc = mapView.getController();
    	
    	GeoPoint point =  locToGeo(loc);
    	mc.animateTo(point);
    }
    
    protected GeoPoint locToGeo(Location loc)
    {
    	// Location -> GeoPoint conversion
    	return new GeoPoint((int)(loc.getLatitude()*1e6),(int)(loc.getLongitude()*1e6));
    }
    
    /**
     * Called by the LocationListener, whenever a new fixpoint is received
     * @param The new location received
     */
    protected void makeUseOfNewLocation(Location loc)
    {
    	// Update our location
    	if(isBetterLocation(loc, mCurrentLoc))
    	{
	    	mCurrentLoc = loc;
	    	//gotoPointOnMap(currentLoc);
	    	//drawPOI();
    	}
    	else{ /* New location not better than current */ }
    }
    
    protected void drawPOI(List<Location> pois)
    {
    	MapView mapView = (MapView) findViewById(R.id.mapview);
    	
        // Setup overlays
    	List<Overlay> mapOverlays = mapView.getOverlays();
    	Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
    	PoiOverlay itemizedoverlay = new PoiOverlay(drawable, this);
    	
    	// Add POI to the overlay
    	GeoPoint point = new GeoPoint(19240000,-99120000);
    	OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
    	itemizedoverlay.addOverlay(overlayitem);
    	
    	GeoPoint point2 = new GeoPoint(17000000,-90000000);
    	OverlayItem overlayitem2 = new OverlayItem(point2, "Hola, Mundo!", "I'm in Mexico City!");
    	itemizedoverlay.addOverlay(overlayitem2);
    	
    	for(Location l : pois)
    	{
    		// TODO: Draw the real POI's, not just dummies
    	}
    	mapOverlays.add(itemizedoverlay);
    }
    
    protected boolean isNearPOI(List<Location> pois)
    {
    	boolean result = false;
    	for(Location p : pois)
    	{
    		if(p.getLatitude() <= mCurrentLoc.getLatitude()+1 && p.getLatitude() >= mCurrentLoc.getLatitude()-1)
    		{
    			if(p.getLongitude() <= mCurrentLoc.getLongitude()+1 && p.getLongitude() >= mCurrentLoc.getLongitude()-1)
    			{
    				result = true;
    			}
    		}
    	}
    	
    	return result;
    }
    
    protected List<Location> knearestPOI(List<Location> query, int k)
    {
    	ArrayList<Location> result = new ArrayList<Location>();
    	
    	List<Location> qtemp = query;
    	for(int i=0;i<=k;i++)
    	{
    		Location nearest = nearestPOI(qtemp);
    		result.add(nearest);
    		// Dont allow duplicates in the resulting set
    		// i.e. dont loop on the same data so we avoid getting k equal elements in the result
    		qtemp.remove(nearest);
    	}
    	return result;
    }
    
    protected Location nearestPOI(List<Location> query) {
    	Location result = null;
    
    	float closestDist = 1000000;
    	for(Location loc : query)
    	{
    		// In meters
    		float locDist = loc.distanceTo(mCurrentLoc);
    		
    		// New closest found
    		if(locDist < closestDist ) { closestDist = locDist; result = loc; }
    	}
    	
		return result;
	}

	protected List<Location> getPOIs()
    {
    	ArrayList<Location> pois = new ArrayList<Location>();
    	if(mCurrentLoc != null)
    	{
	    	// Query webservice?
	    	// FIXME: Dummy POIs
	    	Location dummy = new Location(mCurrentLoc);
	    	// pois.add(dummy);
	    	dummy.setLatitude(dummy.getLatitude()+10);
	    	pois.add(dummy);
	    	dummy.setLongitude(dummy.getLongitude()+20);
	    	pois.add(dummy);
    	}
    	
    	
    	return pois;
    }

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
