package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.List;

public class Point {
	ArrayList<WifiMeasureCollection> measures;
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<WifiMeasureCollection> getMeasures() {
		return measures;
	}

	public void setMeasures(ArrayList<WifiMeasureCollection> measures) {
		this.measures = measures;
	}
	
	public Point(ArrayList<WifiMeasureCollection> m, String n) {
		measures = m;
		name = n;
	}
}
