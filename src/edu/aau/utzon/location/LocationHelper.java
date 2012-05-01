package edu.aau.utzon.location;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import edu.aau.utzon.webservice.PointModel;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 *  Handles location operations, such as the users 
 *  current location. It offers support for following the 
 *  lifecycle of the activity through onCreate(), onResume() etc., 
 *  in addition to utility methods such as isNearPOI().
 *  
 *  TODO: Maybe a separate class to distinguish between
 *  	- Getting updates from Android/hardware
 *  	- Providing utility methods for the Activity/whoever
 *  
 *  	?
 *  	
 */
public class LocationHelper {

	protected Location mCurrentLoc;
	protected LocationListener mLocationListenerGPS;
	protected LocationListener mLocationListenerNetwork;
	protected Context mContext;
	protected NearPoiPublisher mPublisher;
	PointModel mPreviusClosePoi = null;

	public LocationHelper(Context c)
	{
		this.mContext = c;
	}

	public Location getCurrentLocation(){
		return mCurrentLoc;
	}

	protected void makeUseOfNewLocation(Location location) {
		// Update our latest record of the users position
		if(isBetterLocation(location, mCurrentLoc) || mCurrentLoc == null) {
			this.mCurrentLoc = location;

			//ArrayList<PointModel> pois = mPublisher.getPois();

			//for (PointModel p : pois) {
			//	if (isNearPoi(p, 50)) {
			//		if (mPreviusClosePoi != null) {
			//			if (mPreviusClosePoi.mId == p.mId) {
			//				return;
			//			}
			//		}
			//		mPublisher.userIsNearPoi(p);
			//		mPreviusClosePoi = p;
			//		return;
			//	}
			//}

			mPreviusClosePoi = null;
		}
	}

	public void setNearPoiPublisher(NearPoiPublisher publisher) {
		mPublisher = publisher;
	}

	public void onCreate(Bundle savedInstance) {
		// Define a listeners that responds to location updates
		this.mLocationListenerGPS  = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found.
				makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		this.mLocationListenerNetwork  = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found.
				makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Enable location manager
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListenerNetwork);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListenerGPS);

		// Set the current position to the last known position, until we have a better fixpoint
		Location latestGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location latestNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// Find best location
		if(latestGPS == null && latestNetwork != null) { this.mCurrentLoc = latestNetwork; }
		else if(latestGPS != null && latestNetwork == null) { this.mCurrentLoc = latestGPS; }
		else if(latestGPS != null && latestNetwork != null) {
			if(isBetterLocation(latestGPS, latestNetwork)) {
				this.mCurrentLoc = latestGPS;
			} else {
				this.mCurrentLoc = latestNetwork;
			}
		}
	}

	public void onResume() {
		// Resume getting location updates
		LocationManager locationManager = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListenerNetwork);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListenerGPS);
	}

	public void onPause() {
		// Save batterylife by not aquiring location data when paused
		LocationManager locationManager = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(mLocationListenerGPS);
		locationManager.removeUpdates(mLocationListenerNetwork);

	}

	protected boolean isNearPoi(PointModel point, float threshholdMeters) {

		double converted = (double)threshholdMeters / (double)1852; // converts the distance threshold to "longtitude/latitude" distance

		double pointLongtitude =  (double)point.mGeoPoint.getLongitudeE6() / (double)1000000;
		double pointLattitude = (double)point.mGeoPoint.getLatitudeE6() / (double)1000000;

		double dist = distFrom(pointLongtitude, pointLattitude, mCurrentLoc.getLongitude(), mCurrentLoc.getLatitude());

		if (dist < threshholdMeters) {
			return true;
		}

		//Log.e("TACO", Double.toString(dist));

		//if (point.geoPoint.getLatitudeE6())

		return false;
	}

	private double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return new Double(dist * meterConversion).doubleValue();
	}

	protected List<Location> knearestPOI(List<Location> query, int k)
	{
		ArrayList<Location> result = new ArrayList<Location>();

		List<Location> qtemp = query;
		for(int i=0;i<=k;i++)
		{
			Location nearest = nearestPOI(qtemp);
			if(nearest != null)
			{
				result.add(nearest);
				// Dont allow duplicates in the resulting set
				// i.e. dont loop on the same data so we avoid getting k equal elements in the result
				qtemp.remove(nearest);
			}
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
			if(locDist < closestDist ) { 
				closestDist = locDist; 
				result = loc; 
			}
		}

		return result;
	}

	public List<Location> getPOIs()
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

	static public GeoPoint locToGeo(Location loc)
	{
		// Location -> GeoPoint conversion
		return new GeoPoint((int)(loc.getLatitude()*1e6),(int)(loc.getLongitude()*1e6));
	}

	protected void updateUserLocation(Location loc) {
		// Update our location
		if(isBetterLocation(loc, mCurrentLoc))
		{
			mCurrentLoc = loc;
			//locationUpdateEvent();
		}
		else{ /* New location not better than current */ }
	}

	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
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

	// Callback for location sensor
	// Subclasses should implement this to update UI when a new data is available
	//protected abstract void locationUpdateEvent();

}
