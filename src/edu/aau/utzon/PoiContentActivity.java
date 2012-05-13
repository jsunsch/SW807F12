package edu.aau.utzon;

import edu.aau.utzon.webservice.PointModel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PoiContentActivity extends Activity{	
	public static final String COMMAND = "PoiContentActivity_COMMAND";
	public static final String EXTRAS_NORMAL_ID = "PoiContentActivity_NORMAL_ID";
	public static final String EXTRAS_BUBBLETAP_ID = "PoiContentActivity_BUBBLETAP_ID";
	public static final int COMMAND_NORMAL_ID = 1;
	public static final int COMMAND_BUBBLETAP_ID = 2;
	
	
	
	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		int command = extras.getInt(COMMAND);
		
		int id;
		PointModel pm = null;
		
		switch(command) {
		case COMMAND_NORMAL_ID:
			id = i.getExtras().getInt(EXTRAS_NORMAL_ID);
			pm = PointModel.dbGetSingle(this, id);
			break;
		case COMMAND_BUBBLETAP_ID:
			id = i.getExtras().getInt(EXTRAS_BUBBLETAP_ID);
			pm = PointModel.dbGetAll(this).get(id);
			break;
		}
		
		// Fully constructed PointModel available for UI
		setContentView(R.layout.poi_content);
		TextView tvTitle = (TextView)findViewById(R.id.textViewName);
		TextView tvDescription = (TextView)findViewById(R.id.textViewDescription);
		tvTitle.setText(pm.getName());
		tvDescription.setText(pm.getDesc());
	}
}