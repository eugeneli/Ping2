package com.ping;

import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.models.Ping;
import com.ping.models.User;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.app.AlarmManager;
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
        // Check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting())
        {
        	Log.e(TAG, "No data connection. Stopping service.");
            stopSelf();
            return;
        }
        
        Log.i(TAG, "Service started");
        
		LatLng loc = prefs.getLocation();
		int radius = prefs.getZoom();
		
		pingApi.getPingsInArea(loc.latitude, loc.longitude, radius, new FutureCallback<Response<JsonObject>>() {
			@Override
			public void onCompleted(Exception e, Response<JsonObject> response)
			{
				JsonArray pingsJson = response.getResult().getAsJsonArray(PingApi.RESPONSE);
				Log.d(TAG, pingsJson.toString());
				
				for (JsonElement pingJsonData : pingsJson)
				{
					JsonObject pingJson  = pingJsonData.getAsJsonObject();
					JsonObject userJson = pingJson.get(User.USER).getAsJsonObject();
					
					Ping ping = new Ping();
				    User user = new User();
				    
				    ping.fromJson(pingJson, false);
				    user.fromJson(userJson);
				    
				    //Show notification with number of pings?
				}
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
	
	public static void scheduleService(Context context)
	{
	   Intent intent = new Intent(context, PingService.class);
	   PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

	   AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	   alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1000*60, pendingIntent);
	   //Run every minute (1000*60) for testing purposes
	}
}