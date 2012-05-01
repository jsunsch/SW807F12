package edu.aau.utzon.indoor.old;

import java.util.ArrayList;
import java.util.List;

public class Point {
	ArrayList<WifiMeasure> measures;
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<WifiMeasure> getMeasures() {
		return measures;
	}

	public void setMeasures(ArrayList<WifiMeasure> measures) {
		this.measures = measures;
	}
	
	public Point(ArrayList<WifiMeasure> m, String n) {
		measures = m;
		name = n;
	}
}
