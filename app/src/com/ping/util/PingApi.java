package com.ping.util;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.ping.models.Ping;

import android.content.Context;

public class PingApi
{
	@SuppressWarnings("unused")
	private final static String TAG = PingApi.class.getSimpleName();

	private final static String PING_AUTHTOKEN_HEADER = "Auth-Token";
	
	public final static String RESPONSE_STATUS = "success";
	public final static String DATA = "data";
	
	public final static String RESPONSE_ERROR_MESSAGE = "message";
	
	public static void getPingsInArea(Context context, String authToken, double latitude, double longitude, int radius,
			FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getPingsInAreaUrl(latitude, longitude, radius))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public static void getPingById(Context context, String authToken, int id, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getPingByIdUrl(id))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public static void postNewPing(Context context, String authToken, Ping ping, FutureCallback<Response<JsonObject>> callback)
	{
		JsonObject json = new JsonObject();
		JsonObject data = new JsonObject();

		data.addProperty(Ping.DURATION, ping.getDuration());
		data.addProperty(Ping.LATITUDE, ping.getLocation().latitude);
		data.addProperty(Ping.LONGITUDE, ping.getLocation().longitude);
		data.addProperty(Ping.TITLE, ping.getTitle());
		data.addProperty(Ping.MESSAGE, ping.getMessage());
		
		json.add(DATA, data);
		
		Ion.with(context)
		.load("POST", PingApiUrls.pingsUrl())
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.setJsonObjectBody(json)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public static void updatePing(Context context, String authToken, Ping ping, FutureCallback<Response<JsonObject>> callback)
	{
		JsonObject json = new JsonObject();
		JsonObject data = new JsonObject();

		data.addProperty(Ping.TITLE, ping.getTitle());
		data.addProperty(Ping.MESSAGE, ping.getMessage());
		
		json.add(DATA, data);
		
		Ion.with(context)
		.load("PUT", PingApiUrls.getPingByIdUrl(ping.getId()))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.setJsonObjectBody(json)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public static void getUser(Context context, String authToken, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.userUrl())
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public static void userLogin(Context context, String authToken, String oAuthProvider, String oAuthAccessToken, 
			FutureCallback<Response<JsonObject>> callback)
	{
		if(authToken != null)
		{
			Ion.with(context)
			.load("POST", PingApiUrls.userLoginUrl())
			.addHeader(PING_AUTHTOKEN_HEADER, authToken)
			.asJsonObject()
			.withResponse()
			.setCallback(callback);
		}
		else
		{
			JsonObject json = new JsonObject();
			JsonObject data = new JsonObject();

			data.addProperty("oauth", oAuthProvider);
			data.addProperty("token", oAuthAccessToken);
			
			json.add(DATA, data);
			
			Ion.with(context)
			.load("POST", PingApiUrls.userLoginUrl())
			.setJsonObjectBody(json)
			.asJsonObject()
			.withResponse()
			.setCallback(callback);
		}
	}
	
	public void getUserById(Context context, String authToken, int id, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getUserByIdUrl(id))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void getUserPingsById(Context context, String authToken, int id, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getUserPingsUrl(id))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
}