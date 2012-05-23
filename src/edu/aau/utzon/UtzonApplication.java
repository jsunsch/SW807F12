package edu.aau.utzon;

import edu.aau.utzon.location.SampleService;
import android.app.Application;
import android.content.Intent;

public class UtzonApplication extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		startService(new Intent(this, SampleService.class));
	}

}
