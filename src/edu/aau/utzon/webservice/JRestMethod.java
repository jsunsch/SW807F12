package edu.aau.utzon.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.android.maps.GeoPoint;

public class JRestMethod {
//	private static final String BASE_URL = "http://utzonwebservice.apphb.com/";
	private static final String K_NEAREST_URL = "http://utzonwebservice.apphb.com/KNearestPOI";
	
	
	public static void getNearestPoints(Context context, double longitude, double latitude, int numberOfNearestNeighbours)
	{
		String q = K_NEAREST_URL + "/" + longitude + "/" + latitude + "/" + numberOfNearestNeighbours;
		getPoints(context, q);
	}
	
	public static void getNearestPoints(Context context, double longitude, double latitude, double  radius)
	{
		String q = K_NEAREST_URL + "/" + longitude + "/" + latitude + "/-1/" + radius;
		getPoints(context, q);
	}
	
	public static void getNearestPoints(Context context, double longitude, double latitude, int numberOfNearestNeighbours, double radius)
	{
		String q = K_NEAREST_URL + "/" + longitude + "/" + latitude + "/" + numberOfNearestNeighbours + "/" + radius;
		getPoints(context, q);
	}
	
	private static void getPoints(Context context, String query)
	{
		List<PointModel> result = new ArrayList<PointModel>();

		try {
			HttpClient client = new DefaultHttpClient();

			HttpGet request = new HttpGet(query);
			
			HttpResponse response = client.execute(request);
			
			if(response.getStatusLine().getStatusCode() == 200) {
			
				HttpEntity entity = response.getEntity();
				
				if(entity != null) {
					InputStream in = entity.getContent();
					
					JSONArray pointArray = new JSONArray(convertStreamToString(in));
					
					JSONObject jsonObj;
					for(int i=0; i < pointArray.length(); i++){
						jsonObj = pointArray.getJSONObject(i);
						
						PointModel pm = new PointModel();
						pm.mId = jsonObj.getInt("Id");
						pm.mName = jsonObj.getString("Name");
						
						
						pm.mGeoPoint = new GeoPoint((int)jsonObj.getDouble("Longitude"), (int)jsonObj.getDouble("Latitude"));
						result.add(pm);
					}
					
					in.close();
				}
			} else {
				// The requested URL responded with an error
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}

		RestProcessor.insertLocationPoints(result, context);
	}

	private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
