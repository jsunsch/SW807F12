package edu.aau.utzon.indoor;

import java.io.IOException;
import java.util.List;

import edu.aau.utzon.R;
import edu.aau.utzon.location.LocationAwareActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class IndoorActivity extends LocationAwareActivity {

	WifiManager _wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indoormain);
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//SoundPlayer.playSound("tiger_tank.mp3", this);
		
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