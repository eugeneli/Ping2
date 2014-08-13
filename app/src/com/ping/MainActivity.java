package com.ping;

import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.fragments.PingFragment;
import com.ping.interfaces.PingInterface;
import com.ping.models.Ping;
import com.ping.models.PingMap;
import com.ping.models.User;
import com.ping.util.FontTools;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements PingInterface
{	
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private PingMap map;
	private PingApi pingApi;
	private PingPrefs prefs;
	private String authToken;
	private Resources resources;
	
	public static final String BUNDLE_ACTION = "bundle_action";
	public static final String BUNDLE_DATA = "bundle_data";
	
	public Context context = this;
	
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
		
		resources = getResources();
		
		prefs = PingPrefs.getInstance(this);
		authToken = prefs.getAuthToken();
		if(authToken != null)
		{
			pingApi = PingApi.getInstance();
			map = new PingMap(this, R.id.map);
			
			LatLng loc = prefs.getLocation();
			int radius = prefs.getZoom();
			pingApi.getPingsInArea(loc.latitude, loc.longitude, radius*500, new FutureCallback<Response<JsonObject>>(){
				@Override
				public void onCompleted(Exception e, Response<JsonObject> response)
				{
					addPingsToMap(response);
				}
			});
			
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition position)
				{
					prefs.setLocation(position.target);
					prefs.setZoom((int) position.zoom);
					
					pingApi.getPingsInArea(position.target.latitude, position.target.longitude, (int)position.zoom*500, new FutureCallback<Response<JsonObject>>(){
						@Override
						public void onCompleted(Exception e, Response<JsonObject> response)
						{
							addPingsToMap(response);
						}
					});
				}
			});
			
			map.setOnInfoWindowClickedListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker)
				{
					final Ping selectedPing = map.getSelectedPing(marker);
					if(selectedPing != null)
					{
						pingApi.getPingById(selectedPing.getId(), new FutureCallback<Response<JsonObject>>() {
							@Override
							public void onCompleted(Exception e, Response<JsonObject> response)
							{
								JsonObject pingJson = response.getResult().getAsJsonObject(PingApi.RESPONSE);
								JsonArray images = pingJson.get(Ping.IMAGES).getAsJsonArray();
								if(images.size() > 0)
								{
									JsonObject image = images.get(0).getAsJsonObject();
									
									selectedPing.setImageUrlThumb(image.get(Ping.IMAGE_URL_THUMB).getAsString());
									selectedPing.setImageUrlSmall(image.get(Ping.IMAGE_URL_SMALL).getAsString());
									selectedPing.setImageUrlLarge(image.get(Ping.IMAGE_URL_LARGE).getAsString());
								}
								
								FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
								ft.setCustomAnimations(R.anim.zoom_enter, R.anim.zoom_exit, R.anim.zoom_enter, R.anim.zoom_exit);

								PingFragment pingFrag = PingFragment.newInstance(selectedPing);

								ft.replace(R.id.fragmentContainer, pingFrag, "pingFragment");
								ft.addToBackStack(PingFragment.TAG).commit();
							}
							
						});
					}
				}
			});
			
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
						}
					}
					catch(Exception ex)
					{
						Toast.makeText(getApplicationContext(), resources.getString(R.string.connectionFailed), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(context, LoginActivity.class);
						startActivity(intent);
					}
				}
			});
			
			//PingService.scheduleService(this);
		}
		else
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}
	
	private void addPingsToMap(Response<JsonObject> response)
	{
		if(response != null)
		{
			try {
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
				    
				    map.addPingMarker(ping, user.getFullName());
				}
			}
			catch(NullPointerException npe)
			{
				Log.e(TAG, "Ping array response null");
			}
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
				map.addPingMarker(ping, prefs.getCurrentUser().getFullName());
				break;
		}
	}
}
