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
			
			for (int i = 0; i < 2*seconds; i++) {
				
				ArrayList<WifiMeasure> measures = new ArrayList<WifiMeasure>();
				// Raw list of WIFI access points
				List<ScanResult> scanResults =  wifi.getScanResults();

				for (ScanResult res : scanResults) {
					if (-res.level < signalMax) {
						measures.add(new WifiMeasure(res.BSSID, -res.level));
					}
				}
				
				measureCollections.add(new WifiMeasureCollection(measures));

				Thread.sleep(500);
				wifi.startScan();
			}
		  
			return measureCollections;
		}
		else
		{
			return null;
		}
	}
}
