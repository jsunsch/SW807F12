package edu.aau.utzon.indoor;

import java.util.List;

import edu.aau.utzon.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class IndoorActivity extends Activity {

	EditText _editText;
	WifiManager _wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indoormain);

		_editText = (EditText)findViewById(R.id.editText1);  

		String connectivity_context = Context.WIFI_SERVICE;
		_wifi = (WifiManager)getSystemService(connectivity_context);
	}
	
	public void calibration(View view) {
		Intent intent = new Intent(IndoorActivity.this, CalibrationActivity.class);
        startActivity(intent);
	}
	
	public void showPoints(View view) {
		String text = "";
		
		for (Point p : RadioMap.getPoints()) {
			text += p.name + ": ";
			
			//for(WifiMeasure wm : p.getMeasures()) {
			//	text += wm.getSignal() + " ";
			//}
			
			text += "\n";
		}
		
		_editText.setText(text);
	}
	
	public void locationFinding(View view) {
		Intent intent = new Intent(IndoorActivity.this, LocatingActivity.class);
        startActivity(intent);
	}

	public void readSignals(View view) {
		_editText.setText("");
		if (_wifi.startScan() == true)
		{
			List<ScanResult> scanResults =  _wifi.getScanResults();

			String text = "";

			for (ScanResult res : scanResults) {
				text += res.BSSID + ": " +  -res.level + "\n";
			}

			_editText.setText(text);
		}
		else
		{
			_editText.setText("Could not scan networks");
		}
	}
}