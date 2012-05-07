package edu.aau.utzon.webservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.maps.GeoPoint;

public class PointModel implements Parcelable {
	public GeoPoint mGeoPoint;
	public int mId;
	public String mDesc;
	public String mName;

	public PointModel() {
	}
	
	public PointModel(int id, String desc, String name, int lat, int lg)
	{
		this.mId = id;
		this.mDesc = desc;
		this.mName = name;
		this.mGeoPoint = new GeoPoint(lat, lg);
	}
	
	static public List<PointModel> asPointModel (Cursor c){
		List<PointModel> result = new ArrayList<PointModel>();
		
		c.moveToFirst();
		do
		{
			int colIndexId = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_ID);
			int colIndexDesc = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_DESCRIPTION);
			int colIndexX = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_X);
			int colIndexY = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_Y);
			int colIndexName = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_NAME);

			int id = c.getInt(colIndexId);
			String desc = c.getString(colIndexDesc);
			float x = c.getFloat(colIndexX);
			float y = c.getFloat(colIndexY);
			String name = c.getString(colIndexName);
			
			PointModel p = new PointModel();
			p.mDesc = desc;
			p.mId = id;
			p.mGeoPoint = new GeoPoint((int)x,(int)y);
			p.mName = name;

			result.add(p);

		} while (c.moveToNext() == true);
		
		return result;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mGeoPoint.getLatitudeE6());
		dest.writeInt(mGeoPoint.getLongitudeE6());
		dest.writeInt(mId);
		dest.writeString(mDesc);
		dest.writeString(mName);
	}

	// this is used to regenerate your object. All Parcelables must have a
	// CREATOR that implements these two methods
	public static final Parcelable.Creator<PointModel> CREATOR = new Parcelable.Creator<PointModel>() {
		public PointModel createFromParcel(Parcel in) {
			return new PointModel(in);
		}

		public PointModel[] newArray(int size) {
			return new PointModel[size];
		}
	};

	// Constructor that takes a Parcel and gives you an object populated with it's values
	private PointModel(Parcel in) {
		int lat = in.readInt();
		int longitude = in.readInt();
		mGeoPoint = new GeoPoint(lat, longitude);
		mId = in.readInt();
		mDesc = in.readString();
		mName = in.readString();
	}
}
