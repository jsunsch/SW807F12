package edu.aau.utzon.outdoor;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

import edu.aau.utzon.R;
import edu.aau.utzon.location.SampleService;
import edu.aau.utzon.location.SampleService.SampleBinder;
import edu.aau.utzon.webservice.PointModel;

public class OutdoorActivity extends SherlockMapActivity {
	SampleService mService;
	boolean mBound = false;

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			SampleBinder binder = (SampleBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
	MyLocationOverlay mMyLocationOverlay = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display Google maps to the user
		setContentView(R.layout.mapview);
		mMapView = (TapControlledMapView) findViewById(R.id.mapview);
		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		// Draw POIs
		drawMap();
		drawOutdoorPois();
	}

	TapControlledMapView mMapView = null;
	private void drawMap() {
		mMapView.setBuiltInZoomControls(true);
	}

	private void drawOutdoorPois()
	{
		// Draw the user position on map
		mMyLocationOverlay.enableMyLocation();
		mMapView.getOverlays().add(mMyLocationOverlay);

		// Setup overlays
		if(mBound) {
			List<Overlay> mapOverlays = mMapView.getOverlays();
			Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
			final BalloonOverlay itemizedoverlay = new BalloonOverlay(drawable, mMapView);
	
			// Add POI to the overlay
			for(PointModel p : mService.getLocationHelper().getPois())
			{
				GeoPoint gp = p.getGeoPoint();
				int id = p.getId();
				String n = p.getName();
				String d = p.getDesc();
				itemizedoverlay.addOverlay(new OverlayItem(gp, n, d));
			}
	
			// Balloon stuff
			mMapView.setOnSingleTapListener(new OnSingleTapListener() {		
				@Override
				public boolean onSingleTap(MotionEvent e) {
					itemizedoverlay.hideAllBalloons();
					return true;
				}
			});
			// set iOS behavior attributes for overlay (?)
			itemizedoverlay.setShowClose(false);
			itemizedoverlay.setShowDisclosure(true);
			itemizedoverlay.setSnapToCenter(false);

			mapOverlays.add(itemizedoverlay);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to LocalService
		Intent intent = new Intent(this, SampleService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
//
//import java.util.List;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.drawable.Drawable;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.Window;
//import android.widget.Toast;
//
//import com.actionbarsherlock.app.SherlockMapActivity;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuItem;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MyLocationOverlay;
//import com.google.android.maps.Overlay;
//import com.google.android.maps.OverlayItem;
//import com.readystatesoftware.maps.TapControlledMapView;
//import com.readystatesoftware.maps.OnSingleTapListener;
//
//
//import edu.aau.utzon.PoiContentActivity;
//import edu.aau.utzon.PoiListActivity;
//import edu.aau.utzon.R;
//import edu.aau.utzon.SettingsActivity;
//import edu.aau.utzon.augmented.AugmentedActivity;
//import edu.aau.utzon.indoor.IndoorActivity;
//import edu.aau.utzon.location.LocationAwareMapActivity;
//import edu.aau.utzon.location.LocationHelper;
//import edu.aau.utzon.location.NearPoiPublisher;
//import edu.aau.utzon.webservice.PointModel;
//
//public class OutdoorActivity extends LocationAwareMapActivity{
//	
//	private static final String TAG = "OutdoorActivity";
//	private static final String PREFS_PROXIMITY = "proximity";
//	private TapControlledMapView mMapView;
//	private MyLocationOverlay mMyLocationOverlay;
//
//	@Override
//	public void onResume()
//	{
//		super.onResume();
//		mMyLocationOverlay.enableMyLocation();
//	}
//
//	@Override
//	public void onPause()
//	{
//		super.onPause();
//		mMyLocationOverlay.disableMyLocation();
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		// Remove title bar
//		if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
//			requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}
//
//		// Display Google maps to the user
//		setContentView(R.layout.mapview);
//
//		// Enable built-in map controls
//		mMapView = (TapControlledMapView) findViewById(R.id.mapview);
//		mMapView.setBuiltInZoomControls(true);
//
//		// Draw the user position on map
//		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
//		mMyLocationOverlay.enableMyLocation();
//		mMapView.getOverlays().add(mMyLocationOverlay);
//		
//		// Draw POIs
//		drawOutdoorPois();
//	}
//
//	
//	private int shownAlertId = 0;
//	private int shownToastId = 0;
//	@Override
//	public void makeUseOfNewLocation(Location location) {
//			//mLocationHelper.makeUseOfNewLocation(mMyLocationOverlay.getLastFix());	// Needed?
//		
//			LocationHelper lh = getLocationHelper();
//			
//			// Set some threshold for minimum activation distance
//			SharedPreferences settings = getSharedPreferences(PREFS_PROXIMITY, 0);
//			int proximityTreshold = settings.getInt("proximity", 20);
//			final PointModel closePoi = lh.getCurrentClosePoi();
//			double dist = lh.distToPoi(closePoi);
//			
//			if(dist < proximityTreshold) {	
//				AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				builder.setMessage("You are near a POI. Do you wish to see the content available?")
//				.setCancelable(false)
//				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//						StartPoiContentActivity(closePoi.getId());
//					}
//				})
//				.setNegativeButton("No", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//						dialog.cancel();
//					}
//				});
//				
//				// Avoid spamming the user with alert dialogs
//				if(shownAlertId == 0 || (shownAlertId != closePoi.getId())) {
//					shownAlertId = closePoi.getId();
//					AlertDialog alert = builder.create();
//					alert.show();
//				}
//			}
//			else {
//				// User not close to a POI
//				if(shownToastId == 0 || (shownToastId != closePoi.getId())) {
//					shownToastId = closePoi.getId();
//					CharSequence text = "Distance to nearest POI: " + (int)dist + " meter(s).";
//					Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
//					toast.show();
//				}
//			}
//	}
//
//	private void StartPoiContentActivity(int id) {
//		startActivity(new Intent(this, PoiContentActivity.class).putExtra("_ID", id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//	}
//
//	protected void animateToLocation(Location loc)
//	{
//		// Only animate if its a valid location
//		if(loc != null){
//			MapController mc = mMapView.getController();
//			GeoPoint point =  LocationHelper.locToGeo(loc);
//			mc.animateTo(point);
//		}
//	}
//
//	protected void drawOutdoorPois()
//	{
//		// Setup overlays
//		List<Overlay> mapOverlays = mMapView.getOverlays();
//		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
//		final BalloonOverlay itemizedoverlay = new BalloonOverlay(drawable, mMapView);
//
//		// Add POI to the overlay
//		for(PointModel p : getLocationHelper().getPois())
//		{
//			GeoPoint gp = p.getGeoPoint();
//			int id = p.getId();
//			String n = p.getName();
//			String d = p.getDesc();
//			itemizedoverlay.addOverlay(new OverlayItem(gp, n, d));
//		}
//
//		// Balloon stuff
//		mMapView.setOnSingleTapListener(new OnSingleTapListener() {		
//			@Override
//			public boolean onSingleTap(MotionEvent e) {
//				itemizedoverlay.hideAllBalloons();
//				return true;
//			}
//		});
//
//		// set iOS behavior attributes for overlay (?)
//		itemizedoverlay.setShowClose(false);
//		itemizedoverlay.setShowDisclosure(true);
//		itemizedoverlay.setSnapToCenter(false);
//
//		mapOverlays.add(itemizedoverlay);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.layout.menu_outdoor, menu);
//
//		//MenuItem searchItem = menu.findItem(R.id.actionbar_search); // TODO: Implement
//
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle item selection
//		switch (item.getItemId()) {
//		case R.id.actionbar_center_location:
//			animateToLocation(getLocationHelper().getCurrentLocation());
//			return true;
//		case R.id.actionbar_poi_list:
//			startActivity(new Intent(this, PoiListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_search:
//			// TODO: Implement
//			onSearchRequested();
//			return true;
//		case R.id.actionbar_augmented:
//			startActivity(new Intent(this, AugmentedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_indoor:
//			startActivity(new Intent(this, IndoorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		case R.id.actionbar_settings:
//			startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
//}
