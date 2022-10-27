package com.tangledbytes.statussaver;

import android.app.Application;
import android.util.Log;

/**
 * This is invoked before any of the other app components are invoked in app.
 */
public class BaseApplication extends Application {
    private final String TAG = "BaseApplication";
    
    @Override
    public void onCreate() {
	super.onCreate();

	// Handle all unhandled app exceptions here
	final Thread.UncaughtExceptionHandler defHandler = Thread.getDefaultUncaughtExceptionHandler();
	Thread.setDefaultUncaughtExceptionHandler((th, tr) -> {
		try {
		Log.e(TAG, "FATAL EXCEPTION (Thread=" + th.getName() + "):" , tr);
		Log.e(TAG, "Killing app forcefully");
		// Stop looping of app crash  by rethrowing the exception to default handler
		if (defHandler != null)
			defHandler.uncaughtException(th, tr);
		else
			System.exit(2);
		} catch(Throwable throwable) {
		System.exit(2);
		}
	});
    }
}
