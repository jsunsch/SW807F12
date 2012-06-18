package edu.aau.utzon.indoor;

public class WifiMeasure {
	String name;
	int signal;

	public int getSignal() {
		return signal;
	}

	public void setSignal(int signal) {
		this.signal = signal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WifiMeasure(String n, int s) {
		name = n;
		signal = s;
	}
}