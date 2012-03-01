package edu.aau.utzon;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
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
import android.view.ViewGroup;
import android.widget.TextView;

public class AugmentedActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    
    private Preview mPreview;
    //Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;

    // The first rear facing camera
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

    public void onCreate(Bundle saved) {
    	super.onCreate(saved);
    	setContentView(R.layout.augmented);
        
        // Sensor
    	mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        
        // Camera
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Preview(this);
        
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mPreview);
        
    }

    @Override
    protected void onResume() { 
        super.onResume();
        mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    
    @Override
    protected void onPause() {
        super.onPause();
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
    	tv.setText("Azimuth: " + (int)e.values[0] + "\n" +
    				"Pitch: " + (int)e.values[1] + "\n" +
    				"Roll: " + (int)e.values[2]);
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
     Camera.Parameters parameters = mCamera.getParameters();
     parameters.set("orientation", "portrait");
     mCamera.setParameters(parameters);
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

 /*
 @Override
 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
     // We purposely disregard child measurements because act as a
     // wrapper to a SurfaceView that centers the camera preview instead
     // of stretching it.
     final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
     final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
     setMeasuredDimension(width, height);

     if (mSupportedPreviewSizes != null) {
         mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
     }
 }
*/

 /*
 @Override
 protected void onLayout(boolean changed, int l, int t, int r, int b) {
     if (changed && getChildCount() > 0) {
         final View child = getChildAt(0);

         final int width = r - l;
         final int height = b - t;

         int previewWidth = width;
         int previewHeight = height;
         if (mPreviewSize != null) {
             previewWidth = mPreviewSize.width;
             previewHeight = mPreviewSize.height;
         }

         // Center the child SurfaceView within the parent.
         if (width * previewHeight > height * previewWidth) {
             final int scaledChildWidth = previewWidth * height / previewHeight;
             child.layout((width - scaledChildWidth) / 2, 0,
                     (width + scaledChildWidth) / 2, height);
         } else {
             final int scaledChildHeight = previewHeight * width / previewWidth;
             child.layout(0, (height - scaledChildHeight) / 2,
                     width, (height + scaledChildHeight) / 2);
         }
     }
 }
*/
 
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