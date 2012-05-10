package edu.aau.utzon;

import edu.aau.utzon.webservice.PointModel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PoiContentActivity extends Activity{	
	
	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		Intent i = getIntent();

		// Would like to unify this to _ID, but balloon is special case
		int db_id = i.getExtras().getInt("_ID");
		int b_id = i.getExtras().getInt("_BALLOON_ID");
		int list_id = i.getExtras().getInt("_POILIST_ID");
		int _id = b_id > list_id ? b_id : list_id;
		
		PointModel pm;
		
		if(db_id > 0) {
			// We have DB id
			pm = PointModel.dbGetSingle(this, db_id);
		}
		else
		{
			pm = PointModel.dbGetAll(this).get(_id);
		}
		
		// Fully constructed PointModel available for UI
		setContentView(R.layout.poi_content);
		TextView tvTitle = (TextView)findViewById(R.id.textViewName);
		TextView tvDescription = (TextView)findViewById(R.id.textViewDescription);
		tvTitle.setText(pm.getName());
		tvDescription.setText(pm.getDesc());
	}
}