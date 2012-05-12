package edu.aau.utzon;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.view.Window;

public class PoiListActivity extends SherlockListActivity {
	public static final String COMMAND = "PoiListActivity_LOCATION";

	/** Clients must provide (String) EXTRAS_QUERY **/
	public static final int COMMAND_ALL = 1;
	/** Clients must provide (String) EXTRAS_QUERY **/
	public static final int COMMAND_QUERY = 2;
	public static final String EXTRAS_QUERY = "PoiListActivity_QUERY";
	//public static final String EXTRAS_LOCATION = "PoiListActivity_LOCATION";

	Location mLocation = null;
	String[] mGuiText = null;
	List<PointModel> mPois = null;

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

	private String[] generateNoLocText(List<PointModel> pois){
		String[] guiText = new String[pois.size()];
		for (int i = 0; i < pois.size(); i++) {
			PointModel pm = pois.get(i);
			guiText[i] = pm.getName();
		}
		return guiText;
	}

	//	private String[] generateWithLocText(List<PointModel> pois, double lat, double lg){
	//		String[] guiText = new String[pois.size()];
	//		for (int i = 0; i < pois.size(); i++) {
	//			PointModel pm = pois.get(i);
	//			int distance = (int)LocationHelper.distFrom(pm.getLat(), pm.getLong(), lat, lg);
	//			guiText[i] = pm.getName() + " - " + distance + "m";
	//		}
	//		return guiText;
	//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}


		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		int command = extras.getInt(COMMAND);
		switch(command) {
		case COMMAND_ALL:
			mPois = PointModel.dbGetAll(this);
			break;
		case COMMAND_QUERY:
			String query = extras.getString("query");
			String selection = ProviderContract.Points.ATTRIBUTE_NAME + " like " + "'%" + query + "%'"; // Our amazing search algorithm
			Cursor c = 
					getContentResolver().query(	ProviderContract.Points.CONTENT_URI, 
							ProviderContract.Points.PROJECTIONSTRING_ALL, 
							selection, 
							null, 
							null);
			if(c.getCount() > 0) {
				mPois = PointModel.asPointModels(c);
			}
			else {
				mPois = new ArrayList<PointModel>();
				c.close();
			}
			break;
		}


		//mGuiText = mLocation == null ? generateNoLocText(mPois) : generateWithLocText(mPois, mLocation.getLatitude(), mLocation.getLongitude());
		mGuiText = generateNoLocText(mPois);
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
