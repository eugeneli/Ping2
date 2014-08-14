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
import android.widget.Toast;

public class StartActivity extends Activity
{
	public static final String TAG = StartActivity.class.getSimpleName();
	private PingApi pingApi;
	private PingPrefs prefs;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		context = this;
		
		pingApi = PingApi.getInstance(getApplicationContext(), null);
		prefs = PingPrefs.getInstance(this);
		if(prefs.getAuthToken() == null)
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		else
		{
			pingApi.setAuthToken(prefs.getAuthToken());
			pingApi.getUser(new FutureCallback<Response<JsonObject>>() {
				@Override
				public void onCompleted(Exception e, Response<JsonObject> response)
				{
					if(PingApi.validResponse(response, context, getResources()))
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
