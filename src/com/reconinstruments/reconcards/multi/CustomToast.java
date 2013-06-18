package com.reconinstruments.reconcards.multi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CustomToast {
	
	public static final String TAG = "ReconCardActivity";
	
	/*
	 * It's a new age, Irish toast ;)
	 * Make a toast, and log it [in logcat]
	 */
	public void show(Context context, String text, String tag) {
		int duration = Toast.LENGTH_LONG;

		if( 1 > tag.length() ) {
			tag = TAG;
		}
		
		Log.v(tag, "Toast: " + text);
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
