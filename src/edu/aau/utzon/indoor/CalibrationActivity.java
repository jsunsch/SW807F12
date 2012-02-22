package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.aau.utzon.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CalibrationActivity extends Activity  {
	WifiManager _wifi;
	EditText _editText;
	TextView _textView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calibration);

		_editText = (EditText)findViewById(R.id.editText2);  
		_textView = (TextView)findViewById(R.id.textView1);

		String connectivity_context = Context.WIFI_SERVICE;
		_wifi = (WifiManager)getSystemService(connectivity_context);

		_textView.setText("Number of points: " + PointData.getPoints().size());
	}

	public void addPoint(View view) throws InterruptedException {
		if (_wifi.startScan() == true)
		{
			Hashtable<String, Integer> measures = new Hashtable<String, Integer>();
			Hashtable<String, Integer> wifiCounts = new Hashtable<String, Integer> ();
			
			for (int i = 0; i < 4*5; i++) {

				List<ScanResult> scanResults =  _wifi.getScanResults();

				for (ScanResult res : scanResults) {
					if (-res.level < 80) {

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

				Thread.sleep(250);
				_wifi.startScan();
			}
			
			ArrayList<WifiMeasure> realMeasures = new ArrayList<WifiMeasure>(); 

		    Enumeration<String> keys = measures.keys();
		    
		    while(keys.hasMoreElements()) {
		       String key = keys.nextElement();
		       int value = (Integer)measures.get(key);
		       
		       WifiMeasure wm = new WifiMeasure(key, value / wifiCounts.get(key));
		       realMeasures.add(wm);
		    }
		  
			PointData.addPoint(new Point(realMeasures, _editText.getText().toString()));

			new AlertDialog.Builder(this)
			.setTitle("Done")
			.setMessage("Point added!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 

				}
			})
			.show();
		}
		else
		{
			new AlertDialog.Builder(this)
			.setTitle("Error")
			.setMessage("Could not scan for WIFI networks!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 

				}
			})
			.show();
		}
		//PointData.AddPoint(new Point())

		_textView.setText("Number of points: " + PointData.getPoints().size());
	}

	public void deleteAllPoints(View view)  {
		PointData.deleteAllPoints();

		_textView.setText("Number of points: " + PointData.getPoints().size());
	}
}
