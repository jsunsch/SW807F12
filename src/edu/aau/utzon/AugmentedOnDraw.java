package edu.aau.utzon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorEvent;
import android.view.View;

import com.google.android.maps.GeoPoint;

import edu.aau.utzon.location.LocTool;

public class AugmentedOnDraw extends View { 
	private float[] mSensorValues;

	public void updateOverlay(SensorEvent e)
	{
		this.invalidate();
		mSensorValues = e.values;
	}

	public AugmentedOnDraw(Context context) { 
		super(context); 
	}

	@Override 
	protected void onDraw(Canvas canvas) {	
		LocTool mLocTool = LocTool.getLocTool(getContext());

		// TODO Auto-generated method stub 
		Paint paint = new Paint(); 
		paint.setStyle(Paint.Style.FILL); 
		paint.setColor(Color.GREEN); 
		

		BitmapDrawable d = (BitmapDrawable) this.getResources().getDrawable(R.drawable.androidmarker);
		Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

		//TODO: Dummy POI
		GeoPoint poi = new GeoPoint(17000000,-90000000);
		
		GeoPoint userLoc = mLocTool.locToGeo(mLocTool.getCurrentLocation());

		float poiLong = (float) (poi.getLongitudeE6()/1e6 +180 );
		float poiLat = (float) (poi.getLatitudeE6()/1e6);
		float userLong = (float) (userLoc.getLongitudeE6()/1e6 +180);
		float userLat = (float) (userLoc.getLatitudeE6()/1e6);

		// Rectangle for the current view, what the user sees
		// SensorValues[0] azimuth : 0-360
		// SensorValues[1] pitch : -180 - 180
		// SensorValues[2] roll : -90 - 90
		int fLeft = 0;
		int fBottom = 180;
		int fTop = 0;
		int fRight = 20;
		
		final int fovWidth = 40/2;
		final int fovHeight = 180;
		
		if(mSensorValues[0] > fovWidth) { 
			fLeft = (int) (mSensorValues[0] - fovWidth);
			fRight = (int) (mSensorValues[0] + fovWidth);
			}
		if( (mSensorValues[2] + 90) > fovHeight) { 
			fBottom = (int) (mSensorValues[2] + 90 + fovHeight);
			fTop = (int) (mSensorValues[2] + 90 - fovHeight);
			}
		
		Rect fustrum = new Rect(fLeft, 
				fTop, 
				fRight, 
				fBottom);

		// Angle to POI (Forward azimuth)
		double y = Math.sin(Math.abs(poiLong-userLong)) * Math.cos(poiLat);
		double x = Math.cos(userLat)*Math.sin(poiLat) -
		        Math.sin(userLat)*Math.cos(poiLat)*Math.cos(Math.abs(poiLong-userLong));
		double brng = Math.toDegrees(Math.atan2(y, x));
		
		// Distance to POI (Haversine)
		double R = 6371; // km
		double dLat = Math.toRadians(poiLat-userLat);
		double dLon = Math.toRadians(poiLong-userLong);
		double lat1 = Math.toRadians(userLat);
		double lat2 = Math.toRadians(poiLat);

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double distance = R * c;
		
		
		Rect marker = new Rect((int)(brng), 0, (int)(brng), 180);
		
		if(marker.intersect(fustrum))
		{
			// Distance between the ride side of what  we see, and the right side of what we want to display (the poi) 
			float distanceMarkerView = fustrum.right-marker.right;
			// Invert the values for the perspective to work
			float realx = canvas.getWidth() - (canvas.getWidth() / (2*fovWidth)) * distanceMarkerView;
			
			canvas.drawBitmap(bitmap, 
					realx,
					20,		// Do something more intelligent about the height / Y-axis.
					paint);
			
			canvas.drawText(Integer.toString((int)distance), realx, 15, paint); 
		}
	} 
}