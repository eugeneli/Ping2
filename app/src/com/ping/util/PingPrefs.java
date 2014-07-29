package com.ping.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PingPrefs
{
	private SharedPreferences prefs;
	private static final String APP_NAME = "com.ping";
	private static final String PING_AUTH_TOKEN_KEY = APP_NAME + ".authToken";
	
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
}
