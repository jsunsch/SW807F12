package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.List;

import edu.aau.utzon.R;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LocatingActivity extends Activity {
	WifiManager _wifi;
	TextView _textView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		_textView = (TextView)findViewById(R.id.textView3);

		String connectivity_context = Context.WIFI_SERVICE;
		_wifi = (WifiManager)getSystemService(connectivity_context);
	}

	public void findLocation(View view) {
		_textView.setText("");
		if (_wifi.startScan() == true)
		{
			List<ScanResult> scanResults =  _wifi.getScanResults();

			ArrayList<WifiMeasure> measures = new ArrayList<WifiMeasure>();

			for (ScanResult res : scanResults) {
				if (-res.level < 80)
					measures.add(new WifiMeasure(res.BSSID, -res.level));
			}

			Point p = PointData.FindPosition(measures);

			String text = "";
			if (p == null) {
				text = "You are not close to any points. This is bad!";
			}
			else {
				text += p.getName() + "\n";
			}

			for (ScanResult res : scanResults) {
				text += res.BSSID + ": " +  -res.level + "\n";
			}
			
			text += "------------\n";
			
			for (WifiMeasure wm : p.getMeasures()) {
				text += wm.getName() + ": " + wm.getSignal() + "\n";
			}
			
			for (Point p2 : PointData.getPoints()) {
				text += p2.getName() + ": " + PointData.findDist(new Point(measures, ""), p2) + "\n";
				for (WifiMeasure wm : p2.getMeasures()) {
					text += wm.getName() + ": " + wm.getSignal() + "\n";
				}
			}

			_textView.setText(text);
		}
		else
		{
			_textView.setText("Could not scan networks");
		}
	}
}
