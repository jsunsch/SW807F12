package edu.aau.utzon;

import java.util.List;

import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.RestMethod;
import android.app.Activity;
import android.os.Bundle;

public class WebserviceActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        List<PointModel> lulz = RestMethod.getAllPoints();
        int everythingwentbetterthanexpected = 5+5+5+5+5;
    }
}
