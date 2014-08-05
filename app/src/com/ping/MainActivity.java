package com.ping;

import java.util.Iterator;

import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.interfaces.PingInterface;
import com.ping.models.Ping;
import com.ping.models.PingMap;
import com.ping.util.FontTools;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.Window;

public class MainActivity extends FragmentActivity implements PingInterface
{	
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private PingMap map;
	private PingApi pingApi;
	private PingPrefs prefs;
	private String authToken;
	
	public static final String BUNDLE_ACTION = "bundle_action";
	public static final String BUNDLE_DATA = "bundle_data";
	
	public static class Actions
	{
		public static final int NEW_PING = 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		FontTools.applyFont(this, findViewById(R.id.root));
		
		prefs = PingPrefs.getInstance(this);
		authToken = prefs.getAuthToken();
		if(authToken != null)
		{
			pingApi = PingApi.getInstance(getBaseContext(), authToken);
			map = new PingMap(this, R.id.map);
			
			LatLng loc = prefs.getLocation();
			int radius = prefs.getRadius();
			pingApi.getPingsInArea(loc.longitude, loc.latitude, radius*100, new FutureCallback<Response<JsonObject>>(){
				@Override
				public void onCompleted(Exception e, Response<JsonObject> response)
				{
					JsonArray pingsJson = response.getResult().getAsJsonArray(PingApi.RESPONSE);
					Log.d(TAG, pingsJson.toString());
					Iterator<JsonElement> iterator = pingsJson.iterator();

					while(iterator.hasNext())
					{
					    JsonElement pingJson = (JsonElement) iterator.next();
					    Gson gson = new Gson();
					    Ping ping = gson.fromJson(pingJson, Ping.class);

					    map.addPingMarker(ping);
					}
					
				}
			});
			
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition position)
				{
					prefs.setLocation(position.target);
					prefs.setRadius((int) position.zoom);
					
					pingApi.getPingsInArea(position.target.latitude, position.target.longitude, (int)position.zoom*100, new FutureCallback<Response<JsonObject>>(){
						@Override
						public void onCompleted(Exception e, Response<JsonObject> response)
						{
							JsonArray pingsJson = response.getResult().getAsJsonArray(PingApi.RESPONSE);
							Log.d(TAG, pingsJson.toString());
							Iterator<JsonElement> iterator = pingsJson.iterator();

							while(iterator.hasNext())
							{
							    JsonElement pingJson = (JsonElement) iterator.next();
							    Gson gson = new Gson();
							    Ping ping = gson.fromJson(pingJson, Ping.class);

							    map.addPingMarker(ping);
							}
						}
					});
				}
			});
			
			PingService.scheduleService(this);
		}
		else
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onFragmentResult(Bundle bundle)
	{
		switch(bundle.getInt(BUNDLE_ACTION))
		{
			case Actions.NEW_PING:
				Ping ping = bundle.getParcelable(BUNDLE_DATA);
				map.moveCamera(ping.getLocation(), 13);
				map.addPingMarker(ping);
				break;
		}
	}
}
