package edu.aau.utzon;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class SearchableActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.poi_list);

	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      
	      // Only search on name for now
	      startActivity(new Intent(this, PoiListActivity.class)
	      	.putExtra(PoiListActivity.COMMAND, PoiListActivity.COMMAND_QUERY)
	      	.putExtra(PoiListActivity.EXTRAS_QUERY, query)
	      	.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    }
	}
}
