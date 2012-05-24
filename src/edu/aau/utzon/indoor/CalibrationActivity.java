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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CalibrationActivity extends Activity  {
	WifiManager _wifi;
	EditText _editText;
	EditText _editTextTime;
	TextView _textView;
	Spinner _s;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calibration);
		
		_editText = (EditText)findViewById(R.id.editText2);  
		_textView = (TextView)findViewById(R.id.textView1);
		_editTextTime = (EditText)findViewById(R.id.editText3);  
		
		_s = (Spinner) findViewById(R.id.spinner);
	    ArrayAdapter adapter = ArrayAdapter.createFromResource(
	            this, R.array.planets, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    _s.setAdapter(adapter);

		String connectivity_context = Context.WIFI_SERVICE;
		_wifi = (WifiManager)getSystemService(connectivity_context);

		_textView.setText("Number of points: " + RadioMap.getPoints().size());
	}

	public void addPoint(View view) throws InterruptedException {

		//ArrayList<WifiMeasureCollection> realMeasures = WifiHelper.getWifiMeasures(this, _wifi, Integer.parseInt(_editTextTime.getText().toString()), 200);
		ArrayList<WifiMeasureCollection> realMeasures = null;
		//if (realMeasures == null)
		//	return;
		Log.e("TACO", _s.toString());
		RadioMap.addPoint(new Point(realMeasures, _editText.getText().toString(), _s.toString()));

		new AlertDialog.Builder(this)
		.setTitle("Done")
		.setMessage("Point added!")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 

			}
		})
		.show();
		
		//PointData.AddPoint(new Point())

		_textView.setText("Number of points: " + RadioMap.getPoints().size());
	}

	public void deleteAllPoints(View view)  {
		RadioMap.deleteAllPoints();

		_textView.setText("Number of points: " + RadioMap.getPoints().size());
	}
}
