package edu.aau.utzon.location;

import edu.aau.utzon.R;
import edu.aau.utzon.location.SampleService.SampleBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

public class SampleActivity extends Activity {
	SampleService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SampleService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Called when a button is clicked (the button in the layout file attaches to
      * this method with the android:onClick attribute) */
    public void onButtonClick(View v) {
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
//            int num = mService.getRandomNumber();
//            Toast.makeText(this, "number: " + num, Toast.LENGTH_SHORT).show();
        	LocationHelper lh = mService.getLocationHelper();
        	Toast.makeText(this, "POIs: " + lh.getPois().size(), Toast.LENGTH_SHORT).show();
        }
    }
    
    public void buttonLocation(View v) {
    	LocationHelper lh = mService.getLocationHelper();
    	if(lh.getCurrentLocation() != null)
    		Toast.makeText(this, "Location: " + lh.getCurrentLocation().getLatitude(), Toast.LENGTH_SHORT).show();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	SampleBinder binder = (SampleBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//
//public class SampleActivity extends Activity {
//	LocationPoller mLocationPoller = null;
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		service = new Intent(this, SampleService.class);
//		mLocationPoller = new LocationPoller();
//		service.putExtra(SampleService.MSG_REGISTER_CLIENT, ms);
//		startService(service);
//
//		 //bind service to the UI **Important**
//		 bindService();
//
//		IntentFilter timerFilter = new IntentFilter(SampleService.INTENTFILTER); // Filter that gets stuff from the service
//		registerReceiver(mLocationPoller, timerFilter);
//	}
//
//public Intent service;
//
//
//
//
//void bindService() {
//        Intent newIntent = new Intent(this, TimerService.class);
//        bindService(newIntent, mConnection, Context.BIND_AUTO_CREATE);
//        mIsBound = true;
//    }
//
//private ServiceConnection mConnection = new ServiceConnection() {
//
//        public void onServiceConnected(ComponentName className, IBinder binder) {
//            s = ((TimerService.MyBinder) binder).getService();
//        }
//
//        public void onServiceDisconnected(ComponentName className) {
//            s = null;
//        }
//    };
//    public void releaseBind(){
//        if(mIsBound)
//        {
//        unbindService(mConnection);
//        mIsBound = false;
//        }
//      }
//
//// Now in this class we need to add in the listener that will update the UI (the receiver registered above)
//
//    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO Auto-generated method stub
//            //Get Bundles
//            Bundle extras = intent.getExtras();
//            /* DO ANY UI UPDATING YOU WANT HERE (set text boxes, etc.) TAKING INFO FROM THE "extras" Bundle ie: setting the clock*/
////ie: int timerTest = extras.getInt("0");
//            // Now update screen with value from timerTest
//        }
//    };
//    }
////
////import android.app.Activity;
////import android.content.ComponentName;
////import android.content.Context;
////import android.content.Intent;
////import android.content.ServiceConnection;
////import android.os.IBinder;
////
////public class SampleActivity extends Activity implements UpdateListener {
////    private LocalService mBoundService;
////
////    private ServiceConnection mConnection = new ServiceConnection() {
////    	@Override
////    	public void onServiceConnected(ComponentName className, IBinder service) {
////            mBoundService = ((LocalService.LocalBinder)service).getService();
////            mBoundService.registerListener(this);
////        }
////
////    	@Override
////        public void onServiceDisconnected(ComponentName className) {
////            mBoundService = null;
////        }
////    };
////
////	private boolean mIsBound = false;
////
////
////    void doBindService() {
////        bindService(new Intent(Binding.this, 
////                LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
////        mIsBound = true;
////    }
////
////
////    void doUnbindService() {
////        if (mIsBound ) {
////            if (mBoundService != null) {
////                mBoundService.unregisterListener(this);
////            }
////            unbindService(mConnection);
////            mIsBound = false;
////        }
////    }
////
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        doUnbindService();
////    }
////
////	@Override
////	public void onUpdate(long value) {
////		// TODO Auto-generated method stub
////		int herpderp;
////		
////	}
////}
//////
//////import java.util.ArrayList;
//////import java.util.List;
//////
//////import android.app.Activity;
//////import android.content.ComponentName;
//////import android.content.Context;
//////import android.content.Intent;
//////import android.content.IntentFilter;
//////import android.content.ServiceConnection;
//////import android.os.Bundle;
//////import android.os.Handler;
//////import android.os.IBinder;
//////import android.os.Message;
//////import android.os.Messenger;
//////import android.os.RemoteException;
//////import android.support.v4.content.LocalBroadcastManager;
//////import android.view.View;
//////import android.widget.TextView;
//////import android.widget.Toast;
//////import edu.aau.utzon.R;
//////
//////public class SampleActivity extends Activity {
//////	/** Messenger for communicating with service. */
//////	Messenger mService = null;
//////	/** Flag indicating whether we have called bind on the service. */
//////	boolean mIsBound;
//////	/** Some text view we are using to show state information. */
//////	//TextView mCallbackText;
//////
//////	/**
//////	 * Handler of incoming messages from service.
//////	 */
//////	class IncomingHandler extends Handler {
//////	    @Override
//////	    public void handleMessage(Message msg) {
//////	        switch (msg.what) {
//////	            case SampleService.MSG_SET_VALUE:
//////	                mCallbackText.setText("Received from service: " + msg.arg1);
//////	                break;
//////	            default:
//////	                super.handleMessage(msg);
//////	        }
//////	    }
//////	}
//////
//////	/**
//////	 * Target we publish for clients to send messages to IncomingHandler.
//////	 */
//////	final Messenger mMessenger = new Messenger(new IncomingHandler());
//////	protected TextView mCallbackText;
//////
//////	/**
//////	 * Class for interacting with the main interface of the service.
//////	 */
//////	private ServiceConnection mConnection = new ServiceConnection() {
//////	    public void onServiceConnected(ComponentName className,
//////	            IBinder service) {
//////	        // This is called when the connection with the service has been
//////	        // established, giving us the service object we can use to
//////	        // interact with the service.  We are communicating with our
//////	        // service through an IDL interface, so get a client-side
//////	        // representation of that from the raw service object.
//////	        mService = new Messenger(service);
//////	        mCallbackText = (TextView) findViewById(R.id.main_text);
//////	        mCallbackText.setText("Attached.");
//////
//////	        // We want to monitor the service for as long as we are
//////	        // connected to it.
//////	        try {
//////	            Message msg = Message.obtain(null,
//////	            		SampleService.MSG_REGISTER_CLIENT);
//////	            msg.replyTo = mMessenger;
//////	            mService.send(msg);
//////
//////	            // Give it some value as an example.
//////	            msg = Message.obtain(null,
//////	            		SampleService.MSG_SET_VALUE, this.hashCode(), 0);
//////	            mService.send(msg);
//////	        } catch (RemoteException e) {
//////	            // In this case the service has crashed before we could even
//////	            // do anything with it; we can count on soon being
//////	            // disconnected (and then reconnected if it can be restarted)
//////	            // so there is no need to do anything here.
//////	        }
//////
//////	        // As part of the sample, tell the user what happened.
//////	        Toast.makeText(Binding.this, R.string.remote_service_connected,
//////	                Toast.LENGTH_SHORT).show();
//////	    }
//////
//////	    public void onServiceDisconnected(ComponentName className) {
//////	        // This is called when the connection with the service has been
//////	        // unexpectedly disconnected -- that is, its process crashed.
//////	        mService = null;
//////	        mCallbackText.setText("Disconnected.");
//////
//////	        // As part of the sample, tell the user what happened.
//////	        Toast.makeText(Binding.this, R.string.remote_service_disconnected,
//////	                Toast.LENGTH_SHORT).show();
//////	    }
//////	};
//////
//////	void doBindService() {
//////	    // Establish a connection with the service.  We use an explicit
//////	    // class name because there is no reason to be able to let other
//////	    // applications replace our component.
//////	    bindService(new Intent(Binding.this, 
//////	    		SampleService.class), mConnection, Context.BIND_AUTO_CREATE);
//////	    mIsBound = true;
//////	    mCallbackText.setText("Binding.");
//////	}
//////
//////	void doUnbindService() {
//////	    if (mIsBound) {
//////	        // If we have received the service, and hence registered with
//////	        // it, then now is the time to unregister.
//////	        if (mService != null) {
//////	            try {
//////	                Message msg = Message.obtain(null,
//////	                		SampleService.MSG_UNREGISTER_CLIENT);
//////	                msg.replyTo = mMessenger;
//////	                mService.send(msg);
//////	            } catch (RemoteException e) {
//////	                // There is nothing special we need to do if the service
//////	                // has crashed.
//////	            }
//////	        }
//////
//////	        // Detach our existing connection.
//////	        unbindService(mConnection);
//////	        mIsBound = false;
//////	        mCallbackText.setText("Unbinding.");
//////	    }
//////	}
//////
//////	public static String ACTION_START = "com.mypackage.START";
//////
//////    private final ArrayList<UpdateListener> mListeners
//////            = new ArrayList<UpdateListener>();
//////    private final Handler mHandler = new Handler();
//////
//////    private long mTick = 0;
//////
//////    private final Runnable mTickRunnable = new Runnable() {
//////        public void run() {
//////            mTick++;
//////            sendUpdate(mTick);
//////            mHandler.postDelayed(mTickRunnable, 1000);
//////        }
//////    };
//////
//////    public void registerListener(UpdateListener listener) {
//////        mListeners.add(listener);
//////    }
//////
//////    public void unregisterListener(UpdateListener listener) {
//////        mListeners.remove(listener);
//////    }
//////
//////    private void sendUpdate(long value) {
//////        for (int i=mListeners.size()-1; i>=0; i--) {
//////            mListeners.get(i).onUpdate(value);
//////        }
//////    }
//////
//////    public int onStartCommand(Intent intent, int flags, int startId) {
//////        if (ACTION_START.equals(intent.getAction()) {
//////            mTick = 0;
//////            mHandler.removeCallbacks(mTickRunnable);
//////            mHandler.post(mTickRunnable);
//////        }
//////        return START_STICKY;
//////    }
//////
//////    public void onDestroy() {
//////        mHandler.removeCallbacks(mTickRunnable);
//////    }
//////}