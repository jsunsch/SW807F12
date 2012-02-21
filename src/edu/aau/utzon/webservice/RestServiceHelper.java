package edu.aau.utzon.webservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

public class RestServiceHelper {
	
	private RestService mBoundService;
	private boolean mIsBound = false;
	private static RestServiceHelper ref;

	private RestServiceHelper()
    {
    }

    public static RestServiceHelper getServiceHelper()
    {
      if (ref == null)
          ref = new RestServiceHelper();
      return ref;
    }
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        mBoundService = ((RestService.RestBinder)service).getService();

	        // Tell the user about this for our demo.
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        mBoundService = null;
	    }
	};

	public void doBindService(Context context) {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    context.bindService(new Intent(context, RestService.class), mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	}

	public void doUnbindService(Context context) {
	    if (mIsBound) {
	        // Detach our existing connection.
	    	context.unbindService(mConnection);
	        mIsBound = false;
	    }
	}
}
