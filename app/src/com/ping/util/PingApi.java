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
	
	private Context context;
	private String authToken;

	private final static String PING_AUTHTOKEN_HEADER = "Auth-Token";
	
	public final static String RESPONSE_STATUS = "success";
	public final static String RESPONSE_ERROR_MESSAGE = "message";
	public final static String DATA = "data";
	
	private static PingApi instance = null;

	public static PingApi getInstance(Context context, String authToken)
	{ 
		if(instance == null)
			instance = new PingApi(context, authToken);
		return instance;
	}
	
	private PingApi(Context c, String token)
	{
		context = c;
		authToken = token;
	}
	
	public void setAuthToken(String token)
	{
		authToken = token;
	}
	
	public void getPingsInArea(double latitude, double longitude, int radius, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getPingsInAreaUrl(latitude, longitude, radius))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void getPingById(int id, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getPingByIdUrl(id))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void postNewPing(Ping ping, FutureCallback<Response<JsonObject>> callback)
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
	
	public void updatePing(String authToken, Ping ping, FutureCallback<Response<JsonObject>> callback)
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
	
	public void getUser(FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.userUrl())
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void userOAuthLogin(String oAuthProvider, String oAuthAccessToken, FutureCallback<Response<JsonObject>> callback)
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
	
	public void userNormalLogin(String username, String password, FutureCallback<Response<JsonObject>> callback)
	{
		JsonObject json = new JsonObject();
		JsonObject data = new JsonObject();

		data.addProperty("username", username);
		data.addProperty("password", password);
		
		json.add(DATA, data);
		
		Ion.with(context)
		.load("POST", PingApiUrls.userLoginUrl())
		.setJsonObjectBody(json)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void getUserById(int id, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getUserByIdUrl(id))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void getUserPingsById(int id, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getUserPingsUrl(id))
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
}