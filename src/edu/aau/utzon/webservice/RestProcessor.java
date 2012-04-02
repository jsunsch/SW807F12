package edu.aau.utzon.webservice;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class RestProcessor {
	
		// Returns list of URIs with the ID of the newly inserted resource's ID
		public static List<Uri> insertLocationPoints(List<PointModel> points, Context context) {
			//TODO: Add logic for sending these points to the ContentProvider, so that they can be stored in the database.
			ArrayList<Uri> uris = new ArrayList<Uri>();
			
			Log.e("TACO", Integer.toString(points.size()));
			
			for(PointModel p : points)
			{
				uris.add(insertLocationPoint(p, context));
			}
			
			return uris;
		}
		
		// Returns an URI with the ID of the newly inserted resource's ID
		public static Uri insertLocationPoint(PointModel point, Context context) {

			ContentValues values = new ContentValues();
			values.put(ProviderContract.Points.ATTRIBUTE_ID, point.mId);
			values.put(ProviderContract.Points.ATTRIBUTE_X, point.mGeoPoint.getLatitudeE6()); // TODO: Det der getLatitude noget er jeg sq ikke helt sikker på
			values.put(ProviderContract.Points.ATTRIBUTE_Y, point.mGeoPoint.getLongitudeE6());// TODO: Det der getLongtitude noget er jeg sq ikke helt sikker på
			values.put(ProviderContract.Points.ATTRIBUTE_DESCRIPTION, point.mDesc);
			
			Uri inserted = context.getContentResolver()
					.insert(ProviderContract.Points.CONTENT_URI, values);

			return inserted;
		}
}
