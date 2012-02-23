package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiHelper {
	public static ArrayList<WifiMeasure> getWifiMeasures(Context context, WifiManager wifi, int seconds) throws InterruptedException {
		if (wifi.startScan() == true)
		{
			Hashtable<String, Integer> measures = new Hashtable<String, Integer>();
			Hashtable<String, Integer> wifiCounts = new Hashtable<String, Integer> ();

			for (int i = 0; i < 2*seconds; i++) {

				List<ScanResult> scanResults =  wifi.getScanResults();

				for (ScanResult res : scanResults) {

					if (measures.containsKey(res.BSSID) == false) {
						measures.put(res.BSSID, new Integer(-res.level));
					}
					else {
						int newValue = (Integer)measures.get(res.BSSID);
						newValue += -res.level;
						measures.remove(res.BSSID);
						measures.put(res.BSSID, new Integer(newValue));
					}

					if (wifiCounts.containsKey(res.BSSID) == false) {
						wifiCounts.put(res.BSSID, new Integer(1));
					}
					else {
						int newValue = (Integer)wifiCounts.get(res.BSSID);
						newValue++;
						wifiCounts.remove(res.BSSID);
						wifiCounts.put(res.BSSID, new Integer(newValue));
					}

				}

				Thread.sleep(500);
				wifi.startScan();
			}

			ArrayList<WifiMeasure> realMeasures = new ArrayList<WifiMeasure>(); 

			Enumeration<String> keys = measures.keys();

			while(keys.hasMoreElements()) {
				String key = keys.nextElement();
				int value = (Integer)measures.get(key);

				WifiMeasure wm = new WifiMeasure(key, value / wifiCounts.get(key));
				realMeasures.add(wm);
			}

			return realMeasures;
		}
		else
		{
			new AlertDialog.Builder(context)
			.setTitle("Error")
			.setMessage("Could not scan for WIFI networks!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 

				}
			})
			.show();

			return null;
		}
	}
}
