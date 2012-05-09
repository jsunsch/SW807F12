package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class WifiHelper {
	
	public static ArrayList<WifiMeasureCollection> getWifiMeasures(Context context, WifiManager wifi, int seconds, int signalMax) throws InterruptedException {
		if (wifi.startScan() == true)
		{
			ArrayList<WifiMeasureCollection> measureCollections = new ArrayList<WifiMeasureCollection>();
			
			for (int i = 0; i < seconds/2; i++) {
				
				ArrayList<WifiMeasure> measures = new ArrayList<WifiMeasure>();
				// Raw list of WIFI access points
				List<ScanResult> scanResults =  wifi.getScanResults();

				for (ScanResult res : scanResults) {
					if (-res.level < signalMax) {
						measures.add(new WifiMeasure(res.BSSID, -res.level));
					}
				}
				
				measureCollections.add(new WifiMeasureCollection(measures));

				Thread.sleep(2000);
				wifi.startScan();
			}
		  
			return measureCollections;
		}
		else
		{
			return null;
		}
	}
	
	public static ArrayList<WifiMeasure> getWifiMeasuresAvg(Context context, WifiManager wifi, int seconds, int signalMax) throws InterruptedException {
		if (wifi.startScan() == true)
		{
			// Contains the signal sum from each WIFI acces points
			Hashtable<String, Integer> measures = new Hashtable<String, Integer>();
			// Contains the count of how many times an access point as been measured
			Hashtable<String, Integer> wifiCounts = new Hashtable<String, Integer> ();
			
			for (int i = 0; i < seconds/2; i++) {

				// Raw list of WIFI access points
				List<ScanResult> scanResults =  wifi.getScanResults();

				for (ScanResult res : scanResults) {
					if (-res.level < signalMax) {

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
				}

				Thread.sleep(1500);
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
			return null;
		}
	}
	
}
