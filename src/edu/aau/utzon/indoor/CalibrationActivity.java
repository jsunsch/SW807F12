package edu.aau.utzon.indoor;

import java.util.ArrayList;
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
	
	public void addPoint(View view) {
		if (_wifi.startScan() == true)
		{
			List<ScanResult> scanResults =  _wifi.getScanResults();

			ArrayList<WifiMeasure> measures = new ArrayList<WifiMeasure>();
			for (ScanResult res : scanResults) {
				if (-res.level < 80)
					measures.add(new WifiMeasure(res.BSSID, -res.level));
			}
			
			PointData.addPoint(new Point(measures, _editText.getText().toString()));
			
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
