package edu.aau.utzon.webservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class RestService extends IntentService {

	public static final String COMMAND = "COMMAND";
	
	public static final int COMMAND_GET_POI_ALL = 0;
	/** Client must supply POI_ID with the DB id of the point **/
	public static final int COMMAND_GET_POI_ID = 1;
	public static final String POI_ID = "_ID";
	
	public RestService() {
		super("RestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		int command = bundle.getInt(COMMAND);

		switch (command) {
			case COMMAND_GET_POI_ALL:
				GetLocationPoints();
				break;
			case COMMAND_GET_POI_ID:
				GetLocationPoint(bundle.getInt(POI_ID));
				break;
		}
	}

	private static final int THIRTY_MINUTES = 1000 * 60 * 30;
	private void GetLocationPoint(int id) {
		PointModel pm = PointModel.dbGetSingle(this, id);
		long timeDelta = System.currentTimeMillis() - pm.getLastModified();
		boolean isSignificantlyOld = timeDelta > THIRTY_MINUTES;
		
		if(isSignificantlyOld)
			JRestMethod.getPoint(this, id);		
	}

	private void GetLocationPoints() {
		JRestMethod.getAllPoints(this); 
	}

	/*
	 * @return boolean return true if the application can access the internet
	 */
	private boolean haveInternet(){
		NetworkInfo info = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info==null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to disable internet while roaming, just return false
			return true;
		}
		return true;
	}
	// see http://androidsnippets.com/have-internet
}
