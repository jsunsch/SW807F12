package edu.aau.utzon;

import java.util.ArrayList;

import edu.aau.utzon.webservice.RestService;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class PoiService extends IntentService {
	public static final String COMMAND = "COMMAND";
	public static final String LOCATION_ID = "id";
	public static final int START_SERVICE = 0;
	public static final int STOP_SERVICE = 1;

	private boolean mRunning = false;

	Messenger client;

	public PoiService() {
		super("PoiService");
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Log.e("TACO", "Adding client: " + msg.replyTo);
				client = msg.replyTo;
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		int command = bundle.getInt(COMMAND);

		try {
			StartService();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void StartService() throws RemoteException, InterruptedException {
		mRunning = true;

		while (mRunning == true) {

			Thread.sleep(1000);
			if (client != null)
				client.send(Message.obtain(null, 0));
		}
	}

	private void StopService() {
		mRunning = false;
	}
}
