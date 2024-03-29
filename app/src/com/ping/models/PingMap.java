package com.ping.models;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ping.R;
import com.ping.util.PingPrefs;

public class PingMap
{
	private GoogleMap map;
	private FragmentActivity parentActivity;
	private PingPrefs prefs;
	private Map<Marker, Ping> markerToPing = new HashMap<Marker, Ping>();
	
	public PingMap(FragmentActivity activity, int mapId)
	{
		parentActivity = activity;
		prefs = PingPrefs.getInstance(activity);
		map = ((SupportMapFragment) parentActivity.getSupportFragmentManager().findFragmentById(mapId)).getMap();
		
		map.getUiSettings().setZoomControlsEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(false);
		
		map.setMyLocationEnabled(true);
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(prefs.getLocation(), prefs.getZoom()));
	}
	
	public void setOnInfoWindowClickedListener(OnInfoWindowClickListener ocl)
	{
		map.setOnInfoWindowClickListener(ocl);
	}
	
	public void setOnMapLongClickListener(OnMapLongClickListener omlc)
	{
		map.setOnMapLongClickListener(omlc);
	}
	
	public void setOnCameraChangeListener(OnCameraChangeListener ocl)
	{
		map.setOnCameraChangeListener(ocl);
	}
	
	public void moveCamera(LatLng loc, int zoom)
	{
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
	}
	
	public void addPingMarker(Ping ping, String creatorName)
	{
		Marker marker = map.addMarker(new MarkerOptions()
		.title(ping.getTitle())
		.snippet("posted by: "+ creatorName)
		.position(ping.getLocation())
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
		
		markerToPing.put(marker, ping);
	}
	
	public Ping getSelectedPing(Marker marker)
	{
		return markerToPing.get(marker);
	}
	
	public void demoMapOrigin()
	{
		LatLng nyc = new LatLng(40.706093, -73.949629);
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(nyc, 13));
	}
	
	public void demoMapMarkers()
	{
		LatLng nyc = new LatLng(40.706093, -73.949629);
		if (map!=null)
		{
			map.addMarker(new MarkerOptions()
            .title("NYC")
            .snippet("stuff happens here")
            .position(nyc));
			
			map.addMarker(new MarkerOptions()
            .title("NYC")
            .snippet("stuff happens here")
            .position(new LatLng(40.689856, -73.951449)));
		}
		
		
	}
}
