package edu.aau.utzon;

import java.util.List;

import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PoiContentActivity extends Activity{	
	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		Intent i = getIntent();
		
		int b_id = i.getExtras().getInt("_BALLOON_ID");
		int list_id = i.getExtras().getInt("_POILIST_ID");
		int _id = b_id > list_id ? b_id : list_id;

		// We shouldn't need to do this...
		List<PointModel> pois = 
				PointModel.asPointModel(getContentResolver()
						.query(	ProviderContract.Points.CONTENT_URI, 
								ProviderContract.Points.PROJECTIONSTRING_ALL, null, null, null));
		
		PointModel pm = pois.get(_id);
		
		setContentView(R.layout.poi_content);
		TextView tvTitle = (TextView)findViewById(R.id.textViewName);
		TextView tvDescription = (TextView)findViewById(R.id.textViewDescription);
		tvTitle.setText(pm.getName());
		tvDescription.setText(pm.getDesc());
	}
}
