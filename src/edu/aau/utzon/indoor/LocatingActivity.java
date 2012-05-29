package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.aau.utzon.R;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LocatingActivity extends Activity {
	WifiManager _wifi;
	TextView _textView;
	EditText _textViewK;
	EditText _textViewTime;
	EditText _textViewActivation;
	EditText _textViewStop;
	//Timer _timer;

	class LocatingTask extends TimerTask
	{
		Context _context;

		public LocatingTask(Context context)
		{
			_context = context;
		}

		@Override
		public void run() {
			Log.e("TACO", "YO");
			
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		_textView = (TextView)findViewById(R.id.textView3);
		_textViewK = (EditText)findViewById(R.id.editText4);
		_textViewTime = (EditText)findViewById(R.id.editText5);
		_textViewActivation = (EditText)findViewById(R.id.editText6);
		_textViewStop = (EditText)findViewById(R.id.editText7);

		String connectivity_context = Context.WIFI_SERVICE;
		_wifi = (WifiManager)getSystemService(connectivity_context);

	}

	//@Override
	//public void onDestroy()
	//{
	//	SoundPlayer.Stop();
	//}

	int stopCounter = 0;
	public void findLocation(View view) throws InterruptedException {
		_textView.setText("");
		if (_wifi.startScan() == true)
		{
			List<ScanResult> scanResults = _wifi.getScanResults();
			//ArrayList<WifiMeasureCollection> measures = WifiHelper.getWifiMeasures(this, _wifi, 10, 200);
			ArrayList<WifiMeasure> measures = WifiHelper.getWifiMeasuresAvg(this, _wifi, Integer.parseInt(_textViewTime.getText().toString()), 200);

			long timeBefore = System.currentTimeMillis();
			Point p = RadioMap.FindPosition(measures, 1, Integer.parseInt(_textViewK.getText().toString()));
			long timeAfter = System.currentTimeMillis();

			long timeBeforeOld = System.currentTimeMillis();
			Point pOld = OldRadioMap.FindPosition(measures, 1);
			long timeAfterOld = System.currentTimeMillis();

			String text = "";
			text += "Location:\t\t" + p.getName() + "\n" + "Distance:\t\t" + p.dist + "\n" + "Calculation Time:\t\t" + (timeAfter - timeBefore) + "\n\n";
			text += "Location:\t\t" + pOld.getName() + "\n" + "Distance:\t\t" + pOld.dist + "\n" + "Calculation Time:\t\t" + (timeAfterOld - timeBeforeOld) + "\n\n";

			if (p.dist <= Double.parseDouble(_textViewActivation.getText().toString()))
			{
				stopCounter = 0;
				SoundPlayer.playSound(p.getSoundFilePath(), this);
			}
			else 
			{
				if (stopCounter == Integer.parseInt(_textViewStop.getText().toString())) {
					SoundPlayer.Stop();
					stopCounter = -1;
				}
				stopCounter++;
			}

			if (p == null) {
				text = "You are not close to any points.!";
			}
			else {

				// This is just printet out for debug reasons. You can just delete it if you want... But ask lige Steffan first
				//for (WifiMeasure m1 : measures) {
				//	for (WifiMeasure m2 : p.getMeasures()) {
				//		if (m1.getName().equals(m2.getName())) {
				//			Double temp = (double)m1.getSignal() - (double)m2.getSignal();
				//			text += m1.getName() + ": " + temp + "\n";
				//		}
				//	}
				//}
			}
			_textView.setText(text);
		}
		else
		{
			_textView.setText("Could not scan networks");
		}
	}
}
