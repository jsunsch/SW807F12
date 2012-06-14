package edu.aau.utzon.augmented;

import java.io.IOException;
import java.util.List;


import edu.aau.utzon.R;
import edu.aau.utzon.location.LocationAwareActivity;
import edu.aau.utzon.location.LocationHelper;
import edu.aau.utzon.utils.CommonIntents;
import edu.aau.utzon.webservice.PointModel;
import edu.aau.utzon.webservice.ProviderContract;

import android.app.Activity;
import android.content.Context; 
import android.graphics.PixelFormat;
import android.hardware.Camera; 
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.Camera.Size;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle; 
import android.util.Log;
import android.view.SurfaceHolder; 
import android.view.SurfaceView; 
import android.view.Window; 
import android.view.ViewGroup.LayoutParams; 
import android.view.WindowManager;
import android.widget.TextView;

public class AugmentedActivity extends Activity implements SensorEventListener {
	private Preview mPreview;
	//private int numberOfCameras;
	//private int cameraCurrentlyLocked;
	
	private SensorManager mSensorManager;
    private Sensor mGyro;

	// The first rear facing camera
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private AugmentedOverlay mDraw;

	//private LocationHelper mLocationHelper;
	private List<PointModel> mPois;
	private Location mLocation;

	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		
//		while(getLocationHelper() == null) {}
		mLocation = getIntent().getParcelableExtra(CommonIntents.EXTRA_LOCATION);
		mPois = PointModel.dbGetAll(this);
		
		// Initialize location helper
		//mLocationHelper = new LocationHelper(this);
		//this.mLocationHelper.onCreate();
		
		// Fullscreen
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.augmented);

		// Gyro
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
		
		// Camera
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		
		// Create a RelativeLayout container that will hold a SurfaceView
		mPreview = new Preview(this);

		// Create layout for the overlay
		mDraw = new AugmentedOverlay(this, mPois); 
		addContentView(mDraw, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(mPreview);
	}

	@Override
	public void onResume() { 
		super.onResume();
		//mLocationHelper.onResume();
		mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
	}


	@Override
	public void onPause() {
		super.onPause();
		//mLocationHelper.onPause();
		mSensorManager.unregisterListener(this);

	}

	/** GYRO SENSOR CALLBACKS **/
	@Override
	public void onSensorChanged(SensorEvent e) {
		// New data from gyro available
		TextView tv = (TextView)findViewById(R.id.textViewDebug);
		mDraw.updateOverlay(e, mLocation);
		if(tv != null)
		{
			tv.setText("Azimuth: " + (int)e.values[0] + "\n" +
					"Pitch: " + (int)e.values[1] + "\n" +
					"Roll: " + (int)e.values[2]);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		int watizthis = 5;
		watizthis++;
	}


	//----------------------------------------------------------------------

	/**
	 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
	 * to the surface. We need to center the SurfaceView because not all devices have cameras that
	 * support preview sizes at the same aspect ratio as the device's display.
	 */
	class Preview implements SurfaceHolder.Callback {
		private final String TAG = "Preview";

		SurfaceView mSurfaceView;
		SurfaceHolder mHolder;
		Size mPreviewSize;
		List<Size> mSupportedPreviewSizes;
		Camera mCamera;

		Preview(Context context) {
			//super(context);

			mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
			//addView(mSurfaceView);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = mSurfaceView.getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			// Dirty - Default to whatever camera we can find
			setCamera(Camera.open());

			// Set preview size
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, mSurfaceView.getWidth(), mSurfaceView.getHeight());

			// Android sucks, must force the orientation manually if phone is not in landscape
			// We force the orientation in the manifest instead
			/*Camera.Parameters parameters = mCamera.getParameters();
			parameters.set("orientation", "portrait");
			mCamera.setParameters(parameters);*/
		}

		public void setCamera(Camera camera) {
			mCamera = camera;
			if (mCamera != null) {
				mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
				//requestLayout();
			}
		}

		public void switchCamera(Camera camera) {
			setCamera(camera);
			try {
				camera.setPreviewDisplay(mHolder);
			} catch (IOException exception) {
				Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
			}
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			//requestLayout();

			camera.setParameters(parameters);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, acquire the camera and tell it where
			// to draw.
			try {
				if (mCamera != null) {
					mCamera.setPreviewDisplay(holder);
				}
			} catch (IOException exception) {
				Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// Surface will be destroyed when we return, so stop the preview.
			if (mCamera != null) {
				mCamera.stopPreview();
			}
		}


		private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
			final double ASPECT_TOLERANCE = 0.1;
			double targetRatio = (double) w / h;
			if (sizes == null) return null;

			Size optimalSize = null;
			double minDiff = Double.MAX_VALUE;

			int targetHeight = h;

			// Try to find an size match aspect ratio and size
			for (Size size : sizes) {
				double ratio = (double) size.width / size.height;
				if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}

			// Cannot find the one match the aspect ratio, ignore the requirement
			if (optimalSize == null) {
				minDiff = Double.MAX_VALUE;
				for (Size size : sizes) {
					if (Math.abs(size.height - targetHeight) < minDiff) {
						optimalSize = size;
						minDiff = Math.abs(size.height - targetHeight);
					}
				}
			}
			return optimalSize;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			// Now that the size is known, set up the camera parameters and begin
			// the preview.
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			//requestLayout();

			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}

	}
}