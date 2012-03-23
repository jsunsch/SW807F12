package edu.aau.utzon;

import java.util.ArrayList;

import edu.aau.utzon.location.PointOfInterest;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;;

public class PoiListActivity extends ListActivity {
	
	String[] mGuiText;
	ArrayList<PointOfInterest> mPois;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		
		if (bundle == null)
			return;
		
		 mPois = (ArrayList<PointOfInterest>)bundle.getSerializable("pois");
		
		mGuiText = new String[mPois.size()];
		
		for (int i = 0; i < mPois.size(); i++) {
			mGuiText[i] = mPois.get(i).getmLocationName() + " - " + mPois.get(i).getmProximity() + "m";
		}
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.poi_list, mGuiText));

		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);

		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		    	
		    	// TODO: Når der er blevet clicket på en POI skal der "ske noget". brug position variablen til at finde du af hvilken POI der er blevet klikket på
		    	
		    }
		  });
	}
}
