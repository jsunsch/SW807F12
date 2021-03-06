package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.List;


import android.util.Log;

public class OldRadioMap {
	static ArrayList<Point> points= new ArrayList<Point>();

	public static void setPoints(ArrayList<Point> points) {
		OldRadioMap.points = points;
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

	private static double findDist(Point p1, ArrayList<WifiMeasure> measures, int minAccesPoints) {
		Double value = (double) 0;
		int accesPointsUsed = 0;

		for (WifiMeasure m1 : p1.measuresOld) {
			for (WifiMeasure m2 : measures) {
				if (m1.getName().equals(m2.getName())) {
					Double temp = (double)m1.getSignal() - (double)m2.getSignal();
					value += temp * temp;
					accesPointsUsed++;
				}
			}
		}

		if (accesPointsUsed < minAccesPoints) {
			return Double.MAX_VALUE;
		}
		else if (value == (double)0) {
			return Double.MAX_VALUE;
		}
		else {
			value = Math.sqrt(value);

			value = Math.abs(value);

			return value;

		}
	}

	public static Point FindPosition(ArrayList<WifiMeasure> measures, int minAccesPoints) {
		ArrayList<Double> distances = new ArrayList<Double>();

		Double smallestDistance = (double)100000;
		Point closestPoint = null;

		for (Point p : points) {

			double dist = findDist(p, measures, minAccesPoints);

			if (dist < smallestDistance) {
				smallestDistance = dist;
				closestPoint = p;
			}

			distances.add(dist);

		}
	

	closestPoint.dist = smallestDistance;
	
	return closestPoint;
}
}
