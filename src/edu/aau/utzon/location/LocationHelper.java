package edu.aau.utzon.location;

import java.util.List;

import com.google.android.maps.GeoPoint;

import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;
import android.content.Context;
import android.location.Location;

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

	private static final String TAG = "LocationHelper";
	//private Context mContext = null;
	//private LocationManager mLocationManager = null;
	//private LocationListener mLocationListener = null;
	private Location mCurrentLoc = null;
	private PointModel mCurrentClosePoi = null;
	private PointModel mPreviusClosePoi = null;
	private List<PointModel> mPois = null;
	
	public double distToPoi(PointModel poi)
	{
		double userLat = mCurrentLoc.getLatitude()/1e6;
		double userLong = mCurrentLoc.getLongitude()/1e6;
		return distFrom(userLat, userLong, poi.getLat(), poi.getLong());
	}
	
	public void updateUserLocation(Location l)
	{
		if(isBetterLocation(l, mCurrentLoc) || mCurrentLoc == null) {
			mCurrentLoc = l;
			mPreviusClosePoi = mCurrentClosePoi;
			mCurrentClosePoi = nearestPOI(mPois, mCurrentLoc);
		}
	}
	
	public LocationHelper(Context c){
		// Get available POIs
		//mContext = c;
		mPois = PointModel.asPointModels(c.getContentResolver()
				.query(	ProviderContract.Points.CONTENT_URI, 
						ProviderContract.Points.PROJECTIONSTRING_ALL, 
						null, null, null));
	}

	public Location getCurrentLocation(){
		return mCurrentLoc;
	}
	
	public PointModel getCurrentClosePoi(){
		return mCurrentClosePoi;
	}
	
	public void makeUseOfNewLocation(Location location) {
		// Update our latest record of the users position
		if(isBetterLocation(location, mCurrentLoc) || mCurrentLoc == null) {
			mCurrentLoc = location;
			mPreviusClosePoi = mCurrentClosePoi;
			mCurrentClosePoi = nearestPOI(mPois, mCurrentLoc);
		}
	}
	
	public List<PointModel> getPois() {
		return mPois;
	}
	
	static public double distFrom(double lat1, double lng1, double lat2, double lng2) {
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

//	private List<PointModel> knearestPOI(List<PointModel> query, int k)
//	{
//		//ArrayList<Location> result = new ArrayList<Location>();
//
//		List<PointModel> qtemp = query;
//		for(int i=0;i<=k;i++)
//		{
//			Location nearest = nearestPOI(qtemp);
//			if(nearest != null)
//			{
//				result.add(nearest);
//				// Dont allow duplicates in the resulting set
//				// i.e. dont loop on the same data so we avoid getting k equal elements in the result
//				qtemp.remove(nearest);
//			}
//		}
//		return result;
//	}

	private PointModel nearestPOI(List<PointModel> pois, Location loc) {
		//Location result = null;
		
		PointModel result = null;
		double closestDist = Math.pow(2, 64);

			for(PointModel p : pois)
			{
				// In meters
				double locDist = distFrom(p.getLat(), p.getLong(), loc.getLatitude()/1e6, loc.getLongitude()/1e6);
	
				// New closest found
				if(locDist < closestDist ) { 
					closestDist = locDist; 
					result = p; 
				}
			}
		
		return result;
	}

	static public GeoPoint geoToE6(GeoPoint gp)
	{
		return new GeoPoint((int)(gp.getLatitudeE6()*1e6),(int)(gp.getLongitudeE6()*1e6));
	}
	

	static public GeoPoint locToGeo(Location loc)
	{
		// Location -> GeoPoint conversion
		return new GeoPoint((int)(loc.getLatitude()*1e6),(int)(loc.getLongitude()*1e6));
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


}
