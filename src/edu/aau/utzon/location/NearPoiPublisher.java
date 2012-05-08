package edu.aau.utzon.location;

import java.util.List;

import edu.aau.utzon.webservice.PointModel;

public interface NearPoiPublisher {
	void userIsNearPoi(PointModel poi);
	List<PointModel> getPois();
}
