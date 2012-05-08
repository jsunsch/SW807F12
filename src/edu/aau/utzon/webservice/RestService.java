package edu.aau.utzon.webservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class RestService extends IntentService {

	public static final String COMMAND = "COMMAND";
	public static final String LOCATION_ID = "id";
	public static final int COMMAND_GET_LOCATION_POINTS = 0;
	public static final int COMMAND_GET_LOCATION_POINT = 1;

	public RestService() {
		super("RestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		int command = bundle.getInt(COMMAND);

		switch (command) {
		case COMMAND_GET_LOCATION_POINTS:  GetLocationPoints();
		break;
		}
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
