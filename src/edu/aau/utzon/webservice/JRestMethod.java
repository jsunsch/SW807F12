package edu.aau.utzon.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
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

public class JRestMethod {
	private static final String K_NEAREST_URL = "http://62.243.46.141:12345";
	private static final DecimalFormat mDm = new DecimalFormat("################.################################");
	
	public static void getNearestPoints(Context context, double longitude, double latitude, int numberOfNearestNeighbours)
	{	
		String q = K_NEAREST_URL + "/KNearestPOI/" + "/" + mDm.format(latitude) + "/" + mDm.format(longitude) + "/" + numberOfNearestNeighbours;
		q = q.replace(",", ".");
		getPoints(context, q);
	}
	
	public static void getNearestPoints(Context context, double longitude, double latitude, double  radius)
	{
		String q = K_NEAREST_URL + "/KNearestPOI/" + mDm.format(longitude) + "/" + mDm.format(latitude) + "/-1/" + mDm.format(radius);
		getPoints(context, q);
	}
	
	public static void getNearestPoints(Context context, double longitude, double latitude, int numberOfNearestNeighbours, double radius)
	{	
		String q = K_NEAREST_URL + "/KNearestPOI/" + mDm.format(longitude) + "/" + mDm.format(latitude) + "/" + numberOfNearestNeighbours + "/" + mDm.format(radius);
		getPoints(context, q);
	}
	
	public static void getAllPoints(Context context)
	{ 
		String q = K_NEAREST_URL + "/AllPoints";
		getPoints(context, q);
	}
	
	public static void getPoint(Context context, int id)
	{
		String q = K_NEAREST_URL + "/GetPoint/" + id;
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
					
					String s1 = convertStreamToString(in);
					
					// Special case if just 1 item received
					s1 = s1.charAt(0) == '[' ? s1 : "[" + s1 + "]"; 
					JSONArray pointArray = new JSONArray(s1);

					JSONObject jsonObj;
					for(int i=0; i < pointArray.length(); i++){
						jsonObj = pointArray.getJSONObject(i);
						
						PointModel pm = new PointModel(	
								jsonObj.getInt("Id"),
								jsonObj.getString("Description"),
								jsonObj.getString("Name"), 
								jsonObj.getDouble("Latitude"),
								jsonObj.getDouble("Longitude"),
								System.currentTimeMillis(),
								ProviderContract.Points.STATE_UPDATING
								);
						
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
