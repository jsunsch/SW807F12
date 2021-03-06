package edu.aau.utzon.augmented;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorEvent;
import android.location.Location;
import android.view.View;

import com.google.android.maps.GeoPoint;


import edu.aau.utzon.R;
import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.webservice.PointModel;

public class AugmentedOverlay extends View { 
	private float[] mSensorValues;
	//private LocationAwareActivity mLocTool;
	private Location mCurrentLocation;
	private List<PointModel> mPois;
	
	public void updateOverlay(SensorEvent e, Location l)
	{
		this.mSensorValues = e.values;
		this.mCurrentLocation = l;
		//this.mLocTool = t;
		this.invalidate();
	}

	public AugmentedOverlay(Context context, List<PointModel> pois) { 
		super(context); 
		this.mPois = pois;
	}
	
	@Override 
	protected void onDraw(Canvas canvas) {	
		//LocTool mLocTool = LocTool.getLocTool(getContext());
		if(this.mCurrentLocation == null)
			return;

		Paint paint = new Paint(); 
		paint.setStyle(Paint.Style.FILL); 
		paint.setColor(Color.GREEN); 

		BitmapDrawable d = (BitmapDrawable) this.getResources().getDrawable(R.drawable.androidmarker);
		Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

		GeoPoint userLoc = LocationHelper.locToGeo(this.mCurrentLocation);


		float userLong = (float) (userLoc.getLongitudeE6()/1e6);
		float userLat = (float) (userLoc.getLatitudeE6()/1e6);

		for(PointModel poi : mPois)
		{
			float poiLong = (float) (poi.getLong());
			float poiLat = (float) (poi.getLat());


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
			double R = 6371; // earth�s radius in km
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
				// Distance between the right side of what  we see, and the right side of what we want to display (the poi) 
				float distanceMarkerView = fustrum.right-marker.right;
				// Invert the values for the perspective to work, and fit the X position to the canvas
				float realx = canvas.getWidth() - (canvas.getWidth() / (2*fovWidth)) * distanceMarkerView;

				canvas.drawBitmap(bitmap, 
						realx,
						20,		// TODO: Do something more intelligent about the height / Y-axis.
						paint);

				canvas.drawText(Integer.toString((int)distance) + " km.", realx, 15, paint); 
			}
		}
	}
}