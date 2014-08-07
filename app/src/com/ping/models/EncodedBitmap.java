package com.ping.models;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class EncodedBitmap
{
	private Bitmap bitmap;
	private String base64Bitmap;
	
	public EncodedBitmap(Bitmap bmp)
	{ 
		bitmap = bmp;
		base64Bitmap = toBase64(bitmap);
	}
	public EncodedBitmap(String b64Bitmap)
	{ 
		bitmap = fromBase64(b64Bitmap);
		base64Bitmap = b64Bitmap;
	}
	
	public Bitmap getBitmap() { return bitmap; }
	public String getBase64() { return base64Bitmap; }
	
	private String toBase64(Bitmap bmp)
	{
	    ByteArrayOutputStream bitmapBytes = new ByteArrayOutputStream();
	    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bitmapBytes);
	    
	    return Base64.encodeToString(bitmapBytes.toByteArray(), Base64.DEFAULT);
	}
	
	private Bitmap fromBase64(String b64Bitmap)
	{
	    byte[] bitmapBytes = Base64.decode(b64Bitmap, Base64.DEFAULT);
	    
	    return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
	}
}