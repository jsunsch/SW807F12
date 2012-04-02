package edu.aau.utzon;

import edu.aau.utzon.webservice.RestService;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PoiService extends IntentService {
	public static final String COMMAND = "COMMAND";
	public static final String LOCATION_ID = "id";
	public static final int START_SERVICE = 0;
	public static final int STOP_SERVICE = 1;
	
	private boolean mRunning = false;
	
	public PoiService() {
		super("PoiService");
	}
	
	public static void StartService(Context context) {
		Intent intent = new Intent(context, PoiService.class);
		
		intent.putExtra(PoiService.COMMAND, PoiService.START_SERVICE);
		context.startService(intent);
	}
	
	public static void StopService(Context context) {
		Intent intent = new Intent(context, PoiService.class);
		
		intent.putExtra(PoiService.COMMAND, PoiService.STOP_SERVICE);
		context.stopService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		int command = bundle.getInt(COMMAND);

		switch (command) {
		case START_SERVICE:  StartService();
		break;
		case STOP_SERVICE: StopService();
		break;
		}
	}
	
	private void StartService() {
		mRunning = true;
		
		while (mRunning == true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void StopService() {
		mRunning = false;
	}
}
