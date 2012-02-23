package edu.aau.utzon.webservice;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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
			case COMMAND_GET_LOCATION_POINTS:  getLocationPoints();
			break;
			case COMMAND_GET_LOCATION_POINT: getLocationPoint(bundle.getInt(LOCATION_ID));
			break;
		}
	}
	
	private void getLocationPoint(int id) {
		
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

		RestMethod.getPoint(id);

		Log.e("Service Example", "Niggar niggar niggar niggar niggar niggar niggar! Cos them hoes is bitches!"); 
	}

	private void getLocationPoints() {

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

		RestMethod.getAllPoints();

		Log.e("Service Example", "Niggar niggar niggar niggar niggar niggar niggar! Cos them hoes is bitches!"); 
	}
}
