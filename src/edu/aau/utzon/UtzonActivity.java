package edu.aau.utzon;

import java.util.ArrayList;

import edu.aau.utzon.location.PointOfInterest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class UtzonActivity extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		ArrayList<PointOfInterest> pois = new ArrayList<PointOfInterest>();
		pois.add(new PointOfInterest("Tyren ved vejen", 500));
		pois.add(new PointOfInterest("Limfjordbroen", 2000));
		
		Intent intent = new Intent(this, PoiListActivity.class);
		intent.putExtra("pois", pois);
		
		startActivity(intent);
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.AugmentedButton:
			startActivity(new Intent(getApplicationContext(), AugmentedActivity.class));
			break;
		case R.id.OutdoorButton:
			startActivity(new Intent(getApplicationContext(), OutdoorActivity.class));
			break;
		case R.id.WebserviceButton:
			startActivity(new Intent(getApplicationContext(), WebserviceActivity.class));
			break;
		default:
			throw new RuntimeException("Unknown button ID");
		}
	}
}
