package edu.aau.utzon;

import java.util.List;

import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;
import edu.aau.utzon.webservice.RestMethod;
import edu.aau.utzon.webservice.RestServiceHelper;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

public class WebserviceActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //List<PointModel> lulz = RestMethod.getAllPoints();
        
        RestServiceHelper.getServiceHelper().doBindService(this);
        
        // DEBUGGING content provider
     // Queries the user dictionary and returns results
        //String[] mProjection = {ProviderContract.Points.TABLE_NAME};
		
		//Cursor mCursor = getContentResolver().query(
        //    ProviderContract.Points.CONTENT_URI,   // The content URI of the points table
        //    mProjection,                        // The columns to return for each row
        //    null,                    // Selection criteria
        //    null,                     // Selection criteria
        //    null);                        // The sort order for the returned rows
		
		
		//getContentResolver().insert(ProviderContract.Points.CONTENT_URI, values)
		
		//int everythingwentbetterthanexpected = 5+5+5+5+5;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //doUnbindService();
    }
}
