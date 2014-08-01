package com.ping.models;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ping.MainActivity;
import com.ping.R;
import com.ping.fragments.PingFragment;

public class PingMap
{
	private GoogleMap map;
	private FragmentActivity parentActivity;
	private Map<Marker, Ping> markerToPing = new HashMap<Marker, Ping>();
	
	public PingMap(FragmentActivity activity, int mapId)
	{
		parentActivity = activity;
		map = ((SupportMapFragment) parentActivity.getSupportFragmentManager().findFragmentById(mapId)).getMap();
		
		map.getUiSettings().setZoomControlsEnabled(false);
		map.getUiSettings().setMyLocationButtonEnabled(false);
		
		map.setMyLocationEnabled(false);
		
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker)
			{
				Ping selectedPing = markerToPing.get(marker);
				if(selectedPing != null)
				{
					FragmentTransaction ft = parentActivity.getSupportFragmentManager().beginTransaction();
					ft.setCustomAnimations(R.anim.zoom_enter, R.anim.zoom_exit,R.anim.zoom_enter, R.anim.zoom_exit);

					PingFragment pingFrag = PingFragment.newInstance();
					Bundle bundle = new Bundle();
					bundle.putParcelable(PingFragment.PING_DATA, selectedPing);
					pingFrag.setArguments(bundle);

					ft.replace(R.id.fragmentContainer, pingFrag, "pingFragment");
					ft.addToBackStack(PingFragment.TAG).commit();
				}
			}
		});
	}
	
	public void moveCamera(LatLng loc, int zoom)
	{
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));
	}
	
	public void addPingMarker(Ping ping)
	{
		Marker marker = map.addMarker(new MarkerOptions()
		.title(ping.getTitle())
		.snippet(ping.getMessage())
		.position(ping.getLocation()));
		
		markerToPing.put(marker, ping);
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
