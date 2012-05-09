package edu.aau.utzon.webservice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class PointModel {
	private static final String TAG = "PointModel";
	private GeoPoint mGeoPoint;
	private int mId;
	private String mDesc;
	private String mName;
	private double mLong;
	private double mLat;
	private long mLastModified;
	private int mState;

	public int getId() {
		return mId;
	}
	
	public double getLong() {
		return mLong;
	}
	public double getLat() {
		return mLat;
	}
	public String getDesc() {
		return mDesc;
	}
	public String getName() {
		return mName;
	}
	public GeoPoint getGeoPoint() {
		return mGeoPoint;
	}
	public long getLastModified() {
		return mLastModified;
	}
	public int getState() {
		return mState;
	}
	
	public PointModel(int id, String desc, String name, double lat, double lg, long lastModified, int state)
	{
		this.mId = id;
		this.mDesc = desc;
		this.mName = name;
		this.mLat = lat;
		this.mLong = lg;
		this.mGeoPoint = new GeoPoint((int)(lg*1e6), (int)(lat*1e6));
		this.mLastModified = lastModified;
		this.mState = state;
	}
	
	static public List<PointModel> dbGetAll(Context c)
	{
		return asPointModels(c.getContentResolver()
				.query(	ProviderContract.Points.CONTENT_URI, 
						ProviderContract.Points.PROJECTIONSTRING_ALL, 
						null, null, null));
	}
	
	static public PointModel dbGetSingle(Context c, int id)
	{
		String selection = ProviderContract.Points.ATTRIBUTE_ID + "=" + id;
		return asPointModel(c.getContentResolver()
				.query(	ProviderContract.Points.CONTENT_URI, 
						ProviderContract.Points.PROJECTIONSTRING_ALL, 
						selection, null, null));
	}
	
	static public List<PointModel> asPointModels (Cursor c){
		List<PointModel> result = new ArrayList<PointModel>();
		
		c.moveToFirst();
		do
		{
			int colIndexId = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_ID);
			int colIndexDesc = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_DESCRIPTION);
			int colIndexLat = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_LAT);
			int colIndexLong = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_LONG);
			int colIndexName = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_NAME);
			int colIndexLastModified = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_LAST_MODIFIED);
			int colIndexState = c.getColumnIndex(ProviderContract.Points.ATTRIBUTE_STATE);

			int id = c.getInt(colIndexId);
			String desc = c.getString(colIndexDesc);
			double lat = c.getDouble(colIndexLat);
			double lg = c.getDouble(colIndexLong);
			String name = c.getString(colIndexName);
			long lastModified = c.getLong(colIndexLastModified);
			int state = c.getInt(colIndexState);
			
			PointModel p = new PointModel(id, desc, name, lat, lg, lastModified, state);
			result.add(p);

		} while (c.moveToNext() == true);
		
		c.close();
		return result;
	}

	static private PointModel asPointModel(Cursor query) {
		List<PointModel> all = asPointModels(query);
		if( all.size() > 1 )
			Log.e(TAG, "Should use asPointModels instead of asPointModel");
		if( all.size() == 0 )
			Log.e(TAG, "No elements in cursor");
		
		return all.get(0);
	}

//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		// TODO Auto-generated method stub
//		dest.writeInt(mGeoPoint.getLatitudeE6());
//		dest.writeInt(mGeoPoint.getLongitudeE6());
//		dest.writeInt(mId);
//		dest.writeString(mDesc);
//		dest.writeString(mName);
//	}
//
//	// this is used to regenerate your object. All Parcelables must have a
//	// CREATOR that implements these two methods
//	public static final Parcelable.Creator<PointModel> CREATOR = new Parcelable.Creator<PointModel>() {
//		public PointModel createFromParcel(Parcel in) {
//			return new PointModel(in);
//		}
//
//		public PointModel[] newArray(int size) {
//			return new PointModel[size];
//		}
//	};
//
//	// Constructor that takes a Parcel and gives you an object populated with it's values
//	private PointModel(Parcel in) {
//		int lat = in.readInt();
//		int longitude = in.readInt();
//		mGeoPoint = new GeoPoint(lat, longitude);
//		mId = in.readInt();
//		mDesc = in.readString();
//		mName = in.readString();
//	}
}
