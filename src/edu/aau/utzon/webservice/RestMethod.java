package edu.aau.utzon.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;


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
	
	public static List<PointModel> getAllPoints()
	{
		String insertUrl = BASE_URL + "/Point";
		
		ArrayList<PointModel> result = new ArrayList<PointModel>();
		try {
			// Establish connection
			URL myUrl = new URL(insertUrl);
			InputStream in = myUrl.openConnection().getInputStream();
			// Prepare to parse reseponse
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
		
        return result;
	}
}
