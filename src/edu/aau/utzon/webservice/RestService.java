package edu.aau.utzon.webservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
		
		// Simulates long respons by waiting
		long endTime = System.currentTimeMillis() + 5*1000;
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					wait(endTime - System.currentTimeMillis());
				} catch (Exception e) {
				}
			}
		}
		
		RestMethod.getAllPoints(getBaseContext());
		
		Log.e("Service Example", "Niggar niggar niggar niggar niggar niggar niggar! Cos them hoes is bitches!"); 
	}
}
