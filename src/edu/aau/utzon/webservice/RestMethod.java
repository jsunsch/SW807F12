package edu.aau.utzon.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/*
 * <RESULT>
 * 	<POINT>
 * 		<ID>1</ID>
 * 		<X>1.55</X>
 *		<Y>2.9</Y>
 * 		<DESCRIPTION>hej</DESCRIPTION>
 * 	</POINT>
 * </RESULT>
 */

public class RestMethod {
	// XML nodes
	private static final String RESULT = "RESULT";
	private static final String POINT = "POINT";
	private static final String ID = "ID";
	private static final String X = "X";
	private static final String Y = "Y";
	private static final String DESCRIPTION = "DESCRIPTION";
	
	private static final String BASE_URL = "http://utzon.apphb.com";
	
	public static void getPoint(Context context, int id)
	{
		HttpGet request = new HttpGet(BASE_URL + "/Point/" + id);
		PointModel result = new PointModel();
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(request);
			InputStream in = response.getEntity().getContent();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			
			int eventType = parser.getEventType();
	        boolean done = false;
	        PointModel point = new PointModel();
			
	        // PARSE!
	        while (eventType != XmlPullParser.END_DOCUMENT && !done){
	        	String node = null;
	        	switch (eventType){
		        	case XmlPullParser.START_TAG:
	            		node = parser.getName();
	            		if(node.equalsIgnoreCase(ID))
	            		{
	            			point.id = Integer.valueOf(parser.nextText().trim()).intValue();
	            		}
	            		else if(node.equalsIgnoreCase(X))
	            		{
	            			point.x = Float.valueOf(parser.nextText().trim()).floatValue();
	            		}
	            		else if(node.equalsIgnoreCase(Y))
	            		{
	            			point.y = Float.valueOf(parser.nextText().trim()).floatValue();
	            		}
	            		else if(node.equalsIgnoreCase(DESCRIPTION))
	            		{
	            			point.description = parser.nextText();
	            		}
	            		else if(node.equalsIgnoreCase(RESULT))
	            		{
	            			parser.next();
	            		}
	            		break;
		        	case XmlPullParser.END_TAG:
	            		node = parser.getName();
	            		if(node.equalsIgnoreCase(RESULT))
	            		{
	            			done = true;
	            		}
	            		break;
	        	}
	        }
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RestProcessor.insertLocationPoint(result, context);
	}
	
	public static void getAllPoints(Context context)
	{
		HttpGet request = new HttpGet(BASE_URL + "/Point");
		ArrayList<PointModel> result = new ArrayList<PointModel>();
		
		try {			
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(request);
			InputStream in = response.getEntity().getContent();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			
			int eventType = parser.getEventType();
	        boolean done = false;
	        PointModel currentPoint = new PointModel();
			
	        // PARSE!
	        while (eventType != XmlPullParser.END_DOCUMENT && !done){
	        	String node = null;
	        	switch (eventType){
	            	case XmlPullParser.START_TAG:
	            		node = parser.getName();
	            		if(node.equalsIgnoreCase(POINT))
	            		{
	            			currentPoint = new PointModel();
	            		}
	            		else if(node.equalsIgnoreCase(ID))
	            		{
	            			currentPoint.id = Integer.valueOf(parser.nextText().trim()).intValue();
	            		}
	            		else if(node.equalsIgnoreCase(X))
	            		{
	            			currentPoint.x = Float.valueOf(parser.nextText().trim()).floatValue();
	            		}
	            		else if(node.equalsIgnoreCase(Y))
	            		{
	            			currentPoint.y = Float.valueOf(parser.nextText().trim()).floatValue();
	            		}
	            		else if(node.equalsIgnoreCase(DESCRIPTION))
	            		{
	            			currentPoint.description = parser.nextText();
	            		}
	            		else if(node.equalsIgnoreCase(RESULT))
	            		{
	            			parser.next();
	            		}
	            		break;
	            	case XmlPullParser.END_TAG:
	            		node = parser.getName();
	            		if(node.equalsIgnoreCase(POINT))
	            		{
	            			result.add(currentPoint);
	            		}
	            		else if(node.equalsIgnoreCase(RESULT))
	            		{
	            			done = true;
	            		}
	            		break;
	        	}
	        	eventType = parser.next();
	        }
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        RestProcessor.insertLocationPoints(result, context);
	}
}
