package com.ping.fragments;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.LoginActivity;
import com.ping.R;
import com.ping.util.FontTools;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment
{	
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	private PingApi pingApi;
	private PingPrefs prefs;
	
	private TextView profile;
	private TextView logout;
	
	public static PingFragment newInstance() 
	{
		PingFragment frag = new PingFragment();
	    return frag;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View view = inflater.inflate(R.layout.fragment_ping, container, false);
        
        profile = (TextView) view.findViewById(R.id.profile);
        logout = (TextView) view.findViewById(R.id.logout);
        
        return view;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		FontTools.applyFont(getActivity(), getActivity().findViewById(R.id.root));
		pingApi = PingApi.getInstance(getActivity());
		prefs = PingPrefs.getInstance(getActivity());
		setupListeners();
	}
	
	private void setupListeners()
	{
		profile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				
			}
		});
		
		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				pingApi.userLogout(new FutureCallback<Response<JsonObject>>() {
					@Override
					public void onCompleted(Exception e, Response<JsonObject> response)
					{
						prefs.setAuthToken(null);
						Toast.makeText(getActivity(), "You have been logged out", Toast.LENGTH_LONG).show();
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						startActivity(intent);
					}
				});
			}
		});
	}
}