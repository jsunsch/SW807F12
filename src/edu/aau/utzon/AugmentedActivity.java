package edu.aau.utzon;

import java.io.IOException;
import java.util.List;

import com.google.android.maps.GeoPoint;

import edu.aau.utzon.AugmentedActivity.DrawOnTop;
import edu.aau.utzon.location.LocTool;

import android.app.Activity; 
import android.content.Context; 
import android.graphics.Bitmap;
import android.graphics.Canvas; 
import android.graphics.Color; 
import android.graphics.Paint; 
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera; 
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle; 
import android.util.Log;
import android.view.SurfaceHolder; 
import android.view.SurfaceView; 
import android.view.View; 
import android.view.Window; 
import android.view.ViewGroup.LayoutParams; 
import android.view.WindowManager;
import android.widget.TextView;

public class AugmentedActivity extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mOrientationSensor;

	private Preview mPreview;
	int numberOfCameras;
	int cameraCurrentlyLocked;

	// The first rear facing camera
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private DrawOnTop mDraw;
	
	private LocTool mLocTool;

	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		mLocTool = new LocTool(getApplicationContext());
		mLocTool.onCreate();
		
		// Fullscreen
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.augmented);

		// Sensor
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// Camera
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);

		// Create a RelativeLayout container that will hold a SurfaceView
		mPreview = new Preview(this);
		
		// Create layout for the overlay
		mDraw = new DrawOnTop(this); 
		addContentView(mDraw, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(mPreview);
	}

	class DrawOnTop extends View { 
		private SensorEvent mSensor;
		private float[] mSensorValues;
		
		public void updateOverlay(SensorEvent e)
		{
			mDraw.invalidate();
			mSensorValues = e.values;
		}
		
		public DrawOnTop(Context context) { 
			super(context); 
			// TODO Auto-generated constructor stub 
		}
		
		@Override 
		protected void onDraw(Canvas canvas) { 
			
			// TODO Auto-generated method stub 
			Paint paint = new Paint(); 
			paint.setStyle(Paint.Style.FILL); 
			paint.setColor(Color.GREEN); 
			canvas.drawText("Test Text", 10, 10, paint); 
			
			BitmapDrawable d = (BitmapDrawable) this.getResources().getDrawable(R.drawable.androidmarker);
			Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
			

			// Trigonometry to find angle between 2 points
			// double widthPosition = widthMax - ((widthMax / azimuthMax) * mSensorValues[0]);
			GeoPoint poi = new GeoPoint(17000000,-90000000);
			GeoPoint userLoc = mLocTool.locToGeo(mLocTool.getCurrentLocation());
			
			
			/**
			 *  
			 *  B
			 * 	|\
			 * 	|  \
			 * 	|    \
			 * 	|      \
			 * 	|________\A
			 *  C
			 */
	
			double Ax = userLoc.getLatitudeE6();
			double Ay = userLoc.getLongitudeE6();
			
			double Bx = poi.getLatitudeE6();
			double By = poi.getLongitudeE6();
			
			double Cx = Bx;
			double Cy = Ay;
			
			double BC = Bx - Cx;
			double a = Math.abs(Bx - Ax);
			double b = Math.abs(Ay - By);
			
			double tan = a/b;
			double angle = Math.toDegrees(Math.atan(tan));
			
			// Normalize sensor values to display size
			// SensorValues[0] : 0-360
			// SensorValues[1] : -180 - 180
			// SensorValues[2] : -90 - 90
			double azimuthMax = 360;
			double widthMax = (double)canvas.getWidth();
			
			
			double rollMax = 180;
			double heightMax = (double)canvas.getHeight();
			double sensorValue = mSensorValues[2];
			sensorValue += 90;
			double heightPosition = (heightMax / rollMax ) * sensorValue;
				
			canvas.drawBitmap(bitmap, 50, (int)heightPosition, paint);
			super.onDraw(canvas); 
		} 
	}

	@Override
	protected void onResume() { 
		super.onResume();
		mLocTool.onResume();
		mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}


	@Override
	protected void onPause() {
		super.onPause();
		mLocTool.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		int watizthis = 5;
		watizthis++;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		updateUI(event);

		int everythingwentbetterthanexpected = 5;
		everythingwentbetterthanexpected++;
	}

	private void updateUI(SensorEvent e)
	{
		TextView tv = (TextView)findViewById(R.id.textViewDebug);
		mDraw.updateOverlay(e);
		if(tv != null)
		{
			tv.setText("Azimuth: " + (int)e.values[0] + "\n" +
					"Pitch: " + (int)e.values[1] + "\n" +
					"Roll: " + (int)e.values[2]);
		}
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