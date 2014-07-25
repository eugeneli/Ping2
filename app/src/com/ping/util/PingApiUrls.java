package com.ping.util;

public class PingApiUrls
{
	@SuppressWarnings("unused")
	private final static String TAG = PingApiUrls.class.getSimpleName();
	private final static String SERVER_URL = "";

	private final static String USER_PATH = "/user";
	private final static String USERLOGIN_PATH = "/user/login";
	private final static String PINGS_PATH = "/pings";
	
	public static String getPingsInAreaUrl(double lat, double lng, int radius)
	{
		return SERVER_URL + PINGS_PATH + "?latitude="+ lat +"+&longitude="+ lng +"&radius="+ radius;
	}
	
	public static String getPingByIdUrl(int id)
	{
		return SERVER_URL + PINGS_PATH + '/' + id;
	}
	
	public static String pingsUrl()
	{
		return SERVER_URL + PINGS_PATH;
	}
	
	public static String userUrl()
	{
		return SERVER_URL + USER_PATH;
	}
	
	public static String userLoginUrl()
	{
		return SERVER_URL + USERLOGIN_PATH;
	}
	
	public static String getUserByIdUrl(int id)
	{
		return SERVER_URL + USER_PATH + '/' + id;
	}
	
	public static String getUserPingsUrl(int id)
	{
		return getUserByIdUrl(id) + PINGS_PATH;
	}
}
