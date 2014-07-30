package com.ping.util;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;

public class PingPrefs
{
	private SharedPreferences prefs;
	private static final String APP_NAME = "com.ping";
	private static final String PING_AUTH_TOKEN_KEY = APP_NAME + ".authToken";
	private static final String USER_LATITUDE = APP_NAME + ".latitude";
	private static final String USER_LONGITUDE = APP_NAME + ".longitude";
	private static final String USER_RADIUS = APP_NAME + ".radius";
	
	private static PingPrefs instance = null;

	public static PingPrefs getInstance(Context context)
	{ 
		if(instance == null)
			instance = new PingPrefs(context);
		return instance;
	}
	
	private PingPrefs(Context context)
	{
		prefs = context.getApplicationContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
	}

	public String getAuthToken()
	{
		return prefs.getString(PING_AUTH_TOKEN_KEY, null);
	}
	
	public void setAuthToken(String token)
	{
		prefs.edit().putString(PING_AUTH_TOKEN_KEY, token).commit();
	}
	
	public LatLng getLocation()
	{
		Double latitude = (double) prefs.getFloat(USER_LATITUDE, 0);
		Double longitude = (double) prefs.getFloat(USER_LONGITUDE, 0);
		
		return new LatLng(latitude, longitude);
	}
	
	public void setLocation(LatLng loc)
	{
		prefs.edit().putFloat(USER_LATITUDE, (float) loc.latitude);
		prefs.edit().putFloat(USER_LONGITUDE, (float) loc.longitude).commit();
	}
	
	public int getRadius()
	{
		return prefs.getInt(USER_RADIUS, 0);
	}
	
	public void setRadius(int rad)
	{
		prefs.edit().putInt(USER_RADIUS, rad).commit();
	}
}
