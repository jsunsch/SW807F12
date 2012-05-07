package edu.aau.utzon;

import java.util.ArrayList;

import edu.aau.utzon.webservice.PointModel;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class PoiContentActivity extends Activity{	
	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		Intent i = getIntent();
		PointModel pm = i.getParcelableExtra("poi");
		
		if(pm == null)
			return;
		
		setContentView(R.layout.poi_content);
		TextView tvTitle = (TextView)findViewById(R.id.textViewName);
		TextView tvDescription = (TextView)findViewById(R.id.textViewDescription);
		
		tvTitle.setText(pm.mName);
		tvDescription.setText(pm.mDesc);
	}
}
