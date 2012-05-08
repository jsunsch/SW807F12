package edu.aau.utzon.indoor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.SortedMap;

import android.util.Log;

public class RadioMap {
	static ArrayList<Point> points= new ArrayList<Point>();

	public static void setPoints(ArrayList<Point> points) {
		RadioMap.points = points;
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

	private static double findDist(ArrayList<WifiMeasure> p1, ArrayList<WifiMeasure> p2, int minAccesPoints) {
		Double value = (double) 0;
		int accesPointsUsed = 0;

		for (WifiMeasure m1 : p1) {
			for (WifiMeasure m2 : p2) {
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

	public static Point FindPosition(ArrayList<WifiMeasure> measures, int minAccesPoints, int k) {

		Double smallestDistance = (double)100000;
		Point closestPoint = null;

		double[] distances = new double[k];
		Point[] kpoints = new Point[k];

		// Finds the k nearest points
		for (Point p : points) {
			for (WifiMeasureCollection pointMeasures1 : p.getMeasures())
			{
				double dist = findDist(pointMeasures1.getMeasures(), measures, minAccesPoints);

				boolean isPointsFull = true;

				for (int i = 0; i < k; i++)
				{
					if (kpoints[i] == null)
					{
						kpoints[i] = p;
						distances[i] = dist;

						isPointsFull = false;
						if (i == k-1)
							sortPoints(kpoints, distances);

						break;
					}
				}
				if (isPointsFull == false)
					continue;

				if (dist < distances[k-1])
				{
					kpoints[k-1] = p;
					distances[k-1] = dist;
					sortPoints(kpoints, distances);
				}
			}
		}

		// Counts the number of times a point was the nearest
		Hashtable<String, Integer> counts = new Hashtable<String, Integer>();
		for (int i = 0; i < k; i++)
		{
			if (counts.containsKey(kpoints[i].name) == false)
			{
				counts.put(kpoints[i].name, 1);
			}
			else
			{
				int buffer = counts.get(kpoints[i].name);
				buffer++;
				counts.remove(kpoints[i].name);
				counts.put(kpoints[i].name, buffer);
			}
		}

		Enumeration<String> keys = counts.keys();

		ArrayList<String> bestKeys = new ArrayList<String>();
		String bestKey = "";
		int bestCount = -1;

		// Findes the point that was nearest the most
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			int value = (Integer)counts.get(key);

			if (value > bestCount)
			{
				bestKeys.clear();
				bestKey = key;
				bestKeys.add(key);
				bestCount = value;
			}
			else if (value == bestCount)
			{
				bestKeys.add(key);
			}
		}

		if (bestKeys.size() == 1)
		{
			for(Point p : points) {
				if (p.name.equals(bestKey) == true)
				{
					return p;
				}
			}
		}
		else
		{
			for (int i = 0; i < k; i++)
			{
				for (String name : bestKeys) {
					if (kpoints[i].name.equals(name)) {
						return kpoints[i];
					}
				}
			}
		}
		
		return null;
	}

	private static void sortPoints(Point[] kpoints, double[] distances) {
		Point[] sortedPoints = new Point[kpoints.length];
		double[] sortedDistances = new double[distances.length];

		for (int i = 0; i < kpoints.length; i++)
		{
			Point closestPoint = null;
			int pointIndex = -1;
			double dist = 1000000000;
			for (int j = 0; j < kpoints.length; j++)
			{
				if (kpoints[j] != null)
				{
					if (distances[j] < dist)
					{
						closestPoint = kpoints[j];
						dist = distances[j];
						pointIndex = j;
					}
				}
			}
			sortedPoints[i] = closestPoint;
			sortedDistances[i] = dist;
			kpoints[pointIndex] = null;
		}

		// Det her er fucking dumt... Hvad sker der lige for Java? For real? Altså.. Common.. Tror de selv på det?
		//kpoints = sortedPoints;
		for (int i = 0; i < sortedPoints.length; i++)
		{
			kpoints[i] = sortedPoints[i];
		}
		for (int i = 0; i < sortedDistances.length; i++)
		{
			distances[i] = sortedDistances[i];
		}
	}
}
