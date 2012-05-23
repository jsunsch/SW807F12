package edu.aau.utzon.outdoor;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

import edu.aau.utzon.utils.CommonIntents;

public class BalloonOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private Context c;

	public BalloonOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
	}

	public void addOverlay(OverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		/* TODO: How do we get DB id from this? */
		GeoPoint p = item.getPoint();
		String title = item.getTitle();
		String snippet = item.getSnippet();
		c.startActivity(CommonIntents.startPoiContentActivityFromBubbleTap(c, index));
		
		//c.startActivity(CommonIntents.startPoiListActivityQuery(c, title));
		/* TODO: Do not use dummy ID */
		//PointModel poi = new PointModel(1, snippet, title, p.getLatitudeE6(), p.getLatitudeE6());
//		Intent i = new Intent(c, PoiContentActivity.class);
//		i.putExtra("_BALLOON_ID", index);
//		c.startActivity(i);
		return true;
	}
	
	public void clear() {
		m_overlays.clear();
		populate();
	}
}
