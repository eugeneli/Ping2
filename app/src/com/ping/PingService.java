 package com.ping;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class PingService extends Service
{
	private final String TAG = PingService.class.getSimpleName();
	
	private PingApi pingApi;
	private PingPrefs prefs;
	
	@Override
    public void onCreate()
	{
        super.onCreate();
        prefs = PingPrefs.getInstance(this);
        pingApi = PingApi.getInstance(this, prefs.getAuthToken());
    }
	
	private void handleIntent(Intent intent)
	{
		Log.i(TAG, "Service started");
		
        // Check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting() || cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isAvailable())
        {
            stopSelf();
            return;
        }
        
		LatLng loc = prefs.getLocation();
		int radius = prefs.getRadius();
		
		pingApi.getPingsInArea(loc.latitude, loc.longitude, radius, new FutureCallback<Response<JsonObject>>() {
			@Override
			public void onCompleted(Exception e, Response<JsonObject> response)
			{
				response.getResult().getAsJsonObject().get("response");
				//TODO: Do something with the pings received
			}
		});
		
	    stopSelf();
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		handleIntent(intent);
	    return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}