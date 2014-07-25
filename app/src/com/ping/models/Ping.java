package com.ping.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Ping implements Parcelable
{
	private int id;
	private String title;
	private long creationTime;
	private long duration;
	private double latitude;
	private double longitude;
	private String message;
	
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String CREATED_AT = "createdAt";
	public static final String DURATION = "duration";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String MESSAGE = "message";
	
	public Ping()
	{
		//TODO
	}
	
	public void setId(int pingid) { id = pingid; }
	public int getId() { return id; }
	
	public void setTitle(String t) { title = t; }
	public String getTitle() { return title; }
	
	public void setCreationTime(long time) { creationTime = time; }
	public long getCreationTime() { return creationTime; }
	
	public void setDuration(long dur) { duration = dur; }
	public long getDuration() { return duration; }
	
	public void setMessage(String des) { message = des; }
	public String getMessage() { return message; }
	
	public void setLocation(LatLng loc) { latitude = loc.latitude; longitude = loc.longitude; }
	public LatLng getLocation() { return new LatLng(latitude, longitude); }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(getId());
		dest.writeString(getTitle());
		dest.writeString(getMessage());
		dest.writeLong(getCreationTime());
		dest.writeLong(getDuration());
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}
	
	protected Ping(Parcel in)
	{
        setId(in.readInt());
        setTitle(in.readString());
        setMessage(in.readString());
        setCreationTime(in.readLong());
        setDuration(in.readLong());
        setLocation(new LatLng(in.readDouble(), in.readDouble()));
    }
}
