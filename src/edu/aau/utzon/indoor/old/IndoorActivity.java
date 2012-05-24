package edu.aau.utzon.indoor.old;

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

	WifiManager _wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indoormain);

		String connectivity_context = Context.WIFI_SERVICE;
		_wifi = (WifiManager)getSystemService(connectivity_context);
	}
	
	public void calibration(View view) {
		Intent intent = new Intent(IndoorActivity.this, CalibrationActivity.class);
        startActivity(intent);
	}
	
	public void locationFinding(View view) {
		Intent intent = new Intent(IndoorActivity.this, LocatingActivity.class);
        startActivity(intent);
	}
}