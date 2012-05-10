package edu.aau.utzon;

import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.view.Window;

public class PoiListActivity extends SherlockListActivity {
	
	//private static int THEME = R.style.Theme_Sherlock_Light;
	
	String[] mGuiText;
	List<PointModel> mPois;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.layout.menu_poilist, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.actionbar_search:
			onSearchRequested();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		
		if(extras != null) {
			String query = extras.getString("query");
			String selection = ProviderContract.Points.ATTRIBUTE_NAME + " like " + "'%" + query + "%'"; // Our amazing search algorithm
			mPois = PointModel.asPointModels(
					getContentResolver().query(	ProviderContract.Points.CONTENT_URI, 
					ProviderContract.Points.PROJECTIONSTRING_ALL, 
					selection, 
					null, 
					null));
		}
		else {
			mPois = PointModel.dbGetAll(this);
		}
		
		mGuiText = new String[mPois.size()];
		for (int i = 0; i < mPois.size(); i++) {
			//mGuiText[i] = "Name: " + mPois.get(i).mName + "Desc: " + mPois.get(i).mDesc; 
			mGuiText[i] = mPois.get(i).getId() + ": " + mPois.get(i).getName();
		}
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.poi_list, mGuiText));

		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);

		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	startActivity(new Intent(getBaseContext(), PoiContentActivity.class)
		    	.putExtra("_ID", mPois.get(position).getId()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		    }
		  });
	}
}
