package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class PointData {
	static ArrayList<Point> points= new ArrayList<Point>();

	public static void setPoints(ArrayList<Point> points) {
		PointData.points = points;
	}

	public static void deleteAllPoints() {
		points.clear();
	}

	public static void addPoint(Point p) {

		points.add(p);
	}

	public static ArrayList<Point> getPoints() {
		return points;
	}
	
	public static double findDist(Point p1, Point p2) {
		
		try
		{
		Double value = (double)0;
		
		for (WifiMeasure m1 : p1.getMeasures()) {
			for (WifiMeasure m2 : p2.getMeasures()) {
				if (m1.getName().equals(m2.getName())) {
					Double temp = (double)m1.getSignal() - (double)m2.getSignal();
					value += temp * temp;
				}
			}
		}


			value = Math.sqrt(value);
			value = Math.abs(value);
			
			return value;
		}
		catch (Exception e) {
			Log.e("Utzon", "Jeg tror der er en division med 0 fejl i findDist metoden");
		}
			return 0;
	}

	public static Point FindPosition(ArrayList<WifiMeasure> measures) {

		try
		{
		ArrayList<Double> distances = new ArrayList<Double>();
		
		Double smallestDistance = (double)100000;
		Point closestPoint = null;

		for (Point p : points) {
			Double value = (double) 0;

			for (WifiMeasure m1 : p.getMeasures()) {
				for (WifiMeasure m2 : measures) {
					if (m1.getName().equals(m2.getName())) {
						Double temp = (double)m1.getSignal() - (double)m2.getSignal();
						value += temp * temp;
					}
				}
			}

			if (value ==  (double)0) {
				distances.add((double)100000);
			}
			else {
				value = Math.sqrt(value);
				value = Math.abs(value);
				
				if (value < smallestDistance) {
					smallestDistance = value;
					closestPoint = p;
				}
				
				distances.add(value);
			}
		}
		
		Point p2 = new Point(closestPoint.getMeasures(), closestPoint.getName() + " " + smallestDistance);
		
		return p2;
		}
		catch (Exception e) {
			Log.e("Utzon", "Der gik noget galt da Utzon forsoegte at find location");
		}
		
		return null;
	}
}
