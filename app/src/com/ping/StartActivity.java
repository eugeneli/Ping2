package com.ping;

import com.ping.util.PingPrefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class StartActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		PingPrefs prefs = PingPrefs.getInstance(this);
		if(prefs.getAuthToken() == null)
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		else
		{
			//TODO: Check if token is valid before redirecting to MainActivity. (Login to web api)
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
}
