package edu.aau.utzon.indoor;

import java.util.ArrayList;

public class WifiMeasureCollection {
	ArrayList<WifiMeasure> measures;

	public ArrayList<WifiMeasure> getMeasures() {
		return measures;
	}

	public void setMeasures(ArrayList<WifiMeasure> measures) {
		this.measures = measures;
	}
	
	public WifiMeasureCollection(ArrayList<WifiMeasure> measure) {
		measures = measure;
	}
}
