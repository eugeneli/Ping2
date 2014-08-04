package com.ping;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class StartActivity extends Activity
{
	private PingApi pingApi;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		final Context context = this;
		
		PingPrefs prefs = PingPrefs.getInstance(this);
		if(prefs.getAuthToken() == null)
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		else
		{
			pingApi = PingApi.getInstance(this, prefs.getAuthToken());
			pingApi.getUser(new FutureCallback<Response<JsonObject>>() {
				@Override
				public void onCompleted(Exception e, Response<JsonObject> response)
				{
					if(response.getHeaders().getResponseCode() == PingApi.HTTP_SUCCESS)
					{
						Intent intent = new Intent(context, MainActivity.class);
						startActivity(intent);
					}
					else
					{
						Intent intent = new Intent(context, LoginActivity.class);
						startActivity(intent);
					}
				}
			});
		}
	}
}
