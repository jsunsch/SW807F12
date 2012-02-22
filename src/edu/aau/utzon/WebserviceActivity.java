package edu.aau.utzon;

import java.util.List;

import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestMethod;
import edu.aau.utzon.webservice.RestServiceHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class WebserviceActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //List<PointModel> lulz = RestMethod.getAllPoints();
        
        RestServiceHelper.getServiceHelper().getLocationPoints(this);

        
        
        // DEBUGGING content provider
        //String[] mProjection = {ProviderContract.Points.ATTRIBUTE_ID, ProviderContract.Points.ATTRIBUTE_X, ProviderContract.Points.ATTRIBUTE_Y, ProviderContract.Points.ATTRIBUTE_DESCRIPTION};
	
		// Query / Get
        //Cursor mCursor = getContentResolver().query(
         //   ProviderContract.Points.CONTENT_URI,   	// The content URI of the points table
          //  mProjection,                        	// The columns to return for each row
           // null,                    				// Selection criteria
           // null,                     				// Selection criteria
           // null);                        			// The sort order for the returned rows

        // Insert
		//ContentValues values = new ContentValues();
		//values.put(ProviderContract.Points.ATTRIBUTE_X, 0.5);
		//values.put(ProviderContract.Points.ATTRIBUTE_Y, 1.5);
		//values.put(ProviderContract.Points.ATTRIBUTE_DESCRIPTION, "HEjsaDescription");
		
		//Uri mNewUri = getContentResolver().insert(ProviderContract.Points.CONTENT_URI, values);
		
		//int everythingwentbetterthanexpected = 5+5+5+5+5;
		//everythingwentbetterthanexpected++;
    }
}
