package com.ping.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.ping.R;
import com.ping.models.Ping;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

public class PingApi
{
	private final static String TAG = PingApi.class.getSimpleName();
	
	private Context context;
	private String authToken;

	private final static String PING_AUTHTOKEN_HEADER = "Auth-Token";
	private final static String NODE = "node";
	
	public final static String RESPONSE = "response";
	public final static String ERROR = "error";
	public final static String ERROR_DESCRIPTION = "error_description";
	
	public final static String JSON_AUTHTOKEN = "auth_token";
	
	public final static int HTTP_SUCCESS = 200;
	public final static int HTTP_NOT_MODIFIED = 304;
	public final static int HTTP_BAD_REQUEST = 400;
	public final static int HTTP_AUTH_FAIL = 401;
	public final static int HTTP_FORBIDDEN = 403;
	public final static int HTTP_NOT_FOUND = 404;
	public final static int HTTP_UNPROCESSABLE = 422;
	
	private static PingApi instance = null;

	public static PingApi getInstance(Context context, String token)
	{ 
		if(instance == null)
			instance = new PingApi(context, token);
		return instance;
	}
	
	public static PingApi getInstance()
	{
		return instance;
	}
	
	private PingApi(Context c, String token)
	{
		context = c;
		authToken = token;
	}
	
	private PingApi(Context c)
	{
		context = c;
	}
	
	public void setAuthToken(String token)
	{
		authToken = token;
	}
	
	public void getPingsInArea(double latitude, double longitude, int radius, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getPingsInAreaUrl(latitude, longitude, radius))
		.setLogging(TAG, Log.VERBOSE)
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
		JsonObject node = new JsonObject();
		JsonArray images = new JsonArray();
		
		//node.addProperty(Ping.DURATION, ping.getDuration());
		node.addProperty(Ping.LATITUDE, ping.getLocation().latitude);
		node.addProperty(Ping.LONGITUDE, ping.getLocation().longitude);
		node.addProperty(Ping.TITLE, ping.getTitle());
		node.addProperty(Ping.MESSAGE, ping.getMessage());
		
		if(ping.getImage() != null)
		{
			JsonObject image = new JsonObject();
			image.addProperty(Ping.FILENAME, "whydoweneedthis.jpg");
			image.addProperty(Ping.CONTENT_TYPE, "image/jpeg");
			image.addProperty(Ping.IMAGE_DATA, ping.getImage());
			
			images.add(image);
			node.add(Ping.IMAGES, images);
		}
		
		json.add(NODE, node);
		
		Log.d(TAG, json.toString());
		
		Ion.with(context)
		.load("POST", PingApiUrls.pingsUrl())
		.setLogging(TAG, Log.VERBOSE)
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.setJsonObjectBody(json)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void updatePing(String authToken, Ping ping, FutureCallback<Response<JsonObject>> callback)
	{
		JsonObject json = new JsonObject();
		JsonObject node = new JsonObject();

		node.addProperty(Ping.LATITUDE, ping.getLocation().latitude);
		node.addProperty(Ping.LONGITUDE, ping.getLocation().longitude);
		node.addProperty(Ping.TITLE, ping.getTitle());
		node.addProperty(Ping.MESSAGE, ping.getMessage());
		
		json.add(NODE, node);
		
		Ion.with(context)
		.load("PUT", PingApiUrls.getPingByIdUrl(ping.getId()))
		.setLogging(TAG, Log.VERBOSE)
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.setJsonObjectBody(json)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void getUser(FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.userSelfUrl())
		.setLogging(TAG, Log.VERBOSE)
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void userOAuthLogin(String oAuthProvider, String oAuthAccessToken, FutureCallback<Response<JsonObject>> callback)
	{
		JsonObject json = new JsonObject();

		json.addProperty("provider", oAuthProvider);
		json.addProperty("token", oAuthAccessToken);
		
		Ion.with(context)
		.load("POST", PingApiUrls.userLoginUrl())
		.setLogging(TAG, Log.VERBOSE)
		.setJsonObjectBody(json)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void userNormalLogin(String username, String password, FutureCallback<Response<JsonObject>> callback)
	{
		/*JsonObject json = new JsonObject();
		JsonObject data = new JsonObject();

		data.addProperty("username", username);
		data.addProperty("password", password);
		
		json.add(DATA, data);
		
		Ion.with(context)
		.load("POST", PingApiUrls.userLoginUrl())
		.setJsonObjectBody(json)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);*/
	}
	
	public void userLogout(FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("DELETE", PingApiUrls.userLogoutUrl())
		.setLogging(TAG, Log.VERBOSE)
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void getUserById(int id, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getUserByIdUrl(id))
		.setLogging(TAG, Log.VERBOSE)
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public void getUserPingsById(int id, FutureCallback<Response<JsonObject>> callback)
	{
		Ion.with(context)
		.load("GET", PingApiUrls.getUserPingsUrl(id))
		.setLogging(TAG, Log.VERBOSE)
		.addHeader(PING_AUTHTOKEN_HEADER, authToken)
		.asJsonObject()
		.withResponse()
		.setCallback(callback);
	}
	
	public static boolean validResponse(Response<JsonObject> response, Context context, Resources resources)
	{
		if(response == null)
			return false;
		
		switch(response.getHeaders().getResponseCode())
		{
			case HTTP_SUCCESS:
				return true;
			case HTTP_NOT_MODIFIED:
				return true;
			case HTTP_AUTH_FAIL:
				Toast.makeText(context, resources.getString(R.string.authenticationFailed), Toast.LENGTH_SHORT).show();
				return false;
			case HTTP_BAD_REQUEST:
				Toast.makeText(context, resources.getString(R.string.badRequest), Toast.LENGTH_SHORT).show();
				return false;
			case HTTP_NOT_FOUND:
				Toast.makeText(context, resources.getString(R.string.notFound), Toast.LENGTH_SHORT).show();
				return false;
			case HTTP_FORBIDDEN:
				Toast.makeText(context, resources.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
				return false;
			case HTTP_UNPROCESSABLE:
				Toast.makeText(context, resources.getString(R.string.unprocessable), Toast.LENGTH_SHORT).show();
				return false;
			default:
				return false;
		}
	}
}