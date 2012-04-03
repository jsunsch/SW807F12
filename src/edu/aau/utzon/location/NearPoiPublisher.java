package edu.aau.utzon.location;

import java.util.ArrayList;

import edu.aau.utzon.webservice.PointModel;

public interface NearPoiPublisher {
	void userIsNearPoi(PointModel poi);
	ArrayList<PointModel> getPois();
}
