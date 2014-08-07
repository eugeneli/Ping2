package com.ping.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

public class Ping implements Parcelable
{
	private int id;
	private int creatorId;
	private String title;
	private String message;
	private String encodedImage;
	private String imageUrlThumb;
	private String imageUrlSmall;
	private String imageUrlLarge;
	//private long creationTime;
	//private long duration;
	private double latitude;
	private double longitude;
	
	//private String authorName;
	private String address;
	
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String CREATED_AT = "createdAt";
	public static final String CREATOR_ID = "user_id";
	public static final String DURATION = "duration";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String MESSAGE = "message";
	public static final String IMAGES = "images";
	public static final String FILENAME = "file_name";
	public static final String CONTENT_TYPE = "content_type";
	public static final String IMAGE_DATA = "data";
	public static final String IMAGE_URL_THUMB = "photo_url_thumb";
	public static final String IMAGE_URL_SMALL = "photo_url_small";
	public static final String IMAGE_URL_LARGE = "photo_url_large";
	
	public Ping()
	{
		//TODO
	}
	
	public static final Parcelable.Creator<Ping> CREATOR = new Parcelable.Creator<Ping>()
	{
	    @Override
	    public Ping createFromParcel(Parcel source) {
	    	return new Ping(source);
	    }

		@Override
		public Ping[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	public void setId(int pingid) { id = pingid; }
	public int getId() { return id; }
	
	public void setTitle(String t) { title = t; }
	public String getTitle() { return title; }
	
	//public void setCreationTime(long time) { creationTime = time; }
	//public long getCreationTime() { return creationTime; }
	
	public void setCreatorId(int cid) { creatorId = cid; }
	public int getCreatorId() { return creatorId; }
	
	//public void setDuration(long dur) { duration = dur; }
	//public long getDuration() { return duration; }
	
	public void setMessage(String des) { message = des; }
	public String getMessage() { return message; }
	
	public void setImage(String b64bmp) { encodedImage = b64bmp; }
	public void setImage(EncodedBitmap bmp) { encodedImage = bmp.getBase64(); }
	public String getImage() { return encodedImage; }
	
	public void setImageUrlThumb(String url) { imageUrlThumb = url; }
	public String getImageUrlThumb() { return imageUrlThumb; }
	public void setImageUrlSmall(String url) { imageUrlSmall = url; }
	public String getImageUrlSmall() { return imageUrlSmall; }
	public void setImageUrlLarge(String url) { imageUrlLarge = url; }
	public String getImageUrlLarge() { return imageUrlLarge; }
	
	public void setLocation(LatLng loc) { latitude = loc.latitude; longitude = loc.longitude; }
	public LatLng getLocation() { return new LatLng(latitude, longitude); }
	
	//public void setAuthorName(String name) { authorName = name; }
	//public String getAuthorName() { return authorName; }
	
	public void setAddress(String add) { address = add; }
	public String getAddress() { return address; }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(getId());
		dest.writeInt(getCreatorId());
		dest.writeString(getTitle());
		dest.writeString(getMessage());
		dest.writeString(getImage());
		dest.writeString(getImageUrlThumb());
		dest.writeString(getImageUrlSmall());
		dest.writeString(getImageUrlLarge());
		//dest.writeLong(getCreationTime());
		//dest.writeLong(getDuration());
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		//dest.writeString(authorName);
		//dest.writeString(address);
	}
	
	protected Ping(Parcel in)
	{
        setId(in.readInt());
        setCreatorId(in.readInt());
        setTitle(in.readString());
        setMessage(in.readString());
        setImage(in.readString());
        setImageUrlThumb(in.readString());
        setImageUrlSmall(in.readString());
        setImageUrlLarge(in.readString());
        //setCreationTime(in.readLong());
        //setDuration(in.readLong());
        setLocation(new LatLng(in.readDouble(), in.readDouble()));
        //setAuthorName(in.readString());
        //setAddress(in.readString());
    }
	
	public void fromJson(JsonObject json, boolean fullPingInfo)
	{
		if(fullPingInfo)
		{
			setId(json.get(ID).getAsInt());
			setCreatorId(json.get(CREATOR_ID).getAsInt());
			setTitle(json.get(TITLE).getAsString());
			setMessage(json.get(MESSAGE).getAsString());
			setImageUrlThumb(json.get(IMAGE_URL_THUMB).getAsString());
	        setImageUrlSmall(json.get(IMAGE_URL_SMALL).getAsString());
	        setImageUrlLarge(json.get(IMAGE_URL_LARGE).getAsString());
			setLocation(new LatLng(json.get(LATITUDE).getAsDouble(), json.get(LONGITUDE).getAsDouble()));
		}
		else
		{
			setId(json.get(ID).getAsInt());
			setCreatorId(json.get(CREATOR_ID).getAsInt());
			setTitle(json.get(TITLE).getAsString());
			setMessage(json.get(MESSAGE).getAsString());
			setLocation(new LatLng(json.get(LATITUDE).getAsDouble(), json.get(LONGITUDE).getAsDouble()));
		}
	}
}
