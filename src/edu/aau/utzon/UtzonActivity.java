package edu.aau.utzon;

import edu.aau.utzon.indoor.IndoorActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class UtzonActivity extends Activity {
    /** Called when the activity is first created. */

    // roflcopter git er awesome

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //startActivity(new Intent(getApplicationContext(), OutdoorActivity.class));
        startActivity(new Intent(getApplicationContext(), IndoorActivity.class));
    }
}
