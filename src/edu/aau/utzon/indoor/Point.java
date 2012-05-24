package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.List;

public class Point {
	ArrayList<WifiMeasureCollection> measures;
	String name;
	double dist;


	private String soundFilePath;

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
	
	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}
	
	public Point(ArrayList<WifiMeasureCollection> m, String n, String sound) {
		measures = m;
		name = n;
		soundFilePath = sound;
	}

	String getSoundFilePath() {
		return soundFilePath;
	}

	void setSoundFilePath(String soundFilePath) {
		this.soundFilePath = soundFilePath;
	}
}
