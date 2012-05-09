package edu.aau.utzon.webservice;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class RestProcessor {
	
		private static final String TAG = "RestProcessor";

		// Returns list of URIs with the ID of the newly inserted resource's ID
		public static List<Uri> insertLocationPoints(List<PointModel> points, Context context) {
			ArrayList<Uri> uris = new ArrayList<Uri>();
			Log.i(TAG, "Inserting " + points.size() + "POIs");
			for(PointModel p : points)
			{
				uris.add(insertLocationPoint(p, context));
			}			
			return uris;
		}
		
		// Returns an URI with the ID of the newly inserted resource's ID
		public static Uri insertLocationPoint(PointModel point, Context context) {

			ContentValues values = new ContentValues();
			values.put(ProviderContract.Points.ATTRIBUTE_ID, point.getId());
			values.put(ProviderContract.Points.ATTRIBUTE_LAT, point.getLat()); 
			values.put(ProviderContract.Points.ATTRIBUTE_LONG, point.getLong());
			values.put(ProviderContract.Points.ATTRIBUTE_DESCRIPTION, point.getDesc());
			values.put(ProviderContract.Points.ATTRIBUTE_NAME, point.getName());
			values.put(ProviderContract.Points.ATTRIBUTE_LAST_MODIFIED, System.currentTimeMillis());
			values.put(ProviderContract.Points.ATTRIBUTE_STATE, ProviderContract.Points.STATE_OK);
			
			Uri inserted = context.getContentResolver()
					.insert(ProviderContract.Points.CONTENT_URI, values);

			return inserted;
		}
}
