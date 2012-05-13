package edu.aau.utzon.location;

import android.location.Location;
import edu.aau.utzon.webservice.PointModel;

public interface ILocationAware {
	public void serviceNewPoiBroadcast(final PointModel poi);
	public void serviceNewLocationBroadcast(Location location);
}
