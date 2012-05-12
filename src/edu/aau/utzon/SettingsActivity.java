package edu.aau.utzon;

import com.actionbarsherlock.app.SherlockListActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SettingsActivity extends SherlockListActivity {

	public static final String PREFS_PROXIMITY = "proximity";

	@Override
	protected void onCreate(Bundle state)
	{
		super.onCreate(state);

		// Remove title bar
		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}



		// Restore preferences

		// File creation mode: the default mode, where the created file can only be accessed by the calling application
		
		

		String[] settings = new String[1];
		settings[0] = "POI Activation proximity";

		setListAdapter(new ArrayAdapter<String>(this, R.layout.settings_listitem, settings));
		ListView lv = getListView();

		final Context c = this;
		final CharSequence[] proximities = {"20", "100", "1000", "500000"};

		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				SharedPreferences preferenceMngr = getSharedPreferences(PREFS_PROXIMITY, MODE_PRIVATE);
				int selectedItem = preferenceMngr.getInt(PREFS_PROXIMITY, 20);
				switch(selectedItem){
				case 20:
					selectedItem = 0;
					break;
				case 100:
					selectedItem = 1;
					break;
				case 1000:
					selectedItem = 2;
					break;
				case 500000:
					selectedItem = 3;
					break;
				}
					

				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				builder.setCancelable(true)
				.setTitle("Select proximity")
				.setSingleChoiceItems(proximities, selectedItem, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						int newProximity = Integer.parseInt(proximities[item].toString());
						
						SharedPreferences preferenceMngr = getSharedPreferences(PREFS_PROXIMITY, MODE_PRIVATE);
						SharedPreferences.Editor editor = preferenceMngr.edit();
						editor.putInt(PREFS_PROXIMITY, newProximity);

						// Commit the edits!
						editor.commit();
						dialog.cancel();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				builder.create();
				builder.show();
			}
		});
	}

	@Override
	protected void onStop(){
		super.onStop();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		//      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		//      SharedPreferences.Editor editor = settings.edit();
		//      editor.putBoolean("silentMode", mSilentMode);

		// Commit the edits!
		//      editor.commit();
	}
}
