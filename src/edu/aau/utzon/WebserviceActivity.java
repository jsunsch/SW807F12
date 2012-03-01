package edu.aau.utzon;

import edu.aau.utzon.webservice.ProviderContract;
import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class WebserviceActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		// Register content observer
		this.getApplicationContext()
		.getContentResolver()
		.registerContentObserver(ProviderContract.Points.CONTENT_URI, true, new RestContentObserver(new Handler()));
        
        setContentView(R.layout.main);
        
        
        
        // DEBUGGING content provider
        String[] mProjection = {ProviderContract.Points.ATTRIBUTE_ID, 
        		ProviderContract.Points.ATTRIBUTE_X, 
        		ProviderContract.Points.ATTRIBUTE_Y, 
        		ProviderContract.Points.ATTRIBUTE_DESCRIPTION};
	
		// Query / Get
        getContentResolver().query(
            ProviderContract.Points.CONTENT_URI,   	// The content URI of the points table
            mProjection,                        	// The columns to return for each row
            null,                    				// Selection criteria
            null,                     				// Selection criteria
            null);                        			// The sort order for the returned rows

        /* Insert
		ContentValues values = new ContentValues();
		values.put(ProviderContract.Points.ATTRIBUTE_X, 0.5);
		values.put(ProviderContract.Points.ATTRIBUTE_Y, 1.5);
		values.put(ProviderContract.Points.ATTRIBUTE_DESCRIPTION, "HEjsaDescription");
		
		Uri mNewUri = getContentResolver().insert(ProviderContract.Points.CONTENT_URI, values);
		*/
        
        Log.e("Utzon", "Why you call them hoes bitches?");
    }
}

class RestContentObserver extends ContentObserver{

	public RestContentObserver(Handler handler) {
		super(handler);
	}
	
	@Override
	public boolean deliverSelfNotifications() {
		return true;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Log.e("Utzon", "Cos them hoes is bitches!");
	}
}
