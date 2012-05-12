package edu.aau.utzon;

import edu.aau.utzon.location.LocationAwareService;
import android.app.Application;

public class UtzonApplication extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			Class.forName(LocationAwareService.class.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
