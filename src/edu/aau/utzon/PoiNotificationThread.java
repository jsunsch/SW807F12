package edu.aau.utzon;

import android.util.Log;

public class PoiNotificationThread extends Thread{
	
    public void run() {
        while (true) {
        	Log.e("TACO", "Yo");
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
