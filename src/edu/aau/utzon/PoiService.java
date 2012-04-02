package edu.aau.utzon;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.webservice.RestService;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class PoiService extends IntentService {

	public static final String COMMAND = "COMMAND";
	public static final String USER_LOCATION_X = "user_location_x";
	public static final String USER_LOCATION_Y = "user_location_y";
	public static final int COMMAND_START_SERVICE = 0;
	public static final int COMMAND_STOP_SERVICE = 1;
	public static final String POI_INTENTFILTER = "poi_notification";
	
	private boolean isRunning = false;
	private GeoPoint mCurrentPosition;
	
	public PoiService() {
		super("PoiService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		int command = bundle.getInt(COMMAND);


		switch (command) {
		case COMMAND_START_SERVICE:
			if(!isRunning) {
				isRunning = true;
				int x = bundle.getInt(USER_LOCATION_X);
				int y = bundle.getInt(USER_LOCATION_Y);
				mCurrentPosition = new GeoPoint(x, y);
				runNotification();
			}
			break;
		case COMMAND_STOP_SERVICE:
			stopNotification();
			break;
		}
	}

	private void stopNotification() {
		isRunning = false;
	}

	private void runNotification() {
		while(isRunning) {
			// if(isNearPoi()) {
			Intent intent = new Intent(POI_INTENTFILTER);
			intent.putExtra("message", "This is my message!");
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		
	}
}
