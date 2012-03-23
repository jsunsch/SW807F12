package edu.aau.utzon.location;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Note that this is a temporary class. A better class will propably be created later
public class PointOfInterest implements Serializable {
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
	
	public PointOfInterest(String locationName, int proximity) {
		mLocationName = locationName;
		mProximity = proximity;
	}
}
