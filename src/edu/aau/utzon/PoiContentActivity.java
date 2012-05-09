package edu.aau.utzon;

import java.util.List;

import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
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
		// We have DB id
		if(db_id > 0) {
			String selection = ProviderContract.Points.ATTRIBUTE_ID + "=" + db_id;
			pm = PointModel.asPointModel(
					getContentResolver()
					.query(	ProviderContract.Points.CONTENT_URI, 
							ProviderContract.Points.PROJECTIONSTRING_ALL, 
							selection, null, null));
			int herpderp = 4+44;
			int def = herpderp+4;
		}
		else
		{
			pm = PointModel.asPointModels(
					getContentResolver()
					.query(	ProviderContract.Points.CONTENT_URI, 
							ProviderContract.Points.PROJECTIONSTRING_ALL, 
							null, null, null))
							.get(_id);
		}
		
		setContentView(R.layout.poi_content);
		TextView tvTitle = (TextView)findViewById(R.id.textViewName);
		TextView tvDescription = (TextView)findViewById(R.id.textViewDescription);
		tvTitle.setText(pm.getName());
		tvDescription.setText(pm.getDesc());
	}
}