package com.ping.util;

public class PingApiUrls
{
	@SuppressWarnings("unused")
	private final static String TAG = PingApiUrls.class.getSimpleName();
	private final static String SERVER_URL = "http://192.168.2.215:3000/1";

	private final static String USER_PATH = "/users";
	private final static String USER_ME = "/me";
	private final static String USERLOGIN_PATH = "/login";
	private final static String USERLOGOUT_PATH = "/logout";
	private final static String PINGS_PATH = "/pings";
	
	public static String getPingsInAreaUrl(double lat, double lng, int radius)
	{
		return SERVER_URL + PINGS_PATH + "?latitude="+ lat +"&longitude="+ lng +"&kilometers="+ radius;
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
	
	public static String userSelfUrl()
	{
		return SERVER_URL + USER_PATH + USER_ME;
	}
	
	public static String userLoginUrl()
	{
		return SERVER_URL + USERLOGIN_PATH;
	}
	
	public static String userLogoutUrl()
	{
		return SERVER_URL + USERLOGOUT_PATH;
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
