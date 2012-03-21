package edu.aau.utzon.location;

public class PointOfInterest {
	String mLocationName;
	int mProximity; // Proximity in meters

	public int getmProximity() {
		return mProximity;
	}

	public void setmProximity(int mProximity) {
		this.mProximity = mProximity;
	}

	public String getmLocationName() {
		return mLocationName;
	}

	public void setmLocationName(String mLocationName) {
		this.mLocationName = mLocationName;
	}
}
