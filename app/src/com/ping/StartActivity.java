package com.ping;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.models.User;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class StartActivity extends Activity
{
	public static final String TAG = StartActivity.class.getSimpleName();
	private PingApi pingApi;
	private PingPrefs prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		final Context context = this;
		
		prefs = PingPrefs.getInstance(this);
		if(prefs.getAuthToken() == null)
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		else
		{
			pingApi = PingApi.getInstance(getBaseContext(), prefs.getAuthToken());
			pingApi.getUser(new FutureCallback<Response<JsonObject>>() {
				@Override
				public void onCompleted(Exception e, Response<JsonObject> response)
				{
					try {
						if(response.getHeaders().getResponseCode() == PingApi.HTTP_SUCCESS)
						{
							JsonObject userJson = response.getResult().getAsJsonObject(PingApi.RESPONSE);
							User user = new User();
							user.fromJson(userJson);
							
							prefs.setCurrentUser(user);
							
							Intent intent = new Intent(context, MainActivity.class);
							startActivity(intent);
						}
						else
						{
							Intent intent = new Intent(context, LoginActivity.class);
							startActivity(intent);
						}
					}
					catch(NullPointerException npe)
					{
						Toast.makeText(getApplicationContext(), "Couldn't connect to Ping server", Toast.LENGTH_LONG).show();
						Intent intent = new Intent(context, LoginActivity.class);
						startActivity(intent);
					}
				}
			});
		}
	}
}
